package com.wix.pay.paguelofacil.it

import com.google.api.client.http.javanet.NetHttpTransport
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields, YearMonth}
import com.wix.pay.model.CurrencyAmount
import com.wix.pay.paguelofacil._
import com.wix.pay.paguelofacil.testkit.PaguelofacilDriver
import com.wix.pay.{PaymentErrorException, PaymentGateway, PaymentRejectedException}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class PaguelofacilGatewayIT extends SpecWithJUnit {
  val paguelofacilPort = 10020

  val requestFactory = new NetHttpTransport().createRequestFactory()
  val driver = new PaguelofacilDriver(port = paguelofacilPort)
  step {
    driver.startProbe()
  }

  sequential

  trait Ctx extends Scope {
    val merchantParser = new JsonPaguelofacilMerchantParser()
    val authorizationParser = new JsonPaguelofacilAuthorizationParser()

    val someMerchant = PaguelofacilMerchant(
      cclw = "some CCLW"
    )
    val merchantKey = merchantParser.stringify(someMerchant)

    val someCurrencyAmount = CurrencyAmount("PAB", 33.3)
    val someCreditCard = CreditCard(
      number = "4012888818888",
      expiration = YearMonth(2020, 12),
      additionalFields = Some(CreditCardOptionalFields.withFields(
        csc = Some("123"),
       holderName = Some("John Smith")
      ))
    )

    val someTransactionId = "some transaction ID"

    val paguelofacil: PaymentGateway = new PaguelofacilGateway(
      requestFactory = requestFactory,
      endpointUrl = s"http://localhost:$paguelofacilPort/",
      merchantParser = merchantParser,
      authorizationParser = authorizationParser
    )

    driver.resetProbe()
  }

  "sale" should {
    "gracefully fail on error" in new Ctx {
      val someError = "some error"
      driver.aSaleFor(
        cclw = someMerchant.cclw,
        card = someCreditCard,
        amount = someCurrencyAmount.amount
      ) errors someError

      paguelofacil.sale(
        merchantKey = merchantKey,
        creditCard = someCreditCard,
        currencyAmount = someCurrencyAmount
      ) must beAFailedTry.like {
        case e: PaymentErrorException => e.message must contain(someError)
      }
    }

    "successfully yield a transaction ID on valid request" in new Ctx {
      driver.aSaleFor(
        cclw = someMerchant.cclw,
        card = someCreditCard,
        amount = someCurrencyAmount.amount
      ) returns someTransactionId

      paguelofacil.sale(
        merchantKey = merchantKey,
        creditCard = someCreditCard,
        currencyAmount = someCurrencyAmount
      ) must beASuccessfulTry(check = ===(someTransactionId))
    }

    "gracefully fail on rejected card" in new Ctx {
      driver.aSaleFor(
        cclw = someMerchant.cclw,
        card = someCreditCard,
        amount = someCurrencyAmount.amount
      ) isDeclined()

      paguelofacil.sale(
        merchantKey = merchantKey,
        creditCard = someCreditCard,
        currencyAmount = someCurrencyAmount
      ) must beAFailedTry(check = beAnInstanceOf[PaymentRejectedException])
    }

    "gracefully fail on invalid card number length" in new Ctx {
      driver.aSaleFor(
        cclw = someMerchant.cclw,
        card = someCreditCard,
        amount = someCurrencyAmount.amount
      ) failsOnInvalidCardNumberLength()

      paguelofacil.sale(
        merchantKey = merchantKey,
        creditCard = someCreditCard,
        currencyAmount = someCurrencyAmount
      ) must beAFailedTry(check = beAnInstanceOf[PaymentRejectedException])
    }
  }

  step {
    driver.stopProbe()
  }
}
