import tornadofx.Controller
import tornadofx.chooseDirectory
import tornadofx.warning
import java.io.File

class WallCropperController : Controller() {

    private val view: WallCropperView by inject()
    private val previewController: ImagePreviewController by inject()
    private val cropper: ImageFileCropper = ImageFileCropper()

    private var browseDir: File? = null
    private var fileList = emptyList<File>()
    private var index = 0

    private val currentFile: File?
        get() = if (index < fileList.size) fileList[index] else null


    private fun nextImage() {
        index = (index + 1)
        if (index >= fileList.size) {
            onBrowseDirectoryChosen(browseDir)
        }
        loadFile(index)
    }

    fun skipButtonPress() {
        currentFile?.let {
            previewController.clearImage()
            cropper.setAsideImage(it)
            nextImage()
        }
    }

    fun trashButtonPress() {
        currentFile?.let {
            previewController.clearImage()
            cropper.trashImage(it)
            nextImage()
        }
    }

    fun cropButtonPress() {
        currentFile?.let {
            val cropRect = previewController.cropRect
            previewController.clearImage()
            cropper.cropImage(it, cropRect)
            nextImage()
        }
    }

    fun pickOutputDirectory() {
        var dir: File? = null
        if (fileList.isNotEmpty()) {
            dir = fileList[0].parentFile
        }
        onOutputDirectoryChosen(chooseDirectory(initialDirectory = dir))
    }

    fun onOutputDirectoryChosen(file: File?) {
        file?.let {
            cropper.outputDir = it
            view.updateOutputDirText(it.path)
        }
    }

    fun pickBrowseDirectory() {
        var dir: File? = null
        if (fileList.isNotEmpty()) {
            dir = fileList[0].parentFile
        }
        onBrowseDirectoryChosen(chooseDirectory(initialDirectory = dir))
    }

    fun onBrowseDirectoryChosen(file: File?) {
        browseDir = file
        file?.let {
            view.updateBrowseDirText(it.path)
        }
        file?.listFiles(::isValidImage)?.let {
            if (it.isNotEmpty()) {
                fileList = it.toList()
                index = 0
                loadFile(index)
//                println("Loaded ${fileList[0].name}")
            } else {
                warning("No images", "No images found")
            }
        }
    }

    private fun loadFile(index: Int) {
        currentFile?.let { previewController.loadImage(it) }
        view.updateWindowTitle("[${index + 1}/${fileList.size}] ${currentFile?.name}")
    }

    private fun isValidImage(file: File): Boolean {
        return file.isFile &&
                (file.name.endsWith(".jpg", true)
                        || file.name.endsWith(".png")
                        || file.name.endsWith(".jpeg"))
    }
}