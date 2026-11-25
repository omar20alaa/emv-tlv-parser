package app.emv_tlv_parser.parser

data class TLVParserConfig(
    val maxInputLength: Int = 65_536,
    val maxNestingDepth: Int = 16,
    val maxTags: Int = 1_000
) {
    init {
        require(maxInputLength > 0) {
            "Maximum input length must be positive"
        }

        require(maxNestingDepth >= 0) {
            "Maximum nesting depth cannot be negative"
        }

        require(maxTags > 0) {
            "Maximum number of tags must be positive"
        }
    }
}