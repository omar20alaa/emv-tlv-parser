package app.emv_tlv_parser.parser


internal object TLVTagType {

    fun isConstructed(tag: String): Boolean {
        require(tag.length >= 2) {
            "Invalid tag: $tag"
        }

        val firstByte = tag.substring(0, 2).toInt(16)

        return (firstByte and 0x20) != 0
    }
}