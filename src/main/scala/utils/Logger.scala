import java.nio.file.{Files, Paths, StandardOpenOption}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

object Logger {

  val INFO  = "INFO"
  val WARN  = "WARN"
  val ERROR = "ERROR"

  private val logFilePath   = "rules_engine.log"
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  private def formatEntry(level: String, message: String): String = {
    val timestamp = LocalDateTime.now().format(dateFormatter)
    s"$timestamp | $level | $message\n"
  }

  def log(level: String, message: String): Try[Unit] = Try {
    val entry = formatEntry(level, message)
    val path  = Paths.get(logFilePath)
    Files.write(
      path,
      entry.getBytes("UTF-8"),
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND
    )
  }

  def info(message: String):  Unit = log(INFO, message)
  def warn(message: String):  Unit = log(WARN, message)
  def error(message: String): Unit = log(ERROR, message)
}