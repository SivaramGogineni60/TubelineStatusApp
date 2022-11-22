package com.tfl.tubelinestatus.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Test rule to avoid injecting a TestCoroutineDispatcher in classes being tested.
 * This avoid blocking scenarios from never failing the unit test.
 *
 * Just apply the below code in your tests:
 *      @get:Rule
 *      val rule = TestCoroutineRule()
 *
 *      ...
 *
 *      fun test() = rule.runBlockingTest {
 *          ...
 *      }
 */
@ExperimentalCoroutinesApi
class TestCoroutineRule : TestWatcher() {
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
        testDispatcher.runBlockingTest(block)
    }
}