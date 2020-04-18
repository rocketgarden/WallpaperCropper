import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import tornadofx.Controller
import tornadofx.warning
import java.io.File
import java.io.FileInputStream
import kotlin.math.min

/**
 * Controller for draggable image preview
 */
class ImagePreviewController : Controller() {

    private val view: ImagePreviewView by inject()

    companion object {
        const val RATIO = 16.0 / 9.0
    }

    private var dragOriginX = 0.0
    private var dragOriginY = 0.0
    private var scaleFactor = 1.0

    private var stream: FileInputStream? = null
    private var image: Image? = null
    private var viewport: Rectangle2D = Rectangle2D(0.0, 0.0, 0.0, 0.0)

    val cropRect: Rectangle2D
        get() = viewport.scale(1/scaleFactor) // reverse the scaling back to full size when we crop

    fun loadImage(file: File) {
        try {
            stream = FileInputStream(file) // save this so we can close it
            val image = Image(stream)
            updateImage(image, buildInitialViewport(image))
        } catch (e: Exception) {
            warning("File error", "Could not open ${file.name}")
        }
    }

    fun clearImage() {
        this.image = null
        view.loadImage(null)
        stream?.close()
    }

    fun onMouseDragged(event: MouseEvent) {
        val xDelta = event.x - dragOriginX
        val yDelta = event.y - dragOriginY

        dragOriginX = event.x
        dragOriginY = event.y

        image?.let {
            updateViewport(
                getValidDraggedViewport(
                    it,
                    viewport,
                    xDelta,
                    yDelta
                )
            )
        }
    }

    fun onMouseDragStarted(event: MouseEvent) {
        dragOriginX = event.x
        dragOriginY = event.y
    }

    // Function to make sure the viewport doesn't go outside the bounds of the image
    private fun getValidDraggedViewport(
        image: Image,
        originalViewport: Rectangle2D,
        desiredXOffset: Double,
        desiredYOffset: Double
    ): Rectangle2D {
        val minX = 0.0
        val maxX = image.width*scaleFactor - originalViewport.width
        // e.g. if viewport and image preview are same width, zero variance in x is exactly what we want

        val idealX = originalViewport.minX + desiredXOffset

        val minY = 0.0
        val maxY = image.height*scaleFactor - originalViewport.height

        val idealY = originalViewport.minY + desiredYOffset

        val actualX = idealX.coerceIn(minX, maxX)
        val actualY = idealY.coerceIn(minY, maxY)

        return Rectangle2D(actualX, actualY, originalViewport.width, originalViewport.height)
    }

    private fun buildInitialViewport(image: Image): Rectangle2D {
        val w = image.width
        val h = image.height

        scaleFactor = min(ImagePreviewView.WIDTH / image.width, ImagePreviewView.HEIGHT / image.height)
        println("scaleFactor: %.2f".format(scaleFactor))

        return if (w / h < RATIO) { //too tall
            val width = w * scaleFactor
            val height = width / RATIO
            Rectangle2D(0.0, 0.0, width, height)
        } else {
            val height = h * scaleFactor
            val width = height * RATIO
            Rectangle2D(0.0, 0.0, width, height)
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

        view.fitWindow()
    }

}