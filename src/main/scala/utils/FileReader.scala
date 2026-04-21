package utils

import models.Transaction

object FileReader {

  private def validateExtension(filePath: String): Either[String, String] = ???

  private def validateNotEmpty(filePath: String): Either[String, String] = ???

  private def parseRow(row: String, lineNumber: Int): Either[String, Transaction] = ???

  // Failed rows are logged as warnings and skipped, not treated as fatal
  private def readRows(filePath: String): Either[String, List[Transaction]] = ???

  def readFile(filePath: String): Either[String, List[Transaction]] = ???
}