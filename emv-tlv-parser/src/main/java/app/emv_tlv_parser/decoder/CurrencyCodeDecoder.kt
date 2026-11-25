package app.emv_tlv_parser.decoder

internal object CurrencyCodeDecoder {

    private val currencies = mapOf(
        "0826" to "GBP - British Pound",
        "0840" to "USD - US Dollar",
        "0818" to "EGP - Egyptian Pound",
        "0978" to "EUR - Euro",
        "0392" to "JPY - Japanese Yen",
        "0784" to "AED - UAE Dirham",
        "0682" to "SAR - Saudi Riyal"
    )

    fun decode(value: String): String? {
        if (value.length != 4) return null

        if (!value.all { it.isDigit() }) return null

        return currencies[value]
    }
}