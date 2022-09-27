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
package io.github.janbarari.gradle.analytics.metric.modulestimeline.render

import io.github.janbarari.gradle.analytics.domain.model.report.ModulesTimelineReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.whenNotNull

class CreateModulesTimelineReportStage(
    private val branch: String,
    private val getModulesTimelineUseCase: GetModulesTimelineUseCase
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val temp = getModulesTimelineUseCase.execute(branch)
        return input.apply {
            temp.whenNotNull {
                modulesTimelineReport = ModulesTimelineReport(
                    start = start,
                    end = end,
                    createdAt = createdAt,
                    modules = modules
                )
            }
        }
    }

}
