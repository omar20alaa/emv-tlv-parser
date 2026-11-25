package app.emv_tlv_parser.decoder

internal object PANDecoder {

    fun decode(value: String): String? {

        val normalizedValue = value.uppercase()

        if (normalizedValue.isEmpty()) return null

        if (!normalizedValue.matches(Regex("^[0-9A-F]+$"))) {
            return null
        }

        val pan = normalizedValue.removeSuffix("F")

        if (!pan.matches(Regex("^[0-9]{8,19}$"))) {
            return null
        }

        return "*".repeat(pan.length - 4) + pan.takeLast(4)
    }
}