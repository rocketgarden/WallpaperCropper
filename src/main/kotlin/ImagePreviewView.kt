import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import tornadofx.View
import tornadofx.pane

/**
 * View that just holds the draggable image preview component
 */
class ImagePreviewView : View() {

    companion object {
        const val WIDTH = 1280.0
        const val HEIGHT = 1000.0
    }

    private val controller: ImagePreviewController by inject()

    private val imageView = ImageView().apply {
        fitWidth = WIDTH
        fitHeight = HEIGHT
        isPreserveRatio = true

    }

    private val previewRect = Rectangle(0.0,0.0,100.0,100.0).apply {
        stroke = Color.BLACK
        fill = Color.TRANSPARENT
        strokeWidth = 0.0
    }

    private val topRect = Rectangle()
    private val bottomRect = Rectangle()
    private val leftRect = Rectangle()
    private val rightRect = Rectangle()

    private val originalText = Text().apply {
        layoutX = 10.0
        layoutY = 10.0
    }
    private val scaledText = Text().apply {
        layoutX = 10.0
        layoutY = 30.0
    }

    override val root = pane {
        add(imageView)
        add(previewRect)

        add(topRect)
        add(bottomRect)
        add(leftRect)
        add(rightRect)

//        add(originalText)
//        add(scaledText)

        setOnMouseDragged { controller.onMouseDragged(it) }
        setOnMousePressed { controller.onMouseDragStarted(it) }
        setOnMouseReleased { controller.onMouseDragEnded() }
    }

    init {
        listOf(topRect, bottomRect, leftRect, rightRect).forEach { it.apply {
            fill = Color.gray(.3, .90)
            strokeWidth = 0.0
        } }
    }

    fun loadImage(image: Image?) {
        imageView.image = image
    }

    fun updateViewport(viewport: Rectangle2D) {
        previewRect.width = viewport.width
        previewRect.height = viewport.height
        previewRect.layoutX = viewport.minX
        previewRect.layoutY = viewport.minY

        updateBorderRects(viewport)
    }

    fun fitWindow() {
        currentStage?.sizeToScene()
    }

    private fun updateBorderRects(viewport: Rectangle2D) {
        val width = imageView.layoutBounds.width
        val height = imageView.layoutBounds.height

        leftRect.height = height
        rightRect.height = height

        topRect.width = width
        bottomRect.width = width

        rightRect.width = viewport.minX

        topRect.height = viewport.minY

        leftRect.x = viewport.maxX
        leftRect.width = width - viewport.maxX

        bottomRect.y = viewport.maxY
        bottomRect.height = height - viewport.maxY
    }

    fun setCoordText(orig: String, scaled: String) {
        originalText.text = orig
        scaledText.text = scaled
    }
}