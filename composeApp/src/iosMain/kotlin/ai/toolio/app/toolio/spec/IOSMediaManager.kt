package ai.toolio.app.toolio.spec

import ai.toolio.app.utils.MediaInputManager
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioRecorderDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionAllowBluetooth
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
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
import platform.Foundation.NSDictionary
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.dictionaryWithCapacity
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.Any
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.*
import platform.Foundation.NSError

class IOSMediaManager : MediaInputManager {
    private var currentDelegate: NSObject? = null
    private var recorder: AVAudioRecorder? = null
    private var tempUrl: NSURL? = null

    override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
        val picker = UIImagePickerController()
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        } else {
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
        session.setCategory(
            AVAudioSessionCategoryPlayAndRecord,
            AVAudioSessionCategoryOptionAllowBluetooth or AVAudioSessionCategoryOptionDefaultToSpeaker,
            null
        )
        session.setActive(true, null)

        when (session.recordPermission) {
            AVAudioSessionRecordPermissionDenied -> return
            AVAudioSessionRecordPermissionUndetermined -> {
                session.requestRecordPermission { granted -> if (!granted) return@requestRecordPermission }
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

        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()

            val recorderInstance = AVAudioRecorder(url, settings, errorPtr.ptr)

            if (errorPtr.value != null) {
                println("❌ Ошибка инициализации AVAudioRecorder: ${errorPtr.value?.localizedDescription}")
                return
            }

            recorder = recorderInstance
            if (recorder?.prepareToRecord() == true) {
                recorder?.record()
                println("🎙️ Началась запись: ${url.absoluteString}")
            } else {
                println("⚠️ Не удалось подготовить AVAudioRecorder")
            }
        }

    }

    @OptIn(ExperimentalForeignApi::class)
    override fun stopRecording(onResult: (ByteArray?) -> Unit) {
        recorder?.stop()
        recorder = null

        val data = tempUrl?.let { NSData.dataWithContentsOfURL(it) }
        onResult(data?.toByteArray())

        AVAudioSession.sharedInstance().setActive(false, null)

        tempUrl?.let {
            try { NSFileManager.defaultManager.removeItemAtURL(it, null) } catch (_: Throwable) {}
        }
    }

    private fun getTempAudioFileUrl(): NSURL {
        val tempDir = NSTemporaryDirectory()
        val filename = "recording_${NSUUID.UUID().UUIDString}.m4a"
        val path = "$tempDir/$filename"
        return NSURL.fileURLWithPath(path)
    }

}

fun Map<Any?, Any>.toNSDictionary(): NSDictionary {
    val dict = NSMutableDictionary.dictionaryWithCapacity(this.size.toULong())
    for ((key, value) in this) {
        if (key is String) {
            dict.setObject(value, forKey = key as NSString)
        } else if (key is NSString) {
            dict.setObject(value, forKey = key)
        }
    }
    return dict
}