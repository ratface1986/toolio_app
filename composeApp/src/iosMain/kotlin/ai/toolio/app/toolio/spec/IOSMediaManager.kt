package ai.toolio.app.toolio.spec

import ai.toolio.app.utils.MediaInputManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioRecorderDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
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
        // Включаем запись + вывод на динамик, разрешаем BT
        session.setCategory(
            AVAudioSessionCategoryPlayAndRecord,
            0x4u or 0x8u, // AllowBluetooth | DefaultToSpeaker
            null
        )
        session.setActive(true, null)

        // Проверка пермишена
        when (session.recordPermission) {
            AVAudioSessionRecordPermissionDenied -> {
                // нет доступа к микрофону
                return
            }
            AVAudioSessionRecordPermissionUndetermined -> {
                session.requestRecordPermission { granted ->
                    if (!granted) return@requestRecordPermission
                }
            }
            else -> {}
        }

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

    @OptIn(ExperimentalForeignApi::class)
    override fun stopRecording(onResult: (ByteArray?) -> Unit) {
        recorder?.stop()
        recorder = null

        val data = tempUrl?.let { NSData.dataWithContentsOfURL(it) }
        onResult(data?.toByteArray())

        // очищаем файл и освобождаем сессию
        tempUrl?.let {
            try {
                NSFileManager.defaultManager.removeItemAtURL(it, null)
            } catch (_: Throwable) {}
        }
        AVAudioSession.sharedInstance().setActive(false, null)
    }

    private fun getTempAudioFileUrl(): NSURL {
        val tempDir = NSTemporaryDirectory()
        val filename = "recording_${NSUUID.UUID().UUIDString}.m4a"
        val path = "$tempDir/$filename"
        return NSURL.fileURLWithPath(path)
    }

}