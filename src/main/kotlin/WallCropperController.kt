import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.control.Button
import javafx.scene.text.Text
import tornadofx.*
import java.io.File
import java.util.concurrent.TimeUnit

class WallCropperController : Controller() {

    val cropper: ImageFileCropper by inject()
    val previewController: ImagePreviewController by inject()

    private var browseDir: File? = null
    private var fileList = emptyList<File>()
    private var index = 0

    private val currentFile: File?
        get() = if (index < fileList.size) fileList[index] else null

    val browseDirText = Text("Not selected")
    val outputDirText = Text("Not selected")

    val browseButton = Button().apply {
        text = "Choose Browse Directory"
        action { pickBrowseDirectory() }
    }

    val outputButton = Button().apply {
        text = "Choose Browse Directory"
        action { pickOutputDirectory() }
    }

    val skipButton = Button().apply {
        text = "Skip"
        actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe { skipButtonPress() }
        style {
            padding = box(5.px)
        }
    }

    val cropButton = Button().apply {
        text = "Crop"
        actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe { cropButtonPress() }
        style {
            padding = box(5.px, 30.px)
        }
    }

    val trashButton = Button().apply {
        text = "Trash"
        actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe { trashButtonPress() }
        style {
            padding = box(5.px)
        }
    }

    init {
        onBrowseDirectoryChosen(File(".\\picdir"))
        onOutputDirectoryChosen(File(".\\testout"))
    }

    private fun nextImage() {
        index = (index + 1)
        if (index >= fileList.size) {
            onBrowseDirectoryChosen(browseDir)
        }
        currentFile?.let { previewController.loadImage(it) }
        System.gc()
    }

    private fun skipButtonPress() {
        currentFile?.let {
            nextImage()
            cropper.setAsideImage(it)
        }
    }

    private fun trashButtonPress() {
        currentFile?.let {
            nextImage()
            cropper.trashImage(it)
        }
    }

    private fun cropButtonPress() {
        currentFile?.let {
            val cropRect = previewController.imageView.viewport.copy()
            nextImage()
            cropper.cropImage(it, cropRect)
        }
    }


    private fun pickOutputDirectory() {
        var dir: File? = null
        if (fileList.isNotEmpty()) {
            dir = fileList[0].parentFile
        }
        onOutputDirectoryChosen(chooseDirectory(initialDirectory = dir))
    }

    private fun onOutputDirectoryChosen(file: File?) {
        file?.let {
            cropper.outputDir = it
            outputDirText.text = it.path
        }
    }

    private fun pickBrowseDirectory() {
        var dir: File? = null
        if (fileList.isNotEmpty()) {
            dir = fileList[0].parentFile
        }
        onBrowseDirectoryChosen(chooseDirectory(initialDirectory = dir))
    }

    private fun onBrowseDirectoryChosen(file: File?) {
        browseDir = file
        file?.let {
            browseDirText.text = it.path
        }
        file?.listFiles(::isValidImage)?.let {
            if (it.isNotEmpty()) {
                fileList = it.toList()
                index = 0
                previewController.loadImage(fileList[0])
                println("Loaded ${fileList[0].name}")
            } else {
                warning("No images", "No images found")
            }
        }
    }

    private fun isValidImage(file: File): Boolean {
        return file.isFile &&
                (file.name.endsWith(".jpg", true)
                        || file.name.endsWith(".png")
                        || file.name.endsWith(".jpeg"))
    }
}