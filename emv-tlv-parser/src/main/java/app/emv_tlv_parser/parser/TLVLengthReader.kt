
package app.emv_tlv_parser.parser

internal class TLVLengthReader {

    fun read(hex: String, startIndex: Int): LengthResult {
        require(startIndex + 2 <= hex.length) {
            "Unexpected end of data while reading length"
        }

        val firstByte = hex.substring(startIndex, startIndex + 2).toInt(16)

        if ((firstByte and 0x80) == 0) {
            return LengthResult(
                length = firstByte,
                nextIndex = startIndex + 2
            )
        }

        val numberOfLengthBytes = firstByte and 0x7F

        require(numberOfLengthBytes != 0) {
            "Indefinite length is not supported in EMV TLV"
        }

        require(numberOfLengthBytes <= 4) {
            "TLV length exceeds supported range"
        }

        require(startIndex + 2 + (numberOfLengthBytes * 2) <= hex.length) {
            "Incomplete long-form length"
        }

        val firstLengthByte = hex.substring(
            startIndex + 2,
            startIndex + 4
        ).toInt(16)

        require(firstLengthByte != 0) {
            "Invalid long-form length encoding"
        }

        var length = 0

        repeat(numberOfLengthBytes) { index ->
            val byteIndex = startIndex + 2 + (index * 2)

            val byte = hex.substring(
                byteIndex,
                byteIndex + 2
            ).toInt(16)

            require(length <= (Int.MAX_VALUE - byte) / 256) {
                "TLV length exceeds supported range"
            }

            length = length * 256 + byte
        }

        require(length >= 128) {
            "Invalid long-form length encoding"
        }

        return LengthResult(
            length = length,
            nextIndex = startIndex + 2 + (numberOfLengthBytes * 2)
        )
    }
}

internal data class LengthResult(
    val length: Int,
    val nextIndex: Int
)
