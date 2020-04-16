import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent

class ImagePreviewController {

    companion object {
        const val RATIO = 16.0 / 9.0
    }

    private lateinit var image: Image
    var dragOriginX = 0.0
    var dragOriginY = 0.0
    var scaleFactor = 1.0

    var originalViewport = Rectangle2D(0.0, 0.0, 0.0, 0.0)

    var isTall: Boolean = true

    val imageView = ImageView().apply {
        fitWidth = 1280.0
        fitHeight = 720.0
        isPreserveRatio = true
    }

    fun onMouseDragged(event: MouseEvent) {
        var xOffset = dragOriginX - event.x
        var yOffset = dragOriginY - event.y

        imageView.viewport = getValidDraggedViewport(image, originalViewport, xOffset / scaleFactor, yOffset / scaleFactor)
    }

    fun onMouseDragStarted(event: MouseEvent) {
        dragOriginX = event.x
        dragOriginY = event.y

        originalViewport = imageView.viewport.copy()

        scaleFactor = Math.min(imageView.fitWidth/originalViewport.width, imageView.fitHeight/originalViewport.height)
    }

    fun loadImage(image: Image) {
        imageView.apply {
            setOnMouseDragged { onMouseDragged(it) }
            setOnMousePressed { onMouseDragStarted(it) }
        }

        this.image = image
        imageView.image = image
        imageView.viewport = buildInitialViewport(image)
    }

    private fun getValidDraggedViewport(image: Image, originalViewport: Rectangle2D, desiredXOffset: Double, desiredYOffset: Double): Rectangle2D{
        //min x is zero, max x is imagewidth - viewport width
        val minX = 0.0
        val maxX = image.width - originalViewport.width // if viewport and image are same width, zero variance in x is exactly what we want

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

        val ret = if (w / h < RATIO) { //too tall
            val newHeight = w / RATIO

            val topBound = (h - newHeight) / 2

            Rectangle2D(0.0, topBound, w, newHeight)
        } else {
            val newWidth = h * RATIO

            val leftBound = (w - newWidth) / 2

            Rectangle2D(leftBound, 0.0, newWidth, h)
        }

        return ret
    }

}