package utils

import models.Transaction
import utils.Logger

import java.time.{LocalDate, LocalDateTime}
import scala.io.{Codec, Source}
import scala.util.Using

object FileReader {

  private def validateExtension(filePath: String): Either[String, String] = {
    val extensions = List("csv","json")
    val fileExt = filePath.split("\\.").last
    if (extensions.contains(fileExt)){
      Right(filePath)
    }
    else{
        val errMsg = s"FileReader: Invalid file Format, File Path: $filePath"
        Logger.error(errMsg)
        Left(errMsg)
      }
    }

  private def readFile(fileName: String, codec: String = Codec.UTF8.toString): List[String] = {
    // Using.resource ensures the file is closed automatically
    Using.resource(Source.fromFile(fileName, codec)) { source =>
      source.getLines().toList
    }
  }

  private def validateNotEmpty(filePath: String): Either[String, List[String]] = {
    val lines = readFile(filePath)
    if (lines.isEmpty) {
      val errMsg = s"FileReader: File is empty, File Path: $filePath"
      Logger.error(errMsg)
      Left(errMsg)
    } else {
      Right(lines)
    }
  }

  private def parseRow(row: String, lineNumber: Int): Either[String, Transaction] = {
    try {
      val columns = row.split(",").map(_.trim)

      if (columns.length < 7) {
        Left(s"FileReader: Line $lineNumber: Incomplete data. Expected 7 columns.")
      } else {
        // Only run this if we actually have enough columns
        val transaction = Transaction(
          timestamp     = LocalDateTime.parse(columns(0)),
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
  private def readRows(filePath: String): Either[String, List[Transaction]] = {
    val fileLines = readFile(filePath)

    val results = fileLines.tail.zipWithIndex.map(pair => parseRow(pair._1, pair._2 + 2))

    val transactions = results.collect { case Right(t) => t }
    val errors = results.collect { case Left(e) => e }

    errors.foreach(err => Logger.warn(s"FileReader: Skipping row. $err"))

    if (transactions.nonEmpty) {
      Right(transactions)
    } else {
      Left(s"FileReader: No valid transactions could be parsed from $filePath")
    }
  }

  def read(filePath: String): Either[String, List[Transaction]] = {
    validateExtension(filePath) match {
      case Left(err) => Left(err)
      case Right(_)  =>
        validateNotEmpty(filePath) match {
          case Left(err)   => Left(err)
          case Right(lines) => readRows(filePath)
        }
    }
  }
}