package app.emv_tlv_parser.decoder

internal object CryptogramInformationDecoder {

    fun decode(value: String): String? {
        if (value.length != 2 || !value.all { it.digitToIntOrNull(16) != null }) {
            return null
        }

        val byteValue = value.toInt(16)

        return when (byteValue and 0xC0) {
            0x00 -> "AAC - Application Authentication Cryptogram"
            0x40 -> "TC - Transaction Certificate"
            0x80 -> "ARQC - Authorization Request Cryptogram"
            else -> "RFU - Reserved for Future Use"
        }
    }
}