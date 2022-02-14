package com.example.projectreactoressentials

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

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

	@Test
	fun test() {
		println("Everything working as intended")
	}
}
