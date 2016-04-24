package com.wix.pay.paguelofacil

import com.wix.pay.paguelofacil.model.TransactionResponse
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class TransactionResponseParser() {
  implicit val formats = DefaultFormats

  def parse(str: String): TransactionResponse = {
    Serialization.read[TransactionResponse](str)
  }

  def stringify(obj: TransactionResponse): String = {
    Serialization.write(obj)
  }
}
