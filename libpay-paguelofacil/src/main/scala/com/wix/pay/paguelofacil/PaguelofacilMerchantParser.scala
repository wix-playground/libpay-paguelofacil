package com.wix.pay.paguelofacil

trait PaguelofacilMerchantParser {
  def parse(merchantKey: String): PaguelofacilMerchant
  def stringify(merchant: PaguelofacilMerchant): String
}
