package com.wix.pay.direct.paguelofacil


import com.wix.pay.direct.paguelofacil.model.{Statuses, TransactionResponse}
import org.specs2.matcher.MustMatchers._
import org.specs2.matcher.{AlwaysMatcher, Matcher}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class TransactionResponseParserTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val parser = new TransactionResponseParser

    def beTransactionResponse(error: Matcher[Option[String]] = AlwaysMatcher(),
                              Status: Matcher[Option[String]] = AlwaysMatcher(),
                              Amount: Matcher[Option[String]] = AlwaysMatcher(),
                              Response_Text: Matcher[Option[String]] = AlwaysMatcher(),
                              AUTH_TOKEN: Matcher[Option[String]] = AlwaysMatcher(),
                              RespText: Matcher[Option[String]] = AlwaysMatcher(),
                              RespCode: Matcher[Option[String]] = AlwaysMatcher(),
                              CODOPER: Matcher[Option[String]] = AlwaysMatcher(),
                              Date: Matcher[Option[String]] = AlwaysMatcher(),
                              Time: Matcher[Option[String]] = AlwaysMatcher(),
                              CardType: Matcher[Option[String]] = AlwaysMatcher(),
                              Name: Matcher[Option[String]] = AlwaysMatcher(),
                              LastName: Matcher[Option[String]] = AlwaysMatcher(),
                              Email: Matcher[Option[String]] = AlwaysMatcher()): Matcher[TransactionResponse] = {
      error ^^ { (_: TransactionResponse).error aka "error" } and
        Status ^^ { (_: TransactionResponse).Status aka "Status" } and
        Amount ^^ { (_: TransactionResponse).Amount aka "Amount" } and
        AUTH_TOKEN ^^ { (_: TransactionResponse).AUTH_TOKEN aka "AUTH_TOKEN" } and
        RespText ^^ { (_: TransactionResponse).RespText aka "RespText" } and
        RespCode ^^ { (_: TransactionResponse).RespCode aka "RespCode" } and
        CODOPER ^^ { (_: TransactionResponse).CODOPER aka "CODOPER" } and
        Date ^^ { (_: TransactionResponse).Date aka "Date" } and
        Time ^^ { (_: TransactionResponse).Time aka "Time" } and
        CardType ^^ { (_: TransactionResponse).CardType aka "CardType" } and
        Name ^^ { (_: TransactionResponse).Name aka "Name" } and
        LastName ^^ { (_: TransactionResponse).LastName aka "LastName" } and
        Email ^^ { (_: TransactionResponse).Email aka "Email" }
    }

    val someTransactionResponse = TransactionResponse(
      error = None,
      Status = Some(Statuses.approved),
      Amount = Some("some amount"),
      AUTH_TOKEN = Some("some auth token"),
      RespText = Some("some response text"),
      RespCode = Some("some response code"),
      CODOPER = Some("some codoper"),
      Date = Some("some date"),
      Time = Some("some time"),
      CardType = Some("some card type"),
      Name = Some("some name"),
      LastName = Some("some last name"),
      Email = Some("some email")
    )
  }

  "stringify and then parse" should {
    "yield an object similar to the original one" in new Ctx {
      val str = parser.stringify(someTransactionResponse)
      parser.parse(str) must beTransactionResponse(
        error = ===(someTransactionResponse.error),
        Status = ===(someTransactionResponse.Status),
        Amount = ===(someTransactionResponse.Amount),
        AUTH_TOKEN = ===(someTransactionResponse.AUTH_TOKEN),
        RespText = ===(someTransactionResponse.RespText),
        RespCode = ===(someTransactionResponse.RespCode),
        CODOPER = ===(someTransactionResponse.CODOPER),
        Date = ===(someTransactionResponse.Date),
        Time = ===(someTransactionResponse.Time),
        CardType = ===(someTransactionResponse.CardType),
        Name = ===(someTransactionResponse.Name),
        LastName = ===(someTransactionResponse.LastName),
        Email = ===(someTransactionResponse.Email)
      )
    }
  }
}
