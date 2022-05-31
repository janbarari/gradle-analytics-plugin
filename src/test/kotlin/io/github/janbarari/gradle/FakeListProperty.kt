package io.github.janbarari.gradle

import io.github.janbarari.gradle.extension.isNotNull
import org.gradle.api.Transformer
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import java.rmi.UnexpectedException
import java.util.function.BiFunction

class FakeListProperty<T: Any>(private var value: List<T>?): ListProperty<T> {

    override fun get(): MutableList<T> {
        return value!!.toMutableList()
    }

    override fun getOrNull(): MutableList<T>? {
        return value?.toMutableList()
    }

    override fun isPresent(): Boolean {
        return value.isNotNull()
    }

    @Deprecated("Deprecated in Java")
    override fun forUseAtConfigurationTime(): Provider<MutableList<T>> {
        throw UnexpectedException("FakeListProperty class does not support #forUseAtConfigurationTime")
    }

    override fun finalizeValue() {
        throw UnexpectedException("FakeListProperty class does not support #finalizeValue")
    }

    override fun finalizeValueOnRead() {
        throw UnexpectedException("FakeListProperty class does not support #finalizeValueOnRead")
    }

    override fun disallowChanges() {
        throw UnexpectedException("FakeListProperty class does not support #disallowChanges")
    }

    override fun disallowUnsafeRead() {
        throw UnexpectedException("FakeListProperty class does not support #disallowUnsafeRead")
    }

    override fun empty(): ListProperty<T> {
        throw UnexpectedException("FakeListProperty class does not support #empty")
    }

    override fun convention(provider: Provider<out MutableIterable<T>>): ListProperty<T> {
        throw UnexpectedException("FakeListProperty class does not support #convention(Provider)")
    }

    override fun convention(elements: MutableIterable<T>?): ListProperty<T> {
        throw UnexpectedException("FakeListProperty class does not support #convention(T)")
    }

    override fun addAll(provider: Provider<out MutableIterable<T>>) {
        throw UnexpectedException("FakeListProperty class does not support #addAll(Provider)")
    }

    override fun addAll(elements: MutableIterable<T>) {
        throw UnexpectedException("FakeListProperty class does not support #addAll")
    }

    override fun addAll(vararg elements: T) {
        throw UnexpectedException("FakeListProperty class does not support #addAll(T)")
    }

    override fun add(provider: Provider<out T>) {
        throw UnexpectedException("FakeListProperty class does not support #add(Provider)")
    }

    override fun add(element: T) {
        throw UnexpectedException("FakeListProperty class does not support #add(T)")
    }

    override fun value(provider: Provider<out MutableIterable<T>>): ListProperty<T> {
        throw UnexpectedException("FakeListProperty class does not support #value(Provider)")
    }

    override fun value(elements: MutableIterable<T>?): ListProperty<T> {
        throw UnexpectedException("FakeListProperty class does not support #value(T)")
    }

    override fun set(provider: Provider<out MutableIterable<T>>) {
        throw UnexpectedException("FakeListProperty class does not support #set(Provider)")
    }

    override fun set(elements: MutableIterable<T>?) {
        throw UnexpectedException("FakeListProperty class does not support #set(T)")
    }

    override fun <B : Any?, R : Any?> zip(right: Provider<B>, combiner: BiFunction<MutableList<T>, B, R>): Provider<R> {
        throw UnexpectedException("FakeListProperty class does not support #zip")
    }

    override fun orElse(provider: Provider<out MutableList<T>>): Provider<MutableList<T>> {
        throw UnexpectedException("FakeListProperty class does not support #orElse(Provider)")
    }

    override fun orElse(value: MutableList<T>): Provider<MutableList<T>> {
        throw UnexpectedException("FakeListProperty class does not support #orElse(T)")
    }

    override fun <S : Any?> flatMap(transformer: Transformer<out Provider<out S>, in MutableList<T>>): Provider<S> {
        throw UnexpectedException("FakeListProperty class does not support #flatMap")
    }

    override fun <S : Any?> map(transformer: Transformer<out S, in MutableList<T>>): Provider<S> {
        throw UnexpectedException("FakeListProperty class does not support #map")
    }

    override fun getOrElse(defaultValue: MutableList<T>): MutableList<T> {
        throw UnexpectedException("FakeListProperty class does not support #getOrElse")
    }

}
