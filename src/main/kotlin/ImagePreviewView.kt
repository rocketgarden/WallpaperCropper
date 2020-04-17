import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.View
import tornadofx.vbox

/**
 * View that just holds the draggable image preview component
 */
class ImagePreviewView : View() {

    companion object {
        const val WIDTH = 1280.0
        const val HEIGHT = 720.0
    }

    private val controller: ImagePreviewController by inject()

    private val imageView = ImageView().apply {
        fitWidth = WIDTH
        fitHeight = HEIGHT
        isPreserveRatio = true

        setOnMouseDragged { controller.onMouseDragged(it) }
        setOnMousePressed { controller.onMouseDragStarted(it) }
    }

    override val root = vbox {
        add(imageView)
    }

    fun loadImage(image: Image?) {
        imageView.image = image
    }

    fun updateViewport(viewport: Rectangle2D) {
        imageView.viewport = viewport
    }
}