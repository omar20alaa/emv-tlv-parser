package app.emv_tlv_parser.decoder

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal object TransactionDateDecoder {

    fun decode(value: String): String? {

        if (value.length != 6) return null

        if (!value.matches(Regex("^[0-9]{6}$"))) {
            return null
        }

        return try {
            val year = value.substring(0, 2).toInt()
            val month = value.substring(2, 4).toInt()
            val day = value.substring(4, 6).toInt()

            val fullYear = if (year >= 50) {
                1900 + year
            } else {
                2000 + year
            }

            val calendar = Calendar.getInstance().apply {
                isLenient = false
                set(fullYear, month - 1, day)
            }

            calendar.time

            SimpleDateFormat(
                "dd/MM/yy", Locale.US
            ).format(calendar.time)

        } catch (exception: Exception) {
            null
        }
    }
}