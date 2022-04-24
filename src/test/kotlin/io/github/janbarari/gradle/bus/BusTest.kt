package io.github.janbarari.gradle.bus

import io.github.janbarari.gradle.bus.exception.NotSerializableException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BusTest {

    private val observerGUID = Observer.generateGUID()

    @BeforeAll
    fun setup() {
        Bus.enableTestMode()
    }

    @Test
    fun `check enableTestMode works correctly`() {
        Bus.enableTestMode()
        assertTrue(Bus.isTestMode())
    }

    @Test
    fun `check disableTestMode works correctly`() {
        Bus.disableTestMode()
        assertTrue(Bus.isTestMode().not())
    }

    @Test
    fun `check observer registered in Bus`() {
        assertTrue(Bus.getObservers().any { it.guid == observerGUID })
    }

    @Test
    fun `test the event receives in observer callback`() {
        Bus.register<String>(observerGUID) {
            assertTrue(
                it == "check the event"
            )
        }
        Bus.post("check the event")
    }

    @Test
    fun `check the event receives when Bus sends the event with guid`() {
        Bus.register<String>(observerGUID) {
            assertTrue(
                it == "check the event"
            )
        }
        Bus.post("check the event", observerGUID)
    }

    @Test
    fun `check the event callback should not be invoked when Bus sends the same event with wrong guid`() {
        Bus.register<String>(observerGUID) {
            assert(false)
        }
        Bus.post("check the event", Observer.generateGUID())
    }

    @Test
    fun `check unregisterAll works correctly`() {
        Bus.register<String>(observerGUID) {}
        Bus.unregisterAll()
        assertTrue(
            Bus.getObservers().isEmpty()
        )
    }

    @Test
    fun `check the NotSerializableException thrown when the type is not serializable`() {
        try {
            Bus.register<String>(observerGUID) {}
            Bus.post(Any())
        } catch (e: Throwable) {
            assertTrue(e is NotSerializableException)
        }
    }

    @Test
    fun `check the enableTestMode works correctly`() {
        try {
            Bus.enableTestMode()
            Bus.register<String>(observerGUID) {
                throw Exception("FakeException")
            }
            Bus.post("check the event")
        } catch (e: Throwable) {
            assertTrue(e.message == "FakeException")
        }
    }

    @Test
    fun `check the unregister with wrong guid is skipped`() {
        Bus.register<String>(observerGUID){}
        Bus.unregister("FAKE_GUID")
        assertTrue(
            Bus.getObservers().isNotEmpty()
        )
    }

}
