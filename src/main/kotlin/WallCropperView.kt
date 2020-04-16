import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.text.Text
import tornadofx.*
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit


class WallCropperView : View() {

    val mainController: WallCropperController = WallCropperController()
    val previewController: ImagePreviewController = ImagePreviewController()

    override val root = vbox {
        spacing = 10.0

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER

            add(mainController.browseButton)
            add(mainController.browseDirText)
        }

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER

            add(mainController.outputButton)
            add(mainController.outputDirText)
        }

        add(previewController.imageView)

        hbox {
            style {
                padding = box(30.px, 5.px)
            }
            spacing = 50.0
            alignment = Pos.BASELINE_CENTER

            add(mainController.skipButton)

            add(mainController.cropButton.apply {
                shortcut(KeyCodeCombination(KeyCode.SPACE))
            })

            add(mainController.trashButton)
        }
    }
}

