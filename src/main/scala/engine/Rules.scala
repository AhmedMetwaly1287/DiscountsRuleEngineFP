package engine

import models.Transaction

object Rules {

  // Each rule returns Some(discount) if the transaction qualifies, None otherwise
  private def expiryDiscount(t: Transaction): Option[Double] = ???

  private def productTypeDiscount(t: Transaction): Option[Double] = ???

  private def specialDayDiscount(t: Transaction): Option[Double] = ???

  private def quantityDiscount(t: Transaction): Option[Double] = ???

  private def appChannelDiscount(t: Transaction): Option[Double] = ???

  private def visaPaymentDiscount(t: Transaction): Option[Double] = ???

  val allRules: List[Transaction => Option[Double]] = List(
    expiryDiscount,
    productTypeDiscount,
    specialDayDiscount,
    quantityDiscount,
    appChannelDiscount,
    visaPaymentDiscount
  )
}