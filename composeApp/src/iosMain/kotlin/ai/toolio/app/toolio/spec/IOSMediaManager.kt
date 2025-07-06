package ai.toolio.app.toolio.spec

import ai.toolio.app.utils.MediaInputManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioRecorderDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.Any

class IOSMediaManager : MediaInputManager {
    private var currentDelegate: NSObject? = null
    private var recorder: AVAudioRecorder? = null
    private var tempUrl: NSURL? = null

    override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
        val picker = UIImagePickerController()
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        } else {
            // Fallback to photo library in Simulator (or if no camera available)
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }
        picker.allowsEditing = false

        class Delegate(
            val onPhotoPicked: (ByteArray?) -> Unit
        ) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                @Suppress("UNCHECKED_CAST")
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                val compressionQuality = 0.5 // 50% JPEG
                val data = image?.let { UIImageJPEGRepresentation(it, compressionQuality) }
                val byteArray = data?.toByteArray()
                onPhotoPicked(byteArray)
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                onPhotoPicked(null)
                picker.dismissViewControllerAnimated(true, null)
            }
        }

        val delegate = Delegate(onResult)
        currentDelegate = delegate
        picker.delegate = delegate

        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootController?.presentViewController(picker, animated = true, completion = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun startRecording() {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayAndRecord, null)
        session.setActive(true, null)

        val settings = mapOf<Any?, Any>(
            AVFormatIDKey to kAudioFormatMPEG4AAC,
            AVSampleRateKey to 12000.0,
            AVNumberOfChannelsKey to 1,
            AVEncoderAudioQualityKey to AVAudioQualityHigh
        )

        val url = getTempAudioFileUrl()
        tempUrl = url

        recorder = AVAudioRecorder(url, settings, null)
        recorder?.prepareToRecord()
        recorder?.record()
    }

    override fun stopRecording(onResult: (ByteArray?) -> Unit) {
        recorder?.stop()
        recorder = null

        val data = tempUrl?.let { NSData.dataWithContentsOfURL(it) }
        onResult(data?.toByteArray())
    }

    private fun getTempAudioFileUrl(): NSURL {
        val tempDir = NSTemporaryDirectory()
        val path = "$tempDir/recording.m4a"
        return NSURL.fileURLWithPath(path)
    }
}