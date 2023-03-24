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
package io.github.janbarari.gradle

import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.internal.impldep.org.apache.http.MethodNotSupportedException
import java.util.function.BiFunction

class MockProperty<T>(var value: T?): Property<T> {
    override fun get(): T = value!!

    override fun getOrNull(): T? = value

    override fun isPresent(): Boolean = value != null

    @Deprecated("", ReplaceWith("this"))
    override fun forUseAtConfigurationTime(): Provider<T> {
        return this
    }

    override fun finalizeValue() {
        // do nothing
    }

    override fun finalizeValueOnRead() {
        // do nothing
    }

    override fun disallowChanges() {
        // do nothing
    }

    override fun disallowUnsafeRead() {
        // do nothing
    }

    override fun convention(provider: Provider<out T>): Property<T> {
        return this
    }

    override fun convention(value: T?): Property<T> {
        return this
    }

    override fun value(provider: Provider<out T>): Property<T> {
        return this
    }

    override fun value(value: T?): Property<T> {
        return this
    }

    override fun set(provider: Provider<out T>) {
        throw MethodNotSupportedException("set(provider) is not supported in the PropertyMock")
    }

    override fun set(value: T?) {
        this.value = value
    }

    override fun <U : Any?, R : Any?> zip(p0: Provider<U>, p1: BiFunction<in T, in U, out R>): Provider<R> {
        throw MethodNotSupportedException("zip is not supported in the PropertyMock")
    }

    override fun orElse(p0: Provider<out T>): Provider<T> {
        return this
    }

    override fun orElse(value: T): Provider<T> {
        return this
    }

    override fun <S : Any?> flatMap(transformer: Transformer<out Provider<out S>, in T>): Provider<S> {
        throw MethodNotSupportedException("flatMap is not supported in the PropertyMock")
    }

    override fun <S : Any?> map(transformer: Transformer<out S, in T>): Provider<S> {
        throw MethodNotSupportedException("map is not supported in the PropertyMock")
    }

    override fun getOrElse(defaultValue: T): T = value!!
}
