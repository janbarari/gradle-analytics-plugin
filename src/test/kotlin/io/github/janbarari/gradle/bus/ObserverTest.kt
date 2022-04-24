package io.github.janbarari.gradle.bus

import org.gradle.api.invocation.Gradle
import org.gradle.internal.impldep.org.junit.Before
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ObserverTest {

    companion object {
        const val GUID_STANDARD_LENGTH = 36
    }

    @BeforeAll
    fun setup() {
        Bus.enableTestMode()
    }

    @Test
    fun `check the generateGUID generates correct guid string`() {
        val generatedGUID: String = Observer.generateGUID()
        val generatedGUIDByteSize = generatedGUID.toByteArray().size
        assertEquals(true, generatedGUIDByteSize == GUID_STANDARD_LENGTH)
    }

    @Test
    fun `check toString works correctly`() {
        val generatedGUID = Observer.generateGUID()
        val observer = Observer(
            String::class.java,
            generatedGUID,
            {},
            ObserverTest::class.java
        )

        val isStringPackageExists: Boolean = observer.toString().contains("java.lang.String")
        assertEquals(true, isStringPackageExists)

        val isGUIDExists: Boolean = observer.toString().contains(generatedGUID)
        assertEquals(true, isGUIDExists)
    }

    @Test
    fun `test getters`() {
        val generatedGUID = Observer.generateGUID()
        val eventReceiver: ((event: Any) -> Unit) = {

        }

        val observer = Observer(
            String::class.java,
            generatedGUID,
            eventReceiver,
            ObserverTest::class.java
        )

        assertEquals(generatedGUID, observer.guid)
        assertEquals(ObserverTest::class.java, observer.sender)
        assertEquals(String::class.java, observer.observerType)
        assertEquals(eventReceiver::class.java, observer.unit::class.java)
    }

    @Test
    fun `test setters`() {
        val generatedGUID = Observer.generateGUID()
        val eventReceiver: ((event: Any) -> Unit) = {

        }

        val observer = Observer(
            String::class.java,
            generatedGUID,
            eventReceiver,
            ObserverTest::class.java
        )

        val secondGeneratedGUID = Observer.generateGUID()
        observer.guid = secondGeneratedGUID
        assertEquals(secondGeneratedGUID, observer.guid)

        observer.observerType = Int::class.java
        assertEquals(Int::class.java, observer.observerType)

        observer.sender = Gradle::class.java
        assertEquals(Gradle::class.java, observer.sender)

        val secondEventReceiver: ((event: Any) -> Unit) = {

        }
        observer.unit = secondEventReceiver
        assertEquals(secondEventReceiver::class.java, observer.unit::class.java)
    }

    @Test
    fun `test event receives`() {
        val guid = Observer.generateGUID()
        Bus.register<String>(guid) {
            assert(it == "check the event")
        }
        Bus.post("check the event", guid)
    }

}
