package app.emv_tlv_parser.decoder

internal object ApplicationCryptogramDecoder {

    fun decode(value: String): String? {
        if (value.length != 16 || !value.all { it.digitToIntOrNull(16) != null }) {
            return null
        }

        return value.uppercase()
    }
}