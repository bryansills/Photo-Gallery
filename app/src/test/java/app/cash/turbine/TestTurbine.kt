package app.cash.turbine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest

internal interface TurbineTestScope : TurbineContext {
    val backgroundScope: CoroutineScope

    val testScheduler: TestCoroutineScheduler
}

internal fun runTestTurbine(validate: suspend TurbineTestScope.() -> Unit) = runTest {
    turbineScope {
        val turbineTestScope =
            object : TurbineTestScope, TurbineContext by this {
                override val backgroundScope: CoroutineScope = this@runTest.backgroundScope

                override val testScheduler: TestCoroutineScheduler = this@runTest.testScheduler
            }

        turbineTestScope.validate()
    }
}