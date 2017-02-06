package com.wix.pay.paguelofacil

import com.google.api.client.http._
import com.wix.pay.creditcard.CreditCard
import com.wix.pay.model.{Customer, Deal, Payment}
import com.wix.pay.paguelofacil.model.{Errors, Statuses}
import com.wix.pay.{PaymentErrorException, PaymentException, PaymentGateway, PaymentRejectedException}

import scala.collection.JavaConversions
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

object Endpoints {
  val production = "https://secure.paguelofacil.com/rest/ccprocessing"
  val development = "https://dev.paguelofacil.com/rest/ccprocessing"
}

class PaguelofacilGateway(requestFactory: HttpRequestFactory,
                          connectTimeout: Option[Duration] = None,
                          readTimeout: Option[Duration] = None,
                          numberOfRetries: Int = 0,
                          endpointUrl: String = Endpoints.production,
                          merchantParser: PaguelofacilMerchantParser = new JsonPaguelofacilMerchantParser,
                          authorizationParser: PaguelofacilAuthorizationParser = new JsonPaguelofacilAuthorizationParser) extends PaymentGateway {
  private val responseParser = new TransactionResponseParser

  override def authorize(merchantKey: String, creditCard: CreditCard, payment: Payment, customer: Option[Customer], deal: Option[Deal]): Try[String] = {
    Try {
      throw PaymentErrorException("PagueloFacil does not support two-step payments")
    }
  }

  override def capture(merchantKey: String, authorizationKey: String, amount: Double): Try[String] = {
    Try {
      throw PaymentErrorException("PagueloFacil does not support two-step payments")
    }
  }

  override def sale(merchantKey: String, creditCard: CreditCard, payment: Payment, customer: Option[Customer], deal: Option[Deal]): Try[String] = {
    Try {
      require(creditCard.csc.isDefined, "CSC is mandatory for PagueloFacil")
      require(creditCard.holderName.isDefined, "Holder Name is mandatory for PagueloFacil")
      require(PaguelofacilGateway.supportedCurrencies.contains(payment.currencyAmount.currency), s"PagueloFacil doesn't support ${payment.currencyAmount.currency}")
      require(payment.installments == 1, "PagueloFacil does not support installments")

      val merchant = merchantParser.parse(merchantKey)

      val request = PaguelofacilHelper.createSaleRequest(
        cclw = merchant.cclw,
        amount = payment.currencyAmount.amount,
        creditCard = creditCard,
        dealTitle = if (deal.isDefined) deal.get.title else None,
        customerEmail = if (customer.isDefined) customer.get.email else None,
        customerPhone = if (customer.isDefined) customer.get.phone else None
      )
      val responseJson = doRequest(request)
      val response = responseParser.parse(responseJson)

      response.error match {
        case None => response.Status.get match {
          case Statuses.approved => response.CODOPER.get
          case Statuses.declined => throw PaymentRejectedException(response.RespText.get)
        }
        case Some(Errors.invalidCardNumberLength) => throw PaymentRejectedException(Errors.invalidCardNumberLength)
        case Some(errorMessage) => throw PaymentErrorException(errorMessage)
      }

    } match {
      case Success(codoper) => Success(codoper)
      case Failure(e: PaymentException) => Failure(e)
      case Failure(e) => Failure(PaymentErrorException(e.getMessage, e))
    }
  }

  override def voidAuthorization(merchantKey: String, authorizationKey: String): Try[String] = {
    Try {
      throw PaymentErrorException("PagueloFacil does not support two-step payments")
    }
  }

  private def doRequest(params: Map[String, String]): String = {
    val httpRequest = requestFactory.buildPostRequest(
      new GenericUrl(endpointUrl),
      new UrlEncodedContent(JavaConversions.mapAsJavaMap(params))
    )

    connectTimeout foreach (to => httpRequest.setConnectTimeout(to.toMillis.toInt))
    readTimeout foreach (to => httpRequest.setReadTimeout(to.toMillis.toInt))
    httpRequest.setNumberOfRetries(numberOfRetries)

    httpRequest.getHeaders.setAccept("*/*")

    extractAndCloseResponse(httpRequest.execute())
  }

  private def extractAndCloseResponse(httpResponse: HttpResponse): String = {
    try {
      httpResponse.parseAsString()
    } finally {
      httpResponse.ignore()
    }
  }
}

object PaguelofacilGateway {
  private val supportedCurrencies = Set("PAB", "USD")
}
