import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import engine.Rules
import models.Transaction
import java.time.{LocalDate, LocalDateTime}

class RulesSpec extends AnyFlatSpec with Matchers {

  def makeTransaction(
                       timestamp: LocalDateTime = LocalDateTime.now(),
                       productName: String = "Test Product",
                       expiryDate: LocalDate = LocalDate.now().plusMonths(1),
                       quantity: Int = 1,
                       unitPrice: Double = 100.0,
                       channel: String = "Store",
                       paymentMethod: String = "Cash"
                     ): Transaction = Transaction(timestamp, productName, expiryDate, quantity, unitPrice, channel, paymentMethod)

  "expiryDiscount" should "give 1% for a product expiring in 29 days" in {
    val t = makeTransaction(expiryDate = LocalDate.now().plusDays(29))
    Rules.expiryDiscount(t) shouldBe Some(0.01)
  }

  it should "give no discount for a product expiring in exactly 30 days" in {
    val t = makeTransaction(expiryDate = LocalDate.now().plusDays(30))
    Rules.expiryDiscount(t) shouldBe None
  }

  it should "give no discount for an already expired product" in {
    val t = makeTransaction(expiryDate = LocalDate.now().minusDays(1))
    Rules.expiryDiscount(t) shouldBe None
  }

  "productTypeDiscount" should "give 10% for Cheese" in {
    val t = makeTransaction(productName = "Cheese")
    Rules.productTypeDiscount(t) shouldBe Some(0.10)
  }

  it should "give 5% for Wine" in {
    val t = makeTransaction(productName = "Wine")
    Rules.productTypeDiscount(t) shouldBe Some(0.05)
  }

  it should "give no discount for other products" in {
    val t = makeTransaction(productName = "Bread")
    Rules.productTypeDiscount(t) shouldBe None
  }

  "specialDayDiscount" should "give 50% on March 23rd" in {
    val t = makeTransaction(timestamp = LocalDateTime.of(2026, 3, 23, 12, 0))
    Rules.specialDayDiscount(t) shouldBe Some(0.50)
  }

  it should "give no discount on any other date" in {
    val t = makeTransaction(timestamp = LocalDateTime.of(2026, 3, 24, 12, 0))
    Rules.specialDayDiscount(t) shouldBe None
  }

  "quantityDiscount" should "give no discount for 5 units" in {
    val t = makeTransaction(quantity = 5)
    Rules.quantityDiscount(t) shouldBe None
  }

  it should "give 5% for 6 units" in {
    val t = makeTransaction(quantity = 6)
    Rules.quantityDiscount(t) shouldBe Some(0.05)
  }

  it should "give 7% for 10 units" in {
    val t = makeTransaction(quantity = 10)
    Rules.quantityDiscount(t) shouldBe Some(0.07)
  }

  it should "give 10% for 15 units" in {
    val t = makeTransaction(quantity = 15)
    Rules.quantityDiscount(t) shouldBe Some(0.10)
  }

  "appChannelDiscount" should "give 5% for App channel with quantity 3" in {
    val t = makeTransaction(channel = "App", quantity = 3)
    Rules.appChannelDiscount(t) shouldBe Some(0.05)
  }

  it should "give 10% for App channel with quantity 6" in {
    val t = makeTransaction(channel = "App", quantity = 6)
    Rules.appChannelDiscount(t) shouldBe Some(0.10)
  }

  it should "give no discount for Store channel" in {
    val t = makeTransaction(channel = "Store", quantity = 10)
    Rules.appChannelDiscount(t) shouldBe None
  }

  "visaPaymentDiscount" should "give 5% for Visa" in {
    val t = makeTransaction(paymentMethod = "Visa")
    Rules.visaPaymentDiscount(t) shouldBe Some(0.05)
  }

  it should "give no discount for Cash" in {
    val t = makeTransaction(paymentMethod = "Cash")
    Rules.visaPaymentDiscount(t) shouldBe None
  }
}