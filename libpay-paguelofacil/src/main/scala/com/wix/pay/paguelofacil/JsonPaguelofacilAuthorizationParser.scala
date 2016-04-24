package com.wix.pay.paguelofacil

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class JsonPaguelofacilAuthorizationParser() extends PaguelofacilAuthorizationParser {
  implicit val formats = DefaultFormats

  override def parse(authorizationKey: String): PaguelofacilAuthorization = {
    Serialization.read[PaguelofacilAuthorization](authorizationKey)
  }

  override def stringify(authorization: PaguelofacilAuthorization): String = {
    Serialization.write(authorization)
  }
}
