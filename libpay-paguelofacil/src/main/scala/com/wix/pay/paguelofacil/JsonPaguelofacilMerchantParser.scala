package com.wix.pay.paguelofacil

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class JsonPaguelofacilMerchantParser() extends PaguelofacilMerchantParser {
  implicit val formats = DefaultFormats

  override def parse(merchantKey: String): PaguelofacilMerchant = {
    Serialization.read[PaguelofacilMerchant](merchantKey)
  }

  override def stringify(merchant: PaguelofacilMerchant): String = {
    Serialization.write(merchant)
  }
}
