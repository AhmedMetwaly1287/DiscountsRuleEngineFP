package utils

import models.Order
import java.sql.{Connection, DriverManager}
import scala.util.Using

object DatabaseWriter {

  private val url      = sys.env.getOrElse("DB_URL", "jdbc:mysql://HOSTNAME:PORT/DB_NAME")
  private val username = sys.env.getOrElse("DB_USER", "YOUR_USERNAME")
  private val password = sys.env.getOrElse("DB_PASS", "YOUR_PASSWORD")
  private val table    = "orders"

  private def getConnection: Either[String, Connection] =
    try Right(DriverManager.getConnection(url, username, password))
    catch { case e: Exception => Left(s"DatabaseWriter: ${e.getMessage}") }

  private def addToStatement(stmt: java.sql.PreparedStatement, order: Order): Unit = {
    stmt.setObject(1, order.timestamp)
    stmt.setString(2, order.productName)
    stmt.setObject(3, order.expiryDate)
    stmt.setInt(4, order.quantity)
    stmt.setDouble(5, order.unitPrice)
    stmt.setString(6, order.channel)
    stmt.setString(7, order.paymentMethod)
    stmt.setDouble(8, order.discount)
    stmt.setDouble(9, order.finalPrice)
    stmt.addBatch()
  }

  private def writeAll(connection: Connection, orders: List[Order]): Either[String, Int] = {
    val sql =
      s"""INSERT INTO $table
         |(timestamp, product_name, expiry_date, quantity, unit_price, channel, payment_method, discount, final_price)
         |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin
    try {
      Using.resource(connection.prepareStatement(sql)) { stmt =>
        orders.foreach(order => addToStatement(stmt, order))
        val count = stmt.executeBatch().length
        Logger.info(s"DatabaseWriter: Inserted $count orders")
        Right(count)
      }
    } catch {
      case e: Exception =>
        Logger.error(s"DatabaseWriter: ${e.getMessage}")
        Left(e.getMessage)
    }
  }

  def saveOrders(orders: List[Order]): Either[String, Int] =
    getConnection match {
      case Left(err)   =>
        Logger.error(err)
        Left(err)
      case Right(conn) =>
        Using.resource(conn)(writeAll(_, orders))
    }
}