package utils

import models.Order
import java.sql.Connection

object DatabaseWriter {

  private val url      = "jdbc:mysql://localhost:3306/orders_db"
  private val username = "root"
  private val password = ""
  private val table    = "orders"

  private def getConnection(): Either[String, Connection] = ???

  private def writeOrder(connection: Connection, order: Order): Either[String, Unit] = ???

  // Failed rows are logged individually, one failure does not abort the rest
  private def writeAll(connection: Connection, orders: List[Order]): Either[String, Int] = ???

  def saveOrders(orders: List[Order]): Either[String, Int] = ???
}