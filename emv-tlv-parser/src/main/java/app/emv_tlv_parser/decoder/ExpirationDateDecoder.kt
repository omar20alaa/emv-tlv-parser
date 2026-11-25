package app.emv_tlv_parser.decoder

import java.util.Calendar

internal object ExpirationDateDecoder {

    fun decode(value: String): String? {

        if (value.length != 6) return null

        if (!value.matches(Regex("^[0-9]{6}$"))) {
            return null
        }

        return try {

            val year = value.substring(0, 2).toInt()
            val month = value.substring(2, 4).toInt()
            val day = value.substring(4, 6).toInt()

            if (month !in 1..12) return null

            val fullYear = if (year >= 50) {
                1900 + year
            } else {
                2000 + year
            }

            val calendar = Calendar.getInstance().apply {
                isLenient = false
                clear()
                set(fullYear, month - 1, day)
            }

            calendar.time

            "%02d/%02d".format(month, year)

        } catch (exception: Exception) {
            null
        }
    }
}