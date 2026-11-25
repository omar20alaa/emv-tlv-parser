package app.emv_tlv_parser.decoder

internal object TransactionTypeDecoder {

    private val transactionTypes = mapOf(
        "00" to "Purchase",
        "01" to "Cash",
        "09" to "Purchase with Cashback",
        "20" to "Refund"
    )

    fun decode(value: String): String? {
        if (value.length != 2) return null

        if (!value.all { it.isDigit() }) {
            return null
        }

        return transactionTypes[value]
            ?: "Unknown Transaction Type ($value)"
    }

}