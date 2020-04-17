import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import tornadofx.Controller
import tornadofx.warning
import java.io.File
import java.io.FileInputStream
import kotlin.math.min

class ImagePreviewController : Controller() {

    private val view: ImagePreviewView by inject()

    companion object {
        const val RATIO = 16.0 / 9.0
    }

    private var dragOriginX = 0.0
    private var dragOriginY = 0.0
    private var scaleFactor = 1.0


    private var image: Image? = null
    var viewport: Rectangle2D = Rectangle2D(0.0, 0.0, 0.0, 0.0)
        private set

    fun loadImage(file: File) {
        try {
            val stream = FileInputStream(file)
            val image = Image(stream)
            updateImage(image, buildInitialViewport(image))
        } catch (e: Exception) {
            warning("File error", "Could not open ${file.name}")
        }
    }

    fun clearImage() {
        this.image = null
        view.loadImage(null)
        System.gc()
    }

    fun onMouseDragged(event: MouseEvent) {
        val xOffset = dragOriginX - event.x
        val yOffset = dragOriginY - event.y

        dragOriginX = event.x
        dragOriginY = event.y

        image?.let {
            updateViewport(
                getValidDraggedViewport(
                    it,
                    viewport,
                    xOffset / scaleFactor,
                    yOffset / scaleFactor
                )
            )
        }
    }

    fun onMouseDragStarted(event: MouseEvent) {
        dragOriginX = event.x
        dragOriginY = event.y
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

    private fun updateViewport(viewport: Rectangle2D) {
        this.viewport = viewport
        view.updateViewport(viewport)
    }

    private fun updateImage(image: Image?, viewport: Rectangle2D) {
        this.image = image
        view.loadImage(image)
        updateViewport(viewport)

        scaleFactor = min(ImagePreviewView.WIDTH / viewport.width, ImagePreviewView.HEIGHT / viewport.height)
    }

}