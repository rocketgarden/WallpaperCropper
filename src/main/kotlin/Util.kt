import javafx.geometry.Rectangle2D
import java.awt.Rectangle
import kotlin.math.roundToInt

fun Rectangle2D.scale(scaleFactor: Double): Rectangle2D {
    return Rectangle2D(
        (this.minX * scaleFactor),
        (this.minY * scaleFactor),
        this.width * scaleFactor,
        this.height * scaleFactor
    )
}
fun Rectangle2D.round(): Rectangle2D {
    return Rectangle2D(
        (this.minX).roundToInt().toDouble(),
        (this.minY).roundToInt().toDouble(),
        this.width,
        this.height
    )
}

fun Rectangle2D.toIntRect(): Rectangle {
    return Rectangle(
        this.minX.roundToInt(),
        this.minY.roundToInt(),
        this.width.roundToInt(),
        this.height.roundToInt()
    )
}