/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.specs.Spec
import org.gradle.internal.impldep.org.apache.http.MethodNotSupportedException
import java.util.function.BiFunction

class MockSetProperty<T>(var value: MutableSet<T>?): SetProperty<T> {
    override fun get(): MutableSet<T> = value!!

    override fun getOrNull(): MutableSet<T>? = value
    override fun filter(spec: Spec<in MutableSet<T>>): Provider<MutableSet<T>> {
        return this
    }

    override fun isPresent(): Boolean = value != null

    @Deprecated("", ReplaceWith("this"))
    override fun forUseAtConfigurationTime(): Provider<MutableSet<T>> {
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

    override fun empty(): SetProperty<T> {
        return this
    }

    override fun convention(provider: Provider<out MutableIterable<T>>): SetProperty<T> {
        return this
    }

    override fun convention(elements: MutableIterable<T>?): SetProperty<T> {
        return this
    }

    override fun addAll(provider: Provider<out MutableIterable<T>>) {
        throw MethodNotSupportedException("addAll(provider) is not supported in the SetPropertyMock")
    }

    override fun addAll(elements: MutableIterable<T>) {
        value?.addAll(elements)
    }

    override fun addAll(vararg elements: T) {
        value?.addAll(elements)
    }

    override fun add(provider: Provider<out T>) {
        throw MethodNotSupportedException("add(provider) is not supported in the ListPropertyMock")
    }

    override fun add(element: T) {
        value?.add(element)
    }

    override fun value(provider: Provider<out MutableIterable<T>>): SetProperty<T> {
        return this
    }

    override fun value(elements: MutableIterable<T>?): SetProperty<T> {
        return this
    }

    override fun set(provider: Provider<out MutableIterable<T>>) {
        throw MethodNotSupportedException("set(provider) is not supported in the SetPropertyMock")
    }

    override fun set(elements: MutableIterable<T>?) {
        this.value = elements?.toMutableSet()
    }

    override fun <U : Any?, R : Any?> zip(
        p0: Provider<U>,
        p1: BiFunction<in MutableSet<T>, in U, out R>
    ): Provider<R> {
        throw MethodNotSupportedException("zip is not supported in the SetPropertyMock")
    }

    override fun orElse(p0: Provider<out MutableSet<T>>): Provider<MutableSet<T>> {
        return this
    }

    override fun orElse(value: MutableSet<T>): Provider<MutableSet<T>> {
        return this
    }

    override fun <S : Any?> flatMap(transformer: Transformer<out Provider<out S>, in MutableSet<T>>): Provider<S> {
        throw MethodNotSupportedException("flatMap is not supported in the SetPropertyMock")
    }

    override fun <S : Any?> map(transformer: Transformer<out S, in MutableSet<T>>): Provider<S> {
        throw MethodNotSupportedException("map is not supported in the SetPropertyMock")
    }

    override fun getOrElse(defaultValue: MutableSet<T>): MutableSet<T> {
        return value!!
    }

}
