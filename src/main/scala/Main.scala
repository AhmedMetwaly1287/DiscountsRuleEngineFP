import utils.{FileReader, DatabaseWriter, Logger}
import engine.RulesEngine

object Main extends App {

  val inputFilePath = "E:\\Work\\ITI\\Courses\\Course 22 - Scala\\Labs\\Project\\TRX10M.csv"

  FileReader.read(inputFilePath) match {
    case Left(err) => Logger.error(s"Pipeline failed: $err")
    case Right(batches) =>
      val totalInserted = batches.foldLeft(0) { (acc, batch) =>
        RulesEngine.processAll(batch) match {
          case orders =>
            DatabaseWriter.saveOrders(orders) match {
              case Right(count) =>
                Logger.info(s"Batch inserted: $count orders")
                acc + count
              case Left(err) =>
                Logger.error(s"Batch failed: $err")
                acc
            }
        }
      }
      Logger.info(s"Pipeline completed. Total orders written: $totalInserted")
  }
}