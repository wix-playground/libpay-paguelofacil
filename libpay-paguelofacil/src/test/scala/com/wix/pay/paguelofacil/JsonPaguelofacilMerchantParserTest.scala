package com.wix.pay.paguelofacil


import org.specs2.matcher.MustMatchers._
import org.specs2.matcher.{AlwaysMatcher, Matcher}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class JsonPaguelofacilMerchantParserTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val merchantParser: PaguelofacilMerchantParser = new JsonPaguelofacilMerchantParser

    def bePaguelofacilMerchant(cclw: Matcher[String] = AlwaysMatcher()): Matcher[PaguelofacilMerchant] = {
      cclw ^^ { (_: PaguelofacilMerchant).cclw aka "cclw" }
    }

    val someMerchant = PaguelofacilMerchant(
      cclw = "some CCLW"
    )
  }

  "stringify and then parse" should {
    "yield a merchant similar to the original one" in new Ctx {
      val merchantKey = merchantParser.stringify(someMerchant)
      merchantParser.parse(merchantKey) must bePaguelofacilMerchant(
        cclw = ===(someMerchant.cclw)
      )
    }
  }
}
