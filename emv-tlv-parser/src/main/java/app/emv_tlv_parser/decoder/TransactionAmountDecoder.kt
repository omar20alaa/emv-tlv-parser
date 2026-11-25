package app.emv_tlv_parser.decoder

internal object TransactionAmountDecoder {

    fun decode(value: String): String? {
        if (value.length != 12 || !value.all { it.isDigit() }) {
            return null
        }

        val amount = value.toLongOrNull() ?: return null

        val majorUnits = amount / 100
        val minorUnits = amount % 100

        return "%d.%02d".format(majorUnits, minorUnits)
    }
}