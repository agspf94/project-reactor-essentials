package com.example.projectreactoressentials

import java.time.Duration
import org.junit.jupiter.api.Test
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
class ProjectReactorEssentialsApplicationTests {
	/*
	Reactive Streams
	1. Asynchronous
	2. Non-blocking
	3. Backpressure
	Publisher <- (subscribe) Subscriber
	Subscription is created
	Publisher (onSubscribe with the subscription) -> Subscriber
	Subscription <- (request  N) Subscriber
	Publisher -> (onNext) Subscriber
	until:
		1. Publisher sent all the objects requested
		2. Publisher sent all the objects it has. (onComplete) subscriber and subscription will be cancelled
		3. There is an error. (onError) subscriber and subscription will be cancelled
	 */
	val separator = "---------------------------------------------"

	@Test
	fun monoSubscriber() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.log()
		mono.subscribe()
		println(separator)

		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete()
	}

	@Test
	fun monoSubscriberConsumer() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.log()
		mono.subscribe { println("Value: $it") }
		println(separator)

		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete()
	}

	@Test
	fun monoSubscriberConsumerError() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.map { throw RuntimeException("Testing mono with error") }
		mono.subscribe(
			{ println("Value: $it") },
			{ println("Something bad happened") }
		)
		println(separator)

		StepVerifier.create(mono)
			.expectError(RuntimeException::class.java)
			.verify()
	}

	@Test
	fun monoSubscriberConsumerComplete() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.map(String::uppercase)
			.log()
		mono.subscribe(
			{ println("Value: $it") },
			(Throwable::printStackTrace),
			{ println("Finished") }
		)
		println(separator)

		StepVerifier.create(mono)
			.expectNext(name.uppercase())
			.verifyComplete()
	}

	@Test
	fun monoSubscriberConsumerSubscription() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.map(String::uppercase)
			.log()
		mono.subscribe(
			{ println("Value: $it") },
			(Throwable::printStackTrace),
			{ println("Finished") },
			(Subscription::cancel)
		)
		println(separator)

		StepVerifier.create(mono)
			.expectNext(name.uppercase())
			.verifyComplete()
	}

	@Test
	fun monoDoOnMethods() {
		val name = "Anderson Fantin"
		val mono = Mono.just(name)
			.doOnSubscribe { println("doOnSubscribe: $it") }
			.doOnRequest { println("doOnRequest: $it") }
			.doOnNext { println("doOnNext: $it") }
			.doOnSuccess { println("doOnSuccess: $it") }
			.log()
		mono.subscribe(
			{ println("Value: $it") },
			(Throwable::printStackTrace),
			{ println("Finished") }
		)
		println(separator)

		StepVerifier.create(mono)
			.expectNext(name)
			.verifyComplete()
	}

	@Test
	fun monoDoOnError() {
		val mono: Mono<Any> = Mono.error<Any?>(RuntimeException("Runtime error"))
			.doOnError { println("Error: $it") }
			.log()
		println(separator)

		StepVerifier.create(mono)
			.expectError(RuntimeException::class.java)
			.verify()
	}

	@Test
	fun monoOnErrorResume() {
		val mono: Mono<Any> = Mono.error<Any?>(RuntimeException("Runtime error"))
			.onErrorResume {
				println("Continuing")
				Mono.just("Not an error")
			}
			.doOnError { println("Error: $it") }
			.log()
		println(separator)

		StepVerifier.create(mono)
			.expectNext("Not an error")
			.verifyComplete()
	}

	@Test
	fun monoOnErrorReturn() {
		val mono: Mono<Any> = Mono.error<Any?>(RuntimeException("Runtime error"))
			.onErrorResume {
				println("Continuing")
				Mono.error(RuntimeException("Runtime error"))
			}
			.onErrorReturn("onErrorReturn")
			.doOnError { println("Error: $it") }
			.log()
		println(separator)

		StepVerifier.create(mono)
			.expectNext("onErrorReturn")
			.verifyComplete()
	}

	@Test
	fun monoTests() {
		Mono.just("Anderson Fantin")
			.map { it.uppercase() }
			.doOnSubscribe { println("doOnSubscribe: $it") }
			.doOnRequest { println("doOnRequest: $it") }
			.doOnNext { println("doOnNext: $it") }
			.doOnSuccess { println("doOnSuccess: $it") }
			.doOnError { println("doOnError: $it") }
			.onErrorResume {
				println("onErrorResume: $it")
				Mono.just("Giuseppe Saraiva Patriarca")
			}
			.onErrorReturn("Giuseppe Saraiva Patriarca")
			.log()
			.subscribe(
				{ println("Value: $it") },
				(Throwable::printStackTrace),
				{ println("Finished") },
				{ it.request(5) }
			)
	}

	@Test
	fun fluxSubscriber() {
		val flux = Flux.just("Anderson", "Giuseppe", "Saraiva", "Patriarca", "Fantin")
			.log()
		flux.subscribe()
		println(separator)

		StepVerifier.create(flux)
			.expectNext("Anderson", "Giuseppe", "Saraiva", "Patriarca", "Fantin")
			.verifyComplete()
	}

	@Test
	fun fluxSubscriberNumbers() {
		val flux = Flux.range(1, 5)
			.log()
		flux.subscribe { println("Number: $it") }
		println(separator)

		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4, 5)
			.verifyComplete()
	}

	@Test
	fun fluxSubscriberFromList() {
		val flux = Flux.fromIterable(listOf(1, 2, 3, 4))
			.log()
		flux.subscribe { println("Number: $it") }
		println(separator)

		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4)
			.verifyComplete()
	}

	@Test
	fun fluxSubscriberNumbersError() {
		val flux = Flux.range(1, 5)
			.log()
			.map {
				if (it == 4) throw IndexOutOfBoundsException("Index error")
				it
			}
		flux.subscribe(
			{ println("Number: $it") },
			Throwable::printStackTrace,
			{ println("Done") },
			{ it.request(3) }
		)
		println(separator)

		StepVerifier.create(flux)
			.expectNext(1, 2, 3)
			.expectError(IndexOutOfBoundsException::class.java)
			.verify()
	}

	@Test
	fun fluxSubscriberNumbersUglyBackpressure() {
		val flux = Flux.range(1, 10)
			.log()
		flux.subscribe(object : Subscriber<Int?> {
			private var count = 0
			private var subscription: Subscription? = null
			private val requestCount = 2L

			override fun onSubscribe(s: Subscription?) {
				this.subscription = s
				subscription!!.request(requestCount)
			}

			override fun onNext(t: Int?) {
				count++
				if (count >= 2) {
					count = 0
					subscription?.request(requestCount)
				}
			}

			override fun onError(t: Throwable?) {
				TODO("Not yet implemented")
			}

			override fun onComplete() {
				subscription?.cancel()
			}
		})
		println(separator)

		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			.verifyComplete()
	}

	@Test
	fun fluxSubscriberNumbersNotSoUglyBackpressure() {
		val flux = Flux.range(1, 10)
			.log()
		flux.subscribe(object : BaseSubscriber<Int>() {
			private var count = 0
			private val requestCount = 2L

			override fun hookOnSubscribe(subscription: Subscription) {
				request(2)
			}

			override fun hookOnNext(value: Int) {
				count++
				if (count >= 2) {
					count = 0
					request(requestCount)
				}
			}
		})
		println(separator)

		StepVerifier.create(flux)
			.expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			.verifyComplete()
	}

	@Test
	fun fluxSubscriberInterval1() {
		val interval = Flux.interval(Duration.ofMillis(100))
			.take(10)
			.log()
		interval.subscribe { println("Number: $it") }

		Thread.sleep(3000)
	}

	@Test
	fun fluxSubscriberInterval2() {
		StepVerifier.withVirtualTime(this::createInterval)
			.expectSubscription()
			.expectNoEvent(Duration.ofDays(1))
			.thenAwait(Duration.ofDays(1))
			.expectNext(0L)
			.thenAwait(Duration.ofDays(1))
			.expectNext(1L)
			.thenCancel()
			.verify()
	}

	private fun createInterval(): Flux<Long> {
		return Flux.interval(Duration.ofDays(1))
			.log()
	}
}
