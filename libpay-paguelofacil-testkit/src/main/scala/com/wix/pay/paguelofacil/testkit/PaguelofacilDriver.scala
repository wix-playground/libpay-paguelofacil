package com.wix.pay.paguelofacil.testkit

import java.util.{List => JList}

import com.google.api.client.http.UrlEncodedParser
import com.wix.hoopoe.http.testkit.EmbeddedHttpProbe
import com.wix.pay.creditcard.CreditCard
import com.wix.pay.paguelofacil.model.{Errors, Statuses, TransactionResponse}
import com.wix.pay.paguelofacil.{PaguelofacilHelper, TransactionResponseParser}
import spray.http._

import scala.collection.JavaConversions._
import scala.collection.mutable

class PaguelofacilDriver(probe: EmbeddedHttpProbe) {
  def this(port: Int) = this(new EmbeddedHttpProbe(port, EmbeddedHttpProbe.NotFoundHandler))

  private val responseParser = new TransactionResponseParser

  def startProbe() {
    probe.doStart()
  }

  def stopProbe() {
    probe.doStop()
  }

  def resetProbe() {
    probe.handlers.clear()
  }

  def aSaleFor(cclw: String, card: CreditCard, amount: Double): RequestCtx = {
    val params = PaguelofacilHelper.createSaleRequest(
      cclw = cclw,
      amount = amount,
      creditCard = card
    )

    new RequestCtx(amount, params)
  }

  class RequestCtx(amount: Double, params: Map[String, String]) {
    def returns(transactionId: String): Unit = {
      val response = TransactionResponse(
        Status = Some(Statuses.approved),
        Amount = Some(amount.toString),
        AUTH_TOKEN = Some("Some Auth Token"),
        RespText = Some("NO MATCH"),
        RespCode = Some("00"),
        CODOPER = Some(transactionId),
        Date = Some("060515"),
        Time = Some("091715"),
        CardType = Some("VISA"),
        Name = Some("John"),
        LastName = Some("Smith"),
        Email = Some("-")
      )
      returns(response)
    }

    def isDeclined(): Unit = {
      val response = TransactionResponse(
        Status = Some(Statuses.declined),
        Amount = Some(amount.toString),
        AUTH_TOKEN = Some("Some Auth Token"),
        RespText = Some("some response text"),
        RespCode = Some("66"),
        CODOPER = Some("some codoper"),
        Date = Some("060515"),
        Time = Some("091715"),
        CardType = Some("VISA"),
        Name = Some("John"),
        LastName = Some("Smith"),
        Email = Some("-")
      )
      returns(response)
    }

    def errors(error: String): Unit = {
      val response = TransactionResponse(
        error = Some(error)
      )
      returns(response)
    }

    def failsOnInvalidCardNumberLength(): Unit = {
      val response = TransactionResponse(
        error = Some(Errors.invalidCardNumberLength)
      )
      returns(response)
    }

    private def returns(response: TransactionResponse): Unit = {
      probe.handlers += {
        case HttpRequest(
        HttpMethods.POST,
        Uri.Path("/"),
        headers,
        entity,
        _) if isStubbedRequest(headers, entity) =>
          // PagueloFacil returns JSON with Content-Type "text/plain;charset=utf-8"
          HttpResponse(
            status = StatusCodes.OK,
            entity = HttpEntity(ContentType(MediaTypes.`text/plain`, HttpCharsets.`UTF-8`), responseParser.stringify(response)))
      }
    }

    private def isStubbedRequest(headers: List[HttpHeader], entity: HttpEntity): Boolean = {
      isStubbedRequestHeaders(headers) && isStubbedRequestEntity(entity)
    }

    private def isStubbedRequestHeaders(headers: List[HttpHeader]): Boolean = {
      // PagueloFacil fails if no Accept header is given
      headers.exists(header => header.name == "Accept" && header.value == "*/*")
    }

    private def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val requestParams = urlDecode(entity.asString)

      params.forall {
        case (k, v) => requestParams.contains(k) && v == requestParams(k)
      }
    }

    private def urlDecode(str: String): Map[String, String] = {
      val params = mutable.LinkedHashMap[String, JList[String]]()
      UrlEncodedParser.parse(str, mutableMapAsJavaMap(params))
      params.mapValues( _(0) ).toMap
    }
  }
}
