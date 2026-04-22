import utils.{FileReader, DatabaseWriter, Logger}
import engine.RulesEngine

object Main extends App {

  val inputFilePath = "TRX10M.csv"

  FileReader.read(inputFilePath) match {
    case Left(err) => Logger.error(s"Pipeline failed: $err")
    case Right(batches) =>
      val totalInserted = batches.foldLeft(0) { (acc, batch) =>
        val orders = RulesEngine.processAll(batch)
        DatabaseWriter.saveOrders(orders) match {
              case Right(count) =>
                Logger.info(s"Batch inserted: $count orders")
                acc + count
              case Left(err) =>
                Logger.error(s"Batch failed: $err")
                acc
            }
      }
      Logger.info(s"Pipeline completed. Total orders written: $totalInserted")
  }
}