package com.wix.pay.direct.paguelofacil

import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

class SecretHashTest extends SpecWithJUnit {
  trait Ctx extends Scope {}

  "hashing some known values" should {
    "yield a correct hash" in new Ctx {
      val ccNumber = "4580458045804580"
      val csc = "123"
      val customerEmail = "example@example.org"

      val expectedHash = "C50A947DAC2832F38335A1CE366A3C6428DAF3BFE6DF7702ECDBD10EC0E3FEE80FBF0C1DBE6EA39D0AC00764B32BA47B2148CE29D9251497AD04606667275D77"

      SecretHash.hash(ccNumber, csc, customerEmail) must beEqualTo(expectedHash)
    }
  }
}
