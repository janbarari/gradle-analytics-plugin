package io.github.janbarari.gradle

import io.github.janbarari.gradle.extension.isNotNull
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.rmi.UnexpectedException
import java.util.function.BiFunction

class FakeProperty<T: Any>(private var value: T?): Property<T> {

    override fun get(): T {
        return value!!
    }

    override fun getOrNull(): T? {
        return value
    }

    override fun isPresent(): Boolean {
        return value.isNotNull()
    }

    @Deprecated("Deprecated in Java")
    override fun forUseAtConfigurationTime(): Provider<T> {
        throw UnexpectedException("FakeProperty class does not support #forUseAtConfigurationTime")
    }

    override fun finalizeValue() {
        throw UnexpectedException("FakeProperty class does not support #finalizeValue")
    }

    override fun finalizeValueOnRead() {
        throw UnexpectedException("FakeProperty class does not support #finalizeValueOnRead")
    }

    override fun disallowChanges() {
        throw UnexpectedException("FakeProperty class does not support #disallowChanges")
    }

    override fun disallowUnsafeRead() {
        throw UnexpectedException("FakeProperty class does not support #disallowUnsafeRead")
    }

    override fun convention(provider: Provider<out T>): Property<T> {
        throw UnexpectedException("FakeProperty class does not support #convention(Provider)")
    }

    override fun convention(value: T?): Property<T> {
        throw UnexpectedException("FakeProperty class does not support #convention(T)")
    }

    override fun value(provider: Provider<out T>): Property<T> {
        throw UnexpectedException("FakeProperty class does not support #value(Provider)")
    }

    override fun value(value: T?): Property<T> {
        throw UnexpectedException("FakeProperty class does not support #value(T)")
    }

    override fun set(provider: Provider<out T>) {
        throw UnexpectedException("FakeProperty class does not support #set(provider)")
    }

    override fun set(value: T?) {
        this.value = value!!
    }

    override fun <B : Any?, R : Any?> zip(right: Provider<B>, combiner: BiFunction<T, B, R>): Provider<R> {
        throw UnexpectedException("FakeProperty class does not support #zip")
    }

    override fun orElse(provider: Provider<out T>): Provider<T> {
        throw UnexpectedException("FakeProperty class does not support #orElse(provider)")
    }

    override fun orElse(value: T): Provider<T> {
        throw UnexpectedException("FakeProperty class does not support #orElse(T)")
    }

    override fun <S : Any?> flatMap(transformer: Transformer<out Provider<out S>, in T>): Provider<S> {
        throw UnexpectedException("FakeProperty class does not support #flatMap")
    }

    override fun <S : Any?> map(transformer: Transformer<out S, in T>): Provider<S> {
        throw UnexpectedException("FakeProperty class does not support #map")
    }

    override fun getOrElse(defaultValue: T): T {
        return value!!
    }

}
