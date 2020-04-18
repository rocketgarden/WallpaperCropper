import javafx.geometry.Rectangle2D

fun Rectangle2D.scale(scaleFactor: Double): Rectangle2D {
    return Rectangle2D(this.minX * scaleFactor, this.minY * scaleFactor, this.width * scaleFactor, this.height * scaleFactor)
}