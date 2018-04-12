package com.wix.pay.paguelofacil.testkit


import scala.collection.JavaConversions._
import scala.collection.mutable
import java.util.{List => JList}
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import com.google.api.client.http.UrlEncodedParser
import com.wix.e2e.http.api.StubWebServer
import com.wix.e2e.http.client.extractors.HttpMessageExtractors._
import com.wix.e2e.http.server.WebServerFactory.aStubWebServer
import com.wix.pay.creditcard.CreditCard
import com.wix.pay.paguelofacil.model.{Errors, Statuses, TransactionResponse}
import com.wix.pay.paguelofacil.{PaguelofacilHelper, TransactionResponseParser}


class PaguelofacilDriver(server: StubWebServer) {
  def this(port: Int) = this(aStubWebServer.onPort(port).build)
  
  private val responseParser = new TransactionResponseParser

  def start(): Unit = server.start()
  def stop(): Unit = server.stop()
  def reset(): Unit = server.replaceWith()


  def aSaleFor(cclw: String, card: CreditCard, amount: Double): RequestCtx = {
    val params = PaguelofacilHelper.createSaleRequest(
      cclw = cclw,
      amount = amount,
      creditCard = card)

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
        Email = Some("-"))

      returns(response)
    }

    def getsDeclined(): Unit = {
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
        Email = Some("-"))

      returns(response)
    }

    def errors(error: String): Unit = {
      val response = TransactionResponse(error = Some(error))

      returns(response)
    }

    def failsOnInvalidCardNumberLength(): Unit = {
      val response = TransactionResponse(error = Some(Errors.invalidCardNumberLength))

      returns(response)
    }

    private def returns(response: TransactionResponse): Unit = {
      server.appendAll {
        case HttpRequest(
          HttpMethods.POST,
          Path("/"),
          headers,
          entity,
          _) if isStubbedRequest(headers, entity) =>
            // PagueloFacil returns JSON with Content-Type "text/plain;charset=utf-8"
            HttpResponse(
              status = StatusCodes.OK,
              entity = HttpEntity(
                ContentType(MediaTypes.`text/plain`, HttpCharsets.`UTF-8`),
                responseParser.stringify(response)))
      }
    }

    private def isStubbedRequest(headers: Seq[HttpHeader], entity: HttpEntity): Boolean = {
      isStubbedRequestHeaders(headers) && isStubbedRequestEntity(entity)
    }

    private def isStubbedRequestHeaders(headers: Seq[HttpHeader]): Boolean = {
      // PagueloFacil fails if no Accept header is given
      headers.exists(header => header.name == "Accept" && header.value == "*/*")
    }

    private def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val requestParams = urlDecode(entity.extractAsString)

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
