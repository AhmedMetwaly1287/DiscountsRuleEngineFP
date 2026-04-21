package engine

import models.Transaction

import java.time.temporal.ChronoUnit

object Rules {

  // Each rule returns Some(discount) if the transaction qualifies, None otherwise
  private def expiryDiscount(t: Transaction): Option[Double] = {
    val numDays = ChronoUnit.DAYS.between(t.timestamp, t.expiryDate)
    if (numDays < 30 && numDays >= 0) Some((30 - numDays)/100.0)
    else
      None
  }

  private def productTypeDiscount(t: Transaction): Option[Double] = {
    if (t.productName.toLowerCase.contains("cheese")) Some(0.1)
    else if (t.productName.toLowerCase.contains("wine")) Some(0.05)
    else None
  }

  private def specialDayDiscount(t: Transaction): Option[Double] = {
    if (t.timestamp.getMonthValue == 3 && t.timestamp.getDayOfMonth == 23) Some(0.5)
    else None
  }

  private def quantityDiscount(t: Transaction): Option[Double] = {
    t.quantity match{
      case q if (q >= 6 && q <=9) => Some(0.05)
      case q if (q >= 10 && q <=14) => Some(0.07)
      case q if (q >= 15 ) => Some(0.01)
      case _ => None
    }
  }

  private def appChannelDiscount(t: Transaction): Option[Double] = {
    if(t.channel == "App") Some(math.ceil(t.quantity / 5.0) * 0.05)
    else None
  }

  private def visaPaymentDiscount(t: Transaction): Option[Double] = {
    if(t.paymentMethod == "Visa") Some(0.05)
    else None
  }

  val allRules: List[Transaction => Option[Double]] = List(
    expiryDiscount,
    productTypeDiscount,
    specialDayDiscount,
    quantityDiscount,
    appChannelDiscount,
    visaPaymentDiscount
  )
}