package app.emv_tlv_parser.decoder

internal object TransactionCounterDecoder {

    fun decode(value: String): String? {
        if (value.length != 4) return null

        val counter = value.toIntOrNull(16) ?: return null

        return counter.toString()
    }
}