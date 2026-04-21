package engine

import models.{Transaction, Order}

object RulesEngine {

  private def applyRules(t: Transaction, rules: List[Transaction => Option[Double]]): List[Double] = ???

  // 0 discounts -> 0.0 | 1 discount -> as-is | 2+ discounts -> top 2 average
  private def aggregateDiscounts(discounts: List[Double]): Double = ???

  private def computeFinalPrice(t: Transaction, discount: Double): Double = ???

  def processTransaction(t: Transaction): Order = ???

  def processAll(transactions: List[Transaction]): List[Order] = ???
}