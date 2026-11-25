
package app.emv_tlv_parser.parser

internal class TLVTagReader {

    fun read(hex: String, startIndex: Int): Int {
        require(startIndex + 2 <= hex.length) {
            "Unexpected end of data while reading tag"
        }

        val firstByte = hex.substring(startIndex, startIndex + 2).toInt(16)

        if ((firstByte and 0x1F) != 0x1F) {
            return startIndex + 2
        }

        var index = startIndex + 2
        var tagByteCount = 1

        while (true) {
            require(tagByteCount < 3) {
                "EMV tag cannot exceed 3 bytes"
            }

            require(index + 2 <= hex.length) {
                "Incomplete multi-byte tag"
            }

            val nextByte = hex.substring(index, index + 2).toInt(16)

            if (tagByteCount == 1) {
                require((nextByte and 0x7F) != 0) {
                    "Invalid multi-byte tag encoding"
                }
            }

            index += 2
            tagByteCount++

            if ((nextByte and 0x80) == 0) {
                break
            }
        }

        return index
    }
}
