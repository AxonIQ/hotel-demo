package io.axoniq.demo.hotel.booking.command.dp.provider

import io.axoniq.dataprotection.api.ReplacementValueProvider
import java.lang.reflect.Field
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.Month

class DateOfBirthReplacementProvider : ReplacementValueProvider() {
    override fun replacementValue(clazz: Class<*>?, field: Field?, fieldType: Type?, groupName: String?,
                                  replacement: String?, storedPartialValue: ByteArray?): Any? {
        if (fieldType == LocalDate::class.java && replacement == "YEAR_ONLY" && storedPartialValue != null) {
            val buffer = ByteBuffer.allocate(Integer.BYTES)
            buffer.put(storedPartialValue)
            buffer.flip()
            return LocalDate.of(buffer.getInt(), Month.JANUARY, 1)
        } else return super.replacementValue(clazz, field, fieldType, groupName, replacement, storedPartialValue)
    }

    override fun partialValueForStorage(clazz: Class<*>?, field: Field?, fieldType: Type?, groupName: String?,
                                        replacement: String?, inputValue: Any?): ByteArray? {
        if (fieldType == LocalDate::class.java &&
                replacement == "YEAR_ONLY" &&
                inputValue!!::class.java.isAssignableFrom(LocalDate::class.java)) {
            val buffer = ByteBuffer.allocate(Integer.BYTES)
            buffer.putInt((inputValue as LocalDate).year)
            return buffer.array()
        } else {
            return super.partialValueForStorage(clazz, field, fieldType, groupName, replacement, inputValue)
        }
    }
}