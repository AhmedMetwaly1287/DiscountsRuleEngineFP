package utils

object Logger {

  sealed trait LogLevel
  case object INFO  extends LogLevel
  case object WARN  extends LogLevel
  case object ERROR extends LogLevel

  private val logFilePath = "rules_engine.log"

  private def formatEntry(level: LogLevel, message: String): String = ???

  def log(level: LogLevel, message: String): Unit = ???

  def info(message: String): Unit  = log(INFO, message)
  def warn(message: String): Unit  = log(WARN, message)
  def error(message: String): Unit = log(ERROR, message)
}