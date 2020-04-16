import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import tornadofx.Controller
import tornadofx.warning
import java.io.File
import java.io.FileInputStream
import kotlin.math.min

class ImagePreviewController : Controller() {

    companion object {
        const val RATIO = 16.0 / 9.0
    }

    val imageView = ImageView().apply {
        fitWidth = 1280.0
        fitHeight = 720.0
        isPreserveRatio = true

    }

    private lateinit var image: Image
    private var dragOriginX = 0.0
    private var dragOriginY = 0.0
    private var scaleFactor = 1.0

    private var isTall: Boolean = true

    fun loadImage(file: File) {
        try {
            val stream = FileInputStream(file)
            loadImage(Image(stream))
        } catch (e: Exception) {
            warning("File error", "Could not open ${file.name}")
        }
    }

    private fun loadImage(image: Image) {
        imageView.apply { // don't listen for drag before we have an image
            setOnMouseDragged { onMouseDragged(it) }
            setOnMousePressed { onMouseDragStarted(it) }
        }

        this.image = image
        imageView.image = image
        imageView.viewport = buildInitialViewport(image)
    }

    private fun onMouseDragged(event: MouseEvent) {
        val xOffset = dragOriginX - event.x
        val yOffset = dragOriginY - event.y

        dragOriginX = event.x
        dragOriginY = event.y

        imageView.viewport =
            getValidDraggedViewport(image, imageView.viewport, xOffset / scaleFactor, yOffset / scaleFactor)
    }

    private fun onMouseDragStarted(event: MouseEvent) {
        dragOriginX = event.x
        dragOriginY = event.y

        scaleFactor =
            min(imageView.fitWidth / imageView.viewport.width, imageView.fitHeight / imageView.viewport.height)
    }

    private fun getValidDraggedViewport(
        image: Image,
        originalViewport: Rectangle2D,
        desiredXOffset: Double,
        desiredYOffset: Double
    ): Rectangle2D {
        //min x is zero, max x is imagewidth - viewport width
        val minX = 0.0
        val maxX =
            image.width - originalViewport.width // e.g. if viewport and image are same width, zero variance in x is exactly what we want

        val idealX = originalViewport.minX + desiredXOffset

        val minY = 0.0
        val maxY = image.height - originalViewport.height

        val idealY = originalViewport.minY + desiredYOffset

        val actualX = idealX.coerceIn(minX, maxX)
        val actualY = idealY.coerceIn(minY, maxY)

        return Rectangle2D(actualX, actualY, originalViewport.width, originalViewport.height)
    }

    private fun buildInitialViewport(image: Image): Rectangle2D {
        val w = image.width
        val h = image.height
        isTall = w / h <= RATIO

        return if (w / h < RATIO) { //too tall
            val newHeight = w / RATIO

            val topBound = (h - newHeight) / 2

            Rectangle2D(0.0, topBound, w, newHeight)
        } else {
            val newWidth = h * RATIO

            val leftBound = (w - newWidth) / 2

            Rectangle2D(leftBound, 0.0, newWidth, h)
        }
    }

}