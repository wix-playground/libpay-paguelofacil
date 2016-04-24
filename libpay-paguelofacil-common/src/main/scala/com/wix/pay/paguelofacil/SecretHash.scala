package com.wix.pay.paguelofacil

import org.apache.commons.codec.digest.DigestUtils

object SecretHash {
  def hash(ccNumber: String, csc: String, customerEmail: String): String = {
    concatAndHash(ccNumber, csc, customerEmail)
  }

  private def concatAndHash(strings: String*): String = {
    toHexString(DigestUtils.sha512(strings.mkString))
  }

  private def toHexString(bytes: Array[Byte]): String = bytes.map("%02X" format _).mkString
}
