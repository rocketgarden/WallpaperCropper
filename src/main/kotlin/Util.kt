import io.reactivex.rxjavafx.observables.JavaFxObservable
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import java.awt.Rectangle

fun Rectangle2D.copy(): Rectangle2D {
    return Rectangle2D(this.minX, this.minY, this.width, this.height)
}

fun Rectangle2D.offset(x: Double, y: Double): Rectangle2D {
    return Rectangle2D(this.minX + x, this.minY + y, this.width, this.height)
}

fun Rectangle2D.toRect(): Rectangle {
    return Rectangle(this.minX.toInt(), this.minY.toInt(), this.width.toInt(), this.height.toInt())
}