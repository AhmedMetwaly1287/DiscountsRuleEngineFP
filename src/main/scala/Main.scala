import utils.{FileReader, DatabaseWriter, Logger}
import engine.RulesEngine

object Main extends App {

  val inputFilePath = "TRX1000.csv"

  val result: Either[String, Int] = for {
    transactions <- FileReader.readFile(inputFilePath)
    orders        = RulesEngine.processAll(transactions)
    inserted     <- DatabaseWriter.saveOrders(orders)
  } yield inserted

  result match {
    case Right(count) => Logger.info(s"Pipeline completed. $count orders written to database.")
    case Left(error)  => Logger.error(s"Pipeline failed: $error")
  }
}