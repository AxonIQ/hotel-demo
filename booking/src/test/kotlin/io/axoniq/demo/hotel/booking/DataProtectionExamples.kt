package io.axoniq.demo.hotel.booking

import io.axoniq.dataprotection.api.*
import io.axoniq.dataprotection.cryptoengine.InMemoryCryptoEngine
import io.axoniq.demo.hotel.booking.command.api.AccountRegisteredEvent
import io.axoniq.demo.hotel.booking.command.dp.provider.DateOfBirthReplacementProvider
import org.apache.logging.log4j.util.Strings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.time.LocalDate
import java.util.*

@Disabled("Needs -Daxoniq.dataprotection.license=xxxx with the path to a valid license as a env var")
class DataProtectionExamplesTest {
    private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private val cryptoEngine = InMemoryCryptoEngine()
    private val dateOfBirthReplacementProvider = DateOfBirthReplacementProvider()
    private val fieldEncrypter = FieldEncrypter(cryptoEngine)

    @Test
    fun encryptAccountRegisteredEventTest() {
        val accountRegisteredEvent = AccountRegisteredEvent(UUID.randomUUID(), "Name", "Welcome1")
        fieldEncrypter.encrypt(accountRegisteredEvent)
        Assertions.assertNotEquals("Welcome1", accountRegisteredEvent.password)
        logger.info("Encrypted password [${accountRegisteredEvent.password}]")

        fieldEncrypter.decrypt(accountRegisteredEvent)
        Assertions.assertEquals("Welcome1", accountRegisteredEvent.password)

    }

    @Test
    fun deepPersonalDataTest() {
        val accountDataRegisteredEvent =
                AccountDataRegisteredEvent(UUID.randomUUID(), Address(
                        "Broadway",
                        10,
                        null,
                        "zip",
                        "city"))
        fieldEncrypter.encrypt(accountDataRegisteredEvent)
        logger.info("Encrypted address [${accountDataRegisteredEvent.address}]")
        Assertions.assertNotEquals("Broadway", accountDataRegisteredEvent.address.street)
        Assertions.assertNotEquals("city", accountDataRegisteredEvent.address.city)
        Assertions.assertEquals("zip", accountDataRegisteredEvent.address.zip)
        Assertions.assertEquals(0, accountDataRegisteredEvent.address.houseNumber)
        Assertions.assertNotNull(accountDataRegisteredEvent.address.houseNumberEncrypted)

        fieldEncrypter.decrypt(accountDataRegisteredEvent)
        Assertions.assertEquals("Broadway", accountDataRegisteredEvent.address.street)
        Assertions.assertEquals("city", accountDataRegisteredEvent.address.city)
        Assertions.assertEquals("zip", accountDataRegisteredEvent.address.zip)
        Assertions.assertEquals(10, accountDataRegisteredEvent.address.houseNumber)
        Assertions.assertNull(accountDataRegisteredEvent.address.houseNumberEncrypted)
    }

    @Test
    fun deepPersonalDataInMapTest() {
        val addressId1 = UUID.randomUUID()
        val addressId2 = UUID.randomUUID()
        val addressMap = mapOf(addressId1 to Address("Broadway", 10, null, "zip1", "city1"), addressId2 to Address("Broadway", 12, null, "zip2", "city2"))
        val addressesRegisteredEvent = AddressesRegisteredEvent(UUID.randomUUID(), addressMap)
        fieldEncrypter.encrypt(addressesRegisteredEvent)

        val address1 = addressesRegisteredEvent.addressMap[addressId1]
        Assertions.assertFalse("Broadway" == address1?.street)
        Assertions.assertFalse("city1" == address1?.city)
        Assertions.assertEquals("zip1", address1?.zip)
        Assertions.assertEquals(0, address1?.houseNumber)
        Assertions.assertNotNull(address1?.houseNumberEncrypted)

        val address2 = addressesRegisteredEvent.addressMap[addressId2]
        Assertions.assertFalse("Broadway" == address2?.street)
        Assertions.assertFalse("city2" == address2?.city)
        Assertions.assertEquals("zip2", address2?.zip)
        Assertions.assertEquals(0, address2?.houseNumber)
        Assertions.assertNotNull(address2?.houseNumberEncrypted)
    }


    @Test
    fun cryptoShreddingTest() {
        val accountDataRegisteredEvent = AccountDataRegisteredEvent(UUID.randomUUID(),
                Address(street = "Broadway", houseNumber = 10, zip = "zip", city = "city"))
        fieldEncrypter.encrypt(accountDataRegisteredEvent)

        cryptoEngine.deleteKey(accountDataRegisteredEvent.accountId.toString())
        fieldEncrypter.decrypt(accountDataRegisteredEvent)
        // the street has been replaced with the replacement value
        Assertions.assertEquals("replacementValue", accountDataRegisteredEvent.address.street)
        // the city has been replaced with the default replacement value which is an empty String
        Assertions.assertEquals(Strings.EMPTY, accountDataRegisteredEvent.address.city)
        // the zip has not been replaced since there was no PersonalData annotation
        Assertions.assertEquals("zip", accountDataRegisteredEvent.address.zip)
        // the house number has been replaced with the default replacement value which is 0
        Assertions.assertEquals(0, accountDataRegisteredEvent.address.houseNumber)
        // the encrypted house number is null again
        Assertions.assertNull(accountDataRegisteredEvent.address.houseNumberEncrypted)
    }

    @Test
    fun cryptoShreddingWithReplacementValueTest() {
        val dateOfBirthFieldEncrypter = FieldEncrypter(cryptoEngine, dateOfBirthReplacementProvider)
        val dateOfBirthRegisteredEvent =
                DateOfBirthRegisteredEvent(UUID.randomUUID(), LocalDate.parse("1998-07-07"))

        dateOfBirthFieldEncrypter.encrypt(dateOfBirthRegisteredEvent)
        Assertions.assertNull(dateOfBirthRegisteredEvent.dateOfBirth)
        logger.info("birth date encrypted [${dateOfBirthRegisteredEvent.dateOfBirthEncrypted}]")

        cryptoEngine.deleteKey(dateOfBirthRegisteredEvent.accountId.toString())
        dateOfBirthFieldEncrypter.decrypt(dateOfBirthRegisteredEvent)
        Assertions.assertNull(dateOfBirthRegisteredEvent.dateOfBirthEncrypted)
        Assertions.assertEquals(LocalDate.parse("1998-01-01"), dateOfBirthRegisteredEvent.dateOfBirth)
    }
}

data class AddressesRegisteredEvent(
        @DataSubjectId
        val accountId: UUID,
        @DeepPersonalData(scope = Scope.VALUE)
        val addressMap: Map<UUID, Address> = mapOf()
)

data class AccountDataRegisteredEvent(
        @DataSubjectId
        val accountId: UUID,
        @DeepPersonalData
        val address: Address
)

data class Address(
        @PersonalData(replacement = "replacementValue")
        val street: String,
        @SerializedPersonalData
        val houseNumber: Int,
        val houseNumberEncrypted: ByteArray? = null,
        val zip: String,
        @PersonalData
        val city: String)

data class DateOfBirthRegisteredEvent(
        @DataSubjectId
        val accountId: UUID,
        @SerializedPersonalData(replacement = "YEAR_ONLY")
        val dateOfBirth: LocalDate,
        val dateOfBirthEncrypted: ByteArray? = null)
