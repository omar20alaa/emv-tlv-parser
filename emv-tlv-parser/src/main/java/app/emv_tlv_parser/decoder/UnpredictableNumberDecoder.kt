package app.emv_tlv_parser.decoder

internal object UnpredictableNumberDecoder {

    fun decode(value: String): String? {
        if (value.length != 8 || value.toLongOrNull(16) == null) {
            return null
        }

        return value.uppercase()
    }
}