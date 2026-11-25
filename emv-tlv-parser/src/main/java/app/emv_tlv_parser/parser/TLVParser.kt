
package app.emv_tlv_parser.parser

import app.emv_tlv_parser.decoder.EMVValueDecoder
import app.emv_tlv_parser.dictionary.EMVTagDictionary
import app.emv_tlv_parser.model.EMVTag
import app.emv_tlv_parser.model.TLVParseResult

class TLVParser(
    private val config: TLVParserConfig = TLVParserConfig()
) {

    private val tagReader = TLVTagReader()
    private val lengthReader = TLVLengthReader()

    fun parse(hex: String): TLVParseResult {

        if (hex.length > config.maxInputLength) {
            return TLVParseResult.Error("Maximum input length exceeded")
        }

        if (hex.isBlank()) {
            return TLVParseResult.Error("TLV data cannot be empty")
        }

        val normalizedHex = hex.replace("\\s".toRegex(), "").uppercase()

        if (!normalizedHex.matches(Regex("^[0-9A-F]+$"))) {
            return TLVParseResult.Error("Invalid hexadecimal data")
        }

        if (normalizedHex.length % 2 != 0) {
            return TLVParseResult.Error("Invalid TLV data length")
        }

        return try {

            val tagCounter = intArrayOf(0)

            val tags = parseTags(
                hex = normalizedHex,
                depth = 0,
                tagCounter = tagCounter
            )

            TLVParseResult.Success(tags)

        } catch (exception: IllegalArgumentException) {

            TLVParseResult.Error(
                exception.message ?: "Invalid TLV data"
            )
        }
    }

    private fun parseTags(
        hex: String,
        depth: Int,
        tagCounter: IntArray
    ): List<EMVTag> {

        require(depth <= config.maxNestingDepth) {
            "Maximum TLV nesting depth exceeded"
        }

        val tags = mutableListOf<EMVTag>()
        var index = 0

        while (index < hex.length) {

            val tagEndIndex = tagReader.read(hex, index)

            val tag = hex.substring(index, tagEndIndex)

            val lengthResult = lengthReader.read(
                hex, tagEndIndex
            )

            val length = lengthResult.length
            val valueStartIndex = lengthResult.nextIndex

            require(length <= (hex.length - valueStartIndex) / 2) {
                "Value length exceeds available data for tag $tag"
            }

            val valueEndIndex = valueStartIndex + (length * 2)

            val value = hex.substring(
                valueStartIndex, valueEndIndex
            )

            tagCounter[0]++

            require(tagCounter[0] <= config.maxTags) {
                "Maximum number of TLV tags exceeded"
            }

            val children = if (TLVTagType.isConstructed(tag)) {

                parseTags(
                    hex = value,
                    depth = depth + 1,
                    tagCounter = tagCounter
                )

            } else {
                emptyList()
            }

            val tagInfo = EMVTagDictionary.getTagInfo(tag)

            val decodedValue = EMVValueDecoder.decode(
                tag = tag,
                value = value
            )

            tags.add(
                EMVTag(
                    tag = tag,
                    length = length,
                    value = value,
                    name = tagInfo?.name,
                    description = tagInfo?.description,
                    decodedValue = decodedValue,
                    children = children
                )
            )

            index = valueEndIndex
        }

        return tags
    }
}
