import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import javafx.geometry.Rectangle2D
import tornadofx.runAsync
import tornadofx.ui
import java.io.File
import kotlin.math.roundToInt

class ImageFileCropper {

    var outputDir: File = File("./")

    companion object {
        const val LATER_DIR = "skipped"
        const val TRASH_DIR = "trash"
        const val BACKUP_DIR = "backup"
        const val ERROR_DIR = "backup"
        const val CROPPED_DIR = "cropped"
    }

    fun setAsideImage(file: File): Boolean {
        return moveFile(file, outputDir, LATER_DIR)
    }

    fun trashImage(file: File): Boolean {
        return moveFile(file, outputDir, TRASH_DIR)
    }

    private fun errorImage(file: File): Boolean {
        return moveFile(file, outputDir, ERROR_DIR)
    }

    private fun backupImage(file: File): Boolean {
        return moveFile(file, outputDir, BACKUP_DIR)
    }

    fun cropImage(file: File, rect: Rectangle2D) {
        val jpgName = file.name.substringBeforeLast(".") + ".jpg"

        val outFile = File(outputDir, "$CROPPED_DIR\\$jpgName")
        outFile.parentFile.mkdirs()
        println("Cropping to ${rect.minX}, ${rect.minY} by ${rect.maxX}, ${rect.maxY}")
        runAsync {
            try {
                val canvas = ImmutableImage.create(rect.width.toInt(), rect.height.toInt())
                val oldImage = ImmutableImage.loader().fromFile(file)
                canvas.overlay(oldImage, -rect.minX.roundToInt(), -rect.minY.roundToInt()).output(JpegWriter.compression(95),  outFile)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } ui {success ->
            val couldMove = if(success) {
                backupImage(file)
                true
            } else {
                System.err.println("Couldn't crop image ${file.name}")
                errorImage(file)
            }

            if(!couldMove) {
                tornadofx.error("Couldn't properly finalize image ${file.name}")
            }

        }
    }

    private fun moveFile(file: File, baseDir: File, folderName: String): Boolean {
        val dest = File(baseDir, "$folderName\\${file.name}")
        dest.parentFile.mkdirs()
        val moved = file.renameTo(dest)
        if (!moved) println("Warning: Couldn't move file ${file.path} to $dest")
        return moved
    }
}