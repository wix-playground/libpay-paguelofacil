package com.wix.pay.paguelofacil

import org.specs2.matcher.{AlwaysMatcher, Matcher, Matchers}

trait PaguelofacilMatchers extends Matchers {
  def authorizationParser: PaguelofacilAuthorizationParser

  def beAuthorization(authRefNum: Matcher[String] = AlwaysMatcher()): Matcher[PaguelofacilAuthorization] = {
    authRefNum ^^ { (_: PaguelofacilAuthorization).authRefNum aka "authRefNum" }
  }

  def beAuthorizationKey(authorization: Matcher[PaguelofacilAuthorization]): Matcher[String] = {
    authorization ^^ { authorizationParser.parse(_: String) aka "parsed authorization"}
  }
}

object PaguelofacilMatchers extends PaguelofacilMatchers {
  override val authorizationParser = new JsonPaguelofacilAuthorizationParser()
}