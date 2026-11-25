package app.emv_tlv_parser.model

data class EMVTag(
    val tag: String,
    val length: Int,
    val value: String,
    val name: String? = null,
    val description: String? = null,
    val decodedValue: String? = null,
    val children: List<EMVTag> = emptyList()
)