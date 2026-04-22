package utils

import models.Transaction
import utils.Logger

import java.time.{LocalDate, LocalDateTime}
import scala.io.{Codec, Source}
import scala.util.Using

object FileReader {

  private def validateExtension(filePath: String): Either[String, String] = {
    val extensions = List("csv", "json")
    val fileExt    = filePath.split("\\.").last
    if (extensions.contains(fileExt)) Right(filePath)
    else Left(s"FileReader: Invalid file format, File Path: $filePath")
  }

  private def validateNotEmpty(filePath: String): Either[String, Iterator[String]] = {
    val lines = readFile(filePath)
    if (lines.isEmpty) Left(s"FileReader: File is empty, File Path: $filePath")
    else Right(lines)
  }

  private def readFile(fileName: String, codec: String = Codec.UTF8.toString): Iterator[String] = {
    Source.fromFile(fileName, codec).getLines()
  }

  private def parseRow(row: String, lineNumber: Int): Either[String, Transaction] = {
    try {
      val columns = row.split(",").map(_.trim)

      if (columns.length < 7) {
        Left(s"FileReader: Line $lineNumber: Incomplete data. Expected 7 columns.")
      } else {
        // Only run this if we actually have enough columns
        val transaction = Transaction(
          timestamp = LocalDateTime.parse(columns(0).stripSuffix("Z")),
          productName   = columns(1),
          expiryDate    = LocalDate.parse(columns(2)),
          quantity      = columns(3).toInt,
          unitPrice     = columns(4).toDouble,
          channel       = columns(5),
          paymentMethod = columns(6)
        )
        Right(transaction)
      }
    } catch {
      case e: Exception =>
        Left(s"Line $lineNumber: Failed to parse row. Error: ${e.getMessage}")
    }
  }

  // Failed rows are logged as warnings and skipped, not treated as fatal
  private def readRows(filePath: String): Either[String, Iterator[List[Transaction]]] = {
    val fileLines = readFile(filePath).drop(1)
    val batches: Iterator[List[Transaction]] = fileLines.zipWithIndex
      .map { case (line, index) => parseRow(line, index + 2) }
      .tapEach {
        case Left(err) => Logger.warn(s"FileReader: Skipping row. $err")
        case _         => ()
      }
      .collect { case Right(t) => t }
      .grouped(50000)
      .map(_.toList)
    Right(batches)
  }

  def read(filePath: String): Either[String, Iterator[List[Transaction]]] = {
    validateExtension(filePath) match {
      case Left(err) => Left(err)
      case Right(_)  =>
        validateNotEmpty(filePath) match {
          case Left(err) => Left(err)
          case Right(_)  => readRows(filePath)
        }
    }
  }
}