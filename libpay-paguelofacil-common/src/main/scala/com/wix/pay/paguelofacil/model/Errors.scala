package com.wix.pay.paguelofacil.model

object Errors {
  /**
   * Invalid credit card number length.
   * The typo is intentional (that's what the gateway returns).
   */
  val invalidCardNumberLength = "credit card number lenght fail check"

  /** The CCLW is invalid. */
  val invalidCredentials = "Wrong Access Data/Company processing not authorized"
}
