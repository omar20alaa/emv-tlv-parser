package app.emv_tlv_parser.decoder

internal object PANSequenceNumberDecoder {

    fun decode(value: String): String? {
        if (value.length != 2 || !value.all { it.isDigit() }) {
            return null
        }

        return value
    }
}