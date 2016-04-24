package com.wix.pay.paguelofacil

import com.wix.pay.paguelofacil.PaguelofacilMatchers._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class JsonPaguelofacilAuthorizationParserTest extends SpecWithJUnit {
  trait Ctx extends Scope {
    val authorizationParser: PaguelofacilAuthorizationParser = new JsonPaguelofacilAuthorizationParser

    val someAuthorization = PaguelofacilAuthorization(
      authRefNum = "some authRefNum"
    )
  }

  "stringify and then parse" should {
    "yield an authorization similar to the original one" in new Ctx {
      val authorizationKey = authorizationParser.stringify(someAuthorization)
      authorizationParser.parse(authorizationKey) must beAuthorization(
        authRefNum = ===(someAuthorization.authRefNum)
      )
    }
  }
}
