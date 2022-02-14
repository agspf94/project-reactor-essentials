package com.example.projectreactoressentials

import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import org.springframework.boot.test.context.SpringBootTest
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
}
