package app.emv_tlv_parser

import app.emv_tlv_parser.model.TLVParseResult
import app.emv_tlv_parser.parser.TLVParser
import app.emv_tlv_parser.parser.TLVParserConfig
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class TLVParserTest {

    private val parser = TLVParser()

    @Test
    fun `parse valid single TLV`() {

        val result = parser.parse("9F0206000000010000")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(1, tags.size)
        assertEquals("9F02", tags[0].tag)
        assertEquals(6, tags[0].length)
        assertEquals("000000010000", tags[0].value)
        assertEquals("Amount, Authorised", tags[0].name)
        assertEquals("100.00", tags[0].decodedValue)
    }

    @Test
    fun `parse multiple TLV tags`() {

        val result = parser.parse(
            "9F02060000000100009A03261110"
        )

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(2, tags.size)
        assertEquals("9F02", tags[0].tag)
        assertEquals("9A", tags[1].tag)
        assertEquals("10/11/26", tags[1].decodedValue)
    }

    @Test
    fun `reject empty input`() {

        val result = parser.parse("")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `reject invalid hexadecimal input`() {

        val result = parser.parse("9F02GG")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse constructed TLV with nested tags`() {

        val result = parser.parse(
            "700A5A084761739001010010"
        )

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(1, tags.size)

        val parentTag = tags[0]

        assertEquals("70", parentTag.tag)
        assertEquals(1, parentTag.children.size)

        val childTag = parentTag.children[0]

        assertEquals("5A", childTag.tag)
        assertEquals("4761739001010010", childTag.value)
        assertEquals(
            "Application Primary Account Number", childTag.name
        )
    }

    @Test
    fun `reject incomplete nested TLV`() {

        val result = parser.parse(
            "70065A084761739001010010"
        )

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse long form length`() {

        val value = "AA".repeat(128)

        val tlv = "5A8180$value"

        val result = parser.parse(tlv)

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(1, tags.size)
        assertEquals("5A", tags[0].tag)
        assertEquals(128, tags[0].length)
        assertEquals(value, tags[0].value)
    }


    @Test
    fun `should reject value length exceeding available data`() {
        val parser = TLVParser()

        val result = parser.parse("5A084761")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `should reject incomplete long form length`() {
        val parser = TLVParser()

        val result = parser.parse("5A8180AA")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `should reject input exceeding maximum length`() {
        val parser = TLVParser(
            TLVParserConfig(maxInputLength = 4)
        )

        val result = parser.parse("9F020600")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `should reject number of tags exceeding maximum`() {
        val parser = TLVParser(
            TLVParserConfig(maxTags = 1)
        )

        val result = parser.parse("9F02060000000100009A03261110")

        assertTrue(result is TLVParseResult.Error)
    }


    @Test
    fun `should decode transaction type`() {
        val parser = TLVParser()

        val result = parser.parse("9C0100")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("9C", tags[0].tag)
        assertEquals(1, tags[0].length)
        assertEquals("00", tags[0].value)
        assertEquals("Purchase", tags[0].decodedValue)
    }


    @Test
    fun `should decode application transaction counter`() {
        val parser = TLVParser()

        val result = parser.parse("9F36020015")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("9F36", tags[0].tag)
        assertEquals(2, tags[0].length)
        assertEquals("0015", tags[0].value)
        assertEquals("21", tags[0].decodedValue)
    }

    @Test
    fun `should decode unpredictable number`() {
        val parser = TLVParser()

        val result = parser.parse("9F3704A1B2C3D4")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("9F37", tags[0].tag)
        assertEquals(4, tags[0].length)
        assertEquals("A1B2C3D4", tags[0].value)
        assertEquals("A1B2C3D4", tags[0].decodedValue)
    }


    @Test
    fun `should decode PAN sequence number`() {
        val parser = TLVParser()

        val result = parser.parse("5F340101")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("5F34", tags[0].tag)
        assertEquals(1, tags[0].length)
        assertEquals("01", tags[0].value)
        assertEquals("01", tags[0].decodedValue)
    }

    @Test
    fun `should decode application cryptogram`() {
        val parser = TLVParser()

        val result = parser.parse("9F2608AABBCCDDEEFF0011")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("9F26", tags[0].tag)
        assertEquals(8, tags[0].length)
        assertEquals("AABBCCDDEEFF0011", tags[0].value)
        assertEquals("AABBCCDDEEFF0011", tags[0].decodedValue)
    }


    @Test
    fun `should decode cryptogram information data`() {
        val parser = TLVParser()

        val result = parser.parse("9F270180")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("9F27", tags[0].tag)
        assertEquals(1, tags[0].length)
        assertEquals("80", tags[0].value)
        assertEquals(
            "ARQC - Authorization Request Cryptogram", tags[0].decodedValue
        )
    }


    @Test
    fun `should mask PAN value`() {
        val parser = TLVParser()

        val result = parser.parse("5A084761739001010010")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("5A", tags[0].tag)
        assertEquals(8, tags[0].length)
        assertEquals("4761739001010010", tags[0].value)
        assertEquals("************0010", tags[0].decodedValue)
    }


    @Test
    fun `should decode application expiration date`() {
        val parser = TLVParser()

        val result = parser.parse("5F2403261231")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("5F24", tags[0].tag)
        assertEquals(3, tags[0].length)
        assertEquals("261231", tags[0].value)
        assertEquals("12/26", tags[0].decodedValue)
    }


    @Test
    fun `should reject invalid expiration month`() {
        val parser = TLVParser()

        val result = parser.parse("5F2403261331")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(null, tags[0].decodedValue)
    }

    @Test
    fun `should reject invalid expiration day`() {
        val parser = TLVParser()

        val result = parser.parse("5F2403260231")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(null, tags[0].decodedValue)
    }

    @Test
    fun `should accept leap year expiration date`() {
        val parser = TLVParser()

        val result = parser.parse("5F2403240229")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals("02/24", tags[0].decodedValue)
    }

    @Test
    fun `should reject invalid leap year expiration date`() {
        val parser = TLVParser()

        val result = parser.parse("5F2403260229")

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(null, tags[0].decodedValue)
    }

    @Test
    fun `should parse multiple EMV transaction tags and decode values`() {
        val parser = TLVParser()

        val tlv = "9F0206000000010000" + "9C0100" + "9F36020015" + "9F3704A1B2C3D4"

        val result = parser.parse(tlv)

        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(4, tags.size)

        assertEquals("100.00", tags[0].decodedValue)

        assertEquals("Purchase", tags[1].decodedValue)

        assertEquals("21", tags[2].decodedValue)

        assertEquals("A1B2C3D4", tags[3].decodedValue)
    }

    @Test
    fun `parse unknown tag successfully`() {
        val result = parser.parse("DF81010101")
        assertTrue(result is TLVParseResult.Success)

        val tag = (result as TLVParseResult.Success).tags.first()

        assertEquals("DF8101", tag.tag)
        assertEquals(1, tag.length)
        assertEquals("01", tag.value)
        assertNull(tag.name)
        assertNull(tag.decodedValue)
    }

    @Test
    fun `parse incomplete multi byte tag returns error`() {
        val result = parser.parse("9F")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse nested tags beyond maximum depth returns error`() {
        val limitedParser = TLVParser(
            TLVParserConfig(maxNestingDepth = 1)
        )

        val result = limitedParser.parse("7006E1045A020102")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse three byte tag successfully`() {
        val result = parser.parse("DF81010101")

        assertTrue(result is TLVParseResult.Success)

        val tag = (result as TLVParseResult.Success).tags.first()

        assertEquals("DF8101", tag.tag)
    }

    @Test
    fun `parse four byte tag returns error`() {
        val result = parser.parse("DF8181010101")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse hex input with whitespace successfully`() {
        val result = parser.parse("9F02 06 000000010000")

        assertTrue(result is TLVParseResult.Success)

        val tag = (result as TLVParseResult.Success).tags.first()

        assertEquals("9F02", tag.tag)
        assertEquals("000000010000", tag.value)
    }

    @Test
    fun `parse odd length hex input returns error`() {
        val result = parser.parse("9F020")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse nested tags at maximum depth successfully`() {
        val limitedParser = TLVParser(
            TLVParserConfig(maxNestingDepth = 1)
        )

        val result = limitedParser.parse("70045A020102")

        assertTrue(result is TLVParseResult.Success)

        val outerTag = (result as TLVParseResult.Success).tags.first()

        assertEquals("70", outerTag.tag)
        assertEquals("5A", outerTag.children.first().tag)
    }

    @Test
    fun `parse long form length exceeding four bytes returns error`() {
        val result = parser.parse("5A850000000001")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse overflowing length returns error`() {
        val result = parser.parse("5A84FFFFFFFF")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse tags exceeding maximum count returns error`() {
        val limitedParser = TLVParser(
            TLVParserConfig(maxTags = 2)
        )

        val result = limitedParser.parse("9C01009F02060000000100009F36020015")

        assertTrue(result is TLVParseResult.Error)
    }


    @Test
    fun `parse unknown nested tag successfully`() {
        val result = parser.parse("7005DF81010101")

        assertTrue(result is TLVParseResult.Success)

        val outerTag = (result as TLVParseResult.Success).tags.first()
        val childTag = outerTag.children.first()

        assertEquals("70", outerTag.tag)
        assertEquals("DF8101", childTag.tag)
        assertEquals(1, childTag.length)
        assertEquals("01", childTag.value)
        assertNull(childTag.name)
        assertNull(childTag.decodedValue)
    }


    @Test
    fun `parse incomplete long form length returns error`() {
        val result = parser.parse("5A8201")

        assertTrue(result is TLVParseResult.Error)
    }


    @Test
    fun `parse empty constructed tag successfully`() {
        val result = parser.parse("7000")

        assertTrue(result is TLVParseResult.Success)

        val tag = (result as TLVParseResult.Success).tags.first()

        assertEquals("70", tag.tag)
        assertEquals(0, tag.length)
        assertTrue(tag.children.isEmpty())
    }

    @Test
    fun `parse input at maximum length successfully`() {
        val input = "9C0100"

        val limitedParser = TLVParser(
            TLVParserConfig(maxInputLength = input.length)
        )

        val result = limitedParser.parse(input)

        assertTrue(result is TLVParseResult.Success)
    }

    @Test
    fun `parse incomplete constructed value returns error`() {
        val result = parser.parse("70035A0201")

        assertTrue(result is TLVParseResult.Error)
    }

    @Test
    fun `parse multiple constructed tags successfully`() {
        val result = parser.parse("70035A010171045F240102")
        assertTrue(result is TLVParseResult.Success)

        val tags = (result as TLVParseResult.Success).tags

        assertEquals(2, tags.size)
        assertEquals("70", tags[0].tag)
        assertEquals("5A", tags[0].children.first().tag)
        assertEquals("71", tags[1].tag)
        assertEquals("5F24", tags[1].children.first().tag)
    }


}