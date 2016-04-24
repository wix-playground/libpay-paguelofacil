package com.wix.pay.paguelofacil

trait PaguelofacilAuthorizationParser {
  def parse(authorizationKey: String): PaguelofacilAuthorization
  def stringify(authorization: PaguelofacilAuthorization): String
}
