package app.emv_tlv_parser.decoder

internal object EMVValueDecoder {

    fun decode(tag: String, value: String): String? {

        return when (tag.uppercase()) {

            "9A" -> TransactionDateDecoder.decode(value)

            "9F02" -> TransactionAmountDecoder.decode(value)

            "9F03" -> TransactionAmountDecoder.decode(value)

            "5F2A" -> CurrencyCodeDecoder.decode(value)

            "9C" -> TransactionTypeDecoder.decode(value)

            "9F36" -> TransactionCounterDecoder.decode(value)

            "9F37" -> UnpredictableNumberDecoder.decode(value)

            "5F34" -> PANSequenceNumberDecoder.decode(value)

            "9F26" -> ApplicationCryptogramDecoder.decode(value)

            "9F27" -> CryptogramInformationDecoder.decode(value)

            "5A" -> PANDecoder.decode(value)

            "5F24" -> ExpirationDateDecoder.decode(value)

            else -> null
        }
    }
}