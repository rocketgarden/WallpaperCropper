import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.text.Text
import tornadofx.*
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Primary view for main screen of app.
 */
class WallCropperView : View() {

    private val mainController: WallCropperController by inject()

    private lateinit var browseDirButton: Button
    private lateinit var outputDirButton: Button

    private lateinit var browseDirText: Text
    private lateinit var outputDirText: Text

    private lateinit var skipDirButton: Button
    private lateinit var cropDirButton: Button
    private lateinit var trashDirButton: Button

    override val root = vbox {
        spacing = 10.0

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER

            browseDirButton = button {
                text = "Browse Directory"
                action { mainController.pickBrowseDirectory() }
            }
            browseDirText = text("Not selected")
        }

        hbox {
            spacing = 10.0
            alignment = Pos.BASELINE_CENTER

            outputDirButton = button {
                text = "Output Directory"
                action { mainController.pickOutputDirectory() }
            }
            outputDirText = text("Not selected")
        }

        hbox {
            style {
                padding = box(30.px, 5.px)
            }
            spacing = 50.0
            alignment = Pos.BASELINE_CENTER

            trashDirButton = button {
                text = "Trash"
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).observeOnFx().subscribe { mainController.trashButtonPress() }
                style {
                    padding = box(5.px)
                }
            }

            cropDirButton = button {
                text = "Crop"
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).observeOnFx().subscribe { mainController.cropButtonPress() }
                style {
                    padding = box(5.px, 30.px)
                }
                shortcut(KeyCodeCombination(KeyCode.SPACE))
            }

            skipDirButton = button {
                text = "Skip"
                //debounce so we don't just silently skip an image on a misclick
                actionEvents().debounce(200, TimeUnit.MILLISECONDS).observeOnFx().subscribe { mainController.skipButtonPress() }
                style {
                    padding = box(5.px)
                }
            }
        }

        add(find(ImagePreviewView::class))
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        mainController.init()
    }

    fun updateBrowseDirText(text: String) {
        browseDirText.text = text
    }

    fun updateOutputDirText(text: String) {
        outputDirText.text = text
    }

    fun updateWindowTitle(text: String) {
        title = text
    }
}

