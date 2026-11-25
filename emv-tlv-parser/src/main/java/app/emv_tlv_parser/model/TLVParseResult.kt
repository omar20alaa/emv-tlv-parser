package app.emv_tlv_parser.model

sealed class TLVParseResult {

    data class Success(
        val tags: List<EMVTag>
    ) : TLVParseResult()

    data class Error(
        val message: String
    ) : TLVParseResult()

}