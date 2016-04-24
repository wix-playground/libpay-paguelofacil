package com.wix.pay.paguelofacil.model

/**
 *
 * @param Status   @see Statuses
 */
case class TransactionResponse(error: Option[String] = None,
                               Status: Option[String] = None,
                               Amount: Option[String] = None,
                               AUTH_TOKEN: Option[String] = None,
                               RespText: Option[String] = None,
                               RespCode: Option[String] = None,
                               CODOPER: Option[String] = None,
                               Date: Option[String] = None,
                               Time: Option[String] = None,
                               CardType: Option[String] = None,
                               Name: Option[String] = None,
                               LastName: Option[String] = None,
                               Email: Option[String] = None)