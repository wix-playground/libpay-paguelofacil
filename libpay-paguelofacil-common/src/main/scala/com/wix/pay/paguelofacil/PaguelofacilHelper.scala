package com.wix.pay.paguelofacil

import com.wix.pay.creditcard.CreditCard
import com.wix.pay.model.Name
import com.wix.pay.paguelofacil.model.{Fields, TransactionTypes}

object PaguelofacilHelper {
  def createSaleRequest(cclw: String,
                        amount: Double,
                        creditCard: CreditCard,
                        dealTitle: Option[String] = None,
                        customerEmail: Option[String] = None,
                        customerPhone: Option[String] = None): Map[String, String] = {
    createAuthorizeOrSaleRequest(
      transactionType = TransactionTypes.sale,
      cclw = cclw,
      amount = amount,
      creditCard = creditCard,
      dealTitle = dealTitle,
      customerEmail = customerEmail,
      customerPhone = customerPhone
    )
  }

  def createAuthorizeRequest(cclw: String,
                             amount: Double,
                             creditCard: CreditCard,
                             dealTitle: Option[String] = None,
                             customerEmail: Option[String] = None,
                             customerPhone: Option[String] = None): Map[String, String] = {
    createAuthorizeOrSaleRequest(
      transactionType = TransactionTypes.authorize,
      cclw = cclw,
      amount = amount,
      creditCard = creditCard,
      dealTitle = dealTitle,
      customerEmail = customerEmail,
      customerPhone = customerPhone
    )
  }

  def createCaptureRequest(cclw: String,
                           amount: Double,
                           authRefNum: String): Map[String, String] = {
    Map(
      Fields.cclw -> cclw,
      Fields.txType -> TransactionTypes.capture,
      Fields.cmtn -> amount.toString,
      Fields.authRefNum -> authRefNum
    )
  }

  private def createAuthorizeOrSaleRequest(transactionType: String,
                                           cclw: String,
                                           amount: Double,
                                           creditCard: CreditCard,
                                           dealTitle: Option[String] = None,
                                           customerEmail: Option[String] = None,
                                           customerPhone: Option[String] = None): Map[String, String] = {
    val email = customerEmail.getOrElse("-")
    val name = splitName(creditCard.holderName.get)
    Map(
      Fields.cclw -> cclw,
      Fields.txType -> transactionType,
      Fields.cmtn -> amount.toString,
      Fields.cdsc -> dealTitle.getOrElse("-"),
      Fields.ccNum -> creditCard.number,
      Fields.expMonth -> f"${creditCard.expiration.month}%02d",
      Fields.expYear -> f"${creditCard.expiration.year % 100}%02d",
      Fields.cvv2 -> creditCard.csc.get,
      Fields.name -> name.first,
      Fields.lastName -> name.last,
      Fields.email -> email,
      Fields.address -> creditCard.billingAddress.getOrElse("-"),
      Fields.tel -> customerPhone.getOrElse("-"),
      Fields.secretHash -> SecretHash.hash(creditCard.number, creditCard.csc.get, email)
    )
  }

  private def splitName(name: String) = {
    val parts = name.split("\\s")
    parts match {
      case arr if arr.length == 1 => new Name(parts(0), "-")
      case _ => new Name(parts(0), parts.drop(1).mkString(" "))
    }
  }
}
