import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import tornadofx.Controller
import tornadofx.warning
import java.awt.Rectangle
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
    private var viewport: Rectangle2D = Rectangle2D(0.0,0.0,0.0,0.0)

    val cropRect: Rectangle
        get() = viewport.toIntRect() // reverse the scaling back to full size when we crop

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
        val xDelta = (event.x - dragOriginX)/scaleFactor
        val yDelta = (event.y - dragOriginY)/scaleFactor

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

    fun onMouseDragEnded() {
        updateViewport(viewport.round()) //snap the viewport to actual pixel boundaries when drag ends
    }

    // Function to make sure the viewport doesn't go outside the bounds of the image
    private fun getValidDraggedViewport(
        image: Image,
        originalViewport: Rectangle2D,
        desiredXOffset: Double,
        desiredYOffset: Double
    ): Rectangle2D {
        val minX = 0.0
        val maxX = image.width - originalViewport.width
        // e.g. if viewport and image preview are same width, zero variance in x is exactly what we want

        val idealX = originalViewport.minX + desiredXOffset

        val minY = 0.0
        val maxY = image.height - originalViewport.height

        val idealY = originalViewport.minY + desiredYOffset

        val actualX = idealX.coerceIn(minX, maxX)
        val actualY = idealY.coerceIn(minY, maxY)

        return Rectangle2D(actualX, actualY, originalViewport.width, originalViewport.height)
    }

    private fun buildInitialViewport(image: Image): Rectangle2D {
        val imageWidth = image.width
        val imageHeight = image.height

        scaleFactor = min(ImagePreviewView.WIDTH / image.width, ImagePreviewView.HEIGHT / image.height)

        return if (imageWidth / imageHeight < RATIO) { //too tall
            val previewHeight = imageWidth / RATIO
            val yStart = (imageHeight - previewHeight)/2 // start preview centered, not topmost/leftmost
            Rectangle2D(0.0, yStart, imageWidth, previewHeight)
        } else {
            val previewWidth = imageHeight * RATIO
            val xStart = (imageWidth - previewWidth)/2
            Rectangle2D(xStart, 0.0, previewWidth, imageHeight)
        }
    }

    private fun updateViewport(viewport: Rectangle2D) {
        this.viewport = viewport
        val scaledViewport = viewport.scale(scaleFactor)
        view.updateViewport(scaledViewport)

        view.setCoordText("${viewport.minX}, ${viewport.minY}", "${scaledViewport.minX}, ${scaledViewport.minY}")
    }

    private fun updateImage(image: Image?, viewport: Rectangle2D) {
        this.image = image
        view.loadImage(image)

        updateViewport(viewport)

        view.fitWindow()
    }

}