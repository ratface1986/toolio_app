package ai.toolio.app.toolio.spec

import ai.toolio.app.utils.PhotoPicker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf

import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy
import platform.UIKit.UIImageJPEGRepresentation
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData

class IOSPhotoPicker : PhotoPicker {
    private var currentDelegate: NSObject? = null

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
                val data = image?.let { UIImageJPEGRepresentation(it, 1.0) }
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
}


@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    if (size == 0) return ByteArray(0)

    val byteArray = ByteArray(size)
    val buffer = this.bytes

    buffer?.let {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), buffer, size.convert())
        }
    }

    return byteArray
}