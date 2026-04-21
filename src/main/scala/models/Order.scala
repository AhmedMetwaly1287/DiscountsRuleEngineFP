package models

import java.time.{LocalDate, LocalDateTime}

case class Order(
                  timestamp: LocalDateTime,
                  productName: String,
                  expiryDate: LocalDate,
                  quantity: Int,
                  unitPrice: Double,
                  channel: String,
                  paymentMethod: String,
                  discount: Double,
                  finalPrice: Double
                )