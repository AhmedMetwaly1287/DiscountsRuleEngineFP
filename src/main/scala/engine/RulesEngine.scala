package engine

import models.{Transaction, Order}
import scala.collection.parallel.CollectionConverters._

object RulesEngine {

  private def applyRules(t: Transaction, rules: List[Transaction => Option[Double]]): List[Double] = {
    rules.flatMap(rule => rule(t))
  }

  // 0 discounts -> 0.0 | 1 discount -> as-is | 2+ discounts -> top 2 average
  private def aggregateDiscounts(discounts: List[Double]): Double = {
    if(discounts.length > 1) {
      val top2Discounts = discounts.sorted.reverse.take(2)
      top2Discounts.sum / 2.0
    }
    else if(discounts.length == 1) discounts.head
    else 0.0
  }

  private def computeFinalPrice(t: Transaction, discount: Double): Double = {
    val finalPrice = t.unitPrice * t.quantity * (1-discount)
    finalPrice
  }

  def processTransaction(t: Transaction): Order = {
    val discounts = applyRules(t, Rules.allRules)
    val discountValue = aggregateDiscounts(discounts)
    val finalPrice = computeFinalPrice(t, discountValue)
    Order(t.timestamp, t.productName, t.expiryDate, t.quantity, t.unitPrice, t.channel, t.paymentMethod, discountValue, finalPrice)
  }

  def processAll(transactions: List[Transaction]): List[Order] = {
    transactions.par.map(processTransaction).toList
  }
}