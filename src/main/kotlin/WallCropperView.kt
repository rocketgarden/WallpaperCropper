import com.github.thomasnield.rxkotlinfx.actionEvents
import io.reactivex.rxjavafx.observables.JavaFxObservable
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.text.Text
import tornadofx.*
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit


class WallCropperView : View() {

    private var browseDir: File? = null
    var fileList = emptyList<File>()
    var index = 0

    var controller: ImagePreviewController = ImagePreviewController()
    var cropper: ImageFileCropper = ImageFileCropper()

    val browseDirText = Text("Not selected")
    val outputDirText = Text("Not selected")

    val currentFile: File?
        get() = if (index < fileList.size) fileList[index] else null


    override val root = vbox {
        spacing = 10.0

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER
            button {
                text = "Choose Browse Directory"
                action { pickBrowseDirectory() }
            }

            add(browseDirText)
        }

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER
            button {
                text = "Choose Output Directory"
                action { pickOutputDirectory() }
            }

            add(outputDirText)

        }

        add(controller.imageView)

        hbox {
            style {
                padding = box(30.px, 5.px)
            }
            spacing = 50.0
            alignment = Pos.BASELINE_CENTER
            button {
                text = "Skip"
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe( ::skipButtonPress )
                style {
                    padding = box(5.px)
                }
            }

            button {
                text = "Crop"
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe( ::cropButtonPress )
                style {
                    padding = box(5.px, 30.px)
                }

                shortcut(KeyCodeCombination(KeyCode.SPACE))
            }

            button {
                text = "Trash"
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).subscribe( ::trashButtonPress )
                style {
                    padding = box(5.px)
                }
            }
        } //set aside, trash, crop
    }

    init{
        onBrowseDirectoryChosen(File(".\\picdir"))
        onOutputDirectoryChosen(File(".\\testout"))
    }

    fun skipButtonPress(actionEvent: ActionEvent) {
        currentFile?.let{
            nextImage()
            cropper.setAsideImage(it)
        }
    }

    fun trashButtonPress(actionEvent: ActionEvent) {
        currentFile?.let{
            nextImage()
            cropper.trashImage(it)
        }
    }

    fun cropButtonPress(actionEvent: ActionEvent) {
        currentFile?.let{
            val cropRect = controller.imageView.viewport.copy()
            nextImage()
            cropper.cropImage(it, cropRect)
        }
    }

    fun nextImage() {
        index = (index + 1)
        if(index >= fileList.size) {
            onBrowseDirectoryChosen(browseDir)
        }
        currentFile?.let { showImage(it) }
    }

    fun showImage(file: File) {
        try {
            controller.loadImage(Image(FileInputStream(file)))
        } catch (e: Exception) {
            warning("File error", "Could not open ${file.name}")
        }
    }

    fun pickOutputDirectory() {
        var dir: File? = null
        if (fileList.isNotEmpty()) {
            dir = fileList[0].parentFile
        }
        onOutputDirectoryChosen(chooseDirectory(initialDirectory = dir))
    }

    fun onOutputDirectoryChosen(file: File?){
        file?.let {
            cropper.outputDir = it
            outputDirText.text = it.path
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
            browseDirText.text = it.path
        }
        file?.listFiles(::isValidImage)?.let {
            if (it.isNotEmpty()) {
                fileList = it.toList()
                index = 0
                showImage(fileList[0])
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

