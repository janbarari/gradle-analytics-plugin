/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildInitializationServiceTest {

    private lateinit var service: BuildInitializationService

    @BeforeAll
    fun setup() {
        service = BuildInitializationService(mockk())
    }

    @Test
    fun `check reset() function clears the STARTED_AT & INITIALIZED_AT`() {
        BuildInitializationService.STARTED_AT = 38
        BuildInitializationService.reset()

        assertEquals(0, BuildInitializationService.STARTED_AT)
        assertEquals(0, BuildInitializationService.INITIALIZED_AT)
    }

    @Test
    fun `check beforeEvaluate() assigns the INITIALIZED_AT if its not initialized`() {
        service.beforeEvaluate(mockk())
        assert(BuildInitializationService.INITIALIZED_AT > 0)

        BuildInitializationService.INITIALIZED_AT = 10L
        service.beforeEvaluate(mockk())
        assert(BuildInitializationService.INITIALIZED_AT == 10L)
    }

    @Test
    fun `check afterEvaluate() assigns the INITIALIZED_AT if its not initialized`() {
        service.afterEvaluate(mockk(), mockk())
        assert(BuildInitializationService.INITIALIZED_AT > 0)

        BuildInitializationService.INITIALIZED_AT = 10L
        service.afterEvaluate(mockk(), mockk())
        assert(BuildInitializationService.INITIALIZED_AT == 10L)
    }

}