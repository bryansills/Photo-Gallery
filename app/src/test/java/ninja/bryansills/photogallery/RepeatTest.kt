package ninja.bryansills.photogallery

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Repeat tests when they are flaky.
 *
 * @property attemptCount The number of times to retry the test. Defaults to 1.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RepeatTest(val attemptCount: Int = 1)

/**
 * A JUnit rule that retries tests annotated with [RepeatTest] a specified number of times.
 */
class RepeatTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                val annotation = description.getAnnotation(RepeatTest::class.java)
                val maxAttempts = annotation?.attemptCount ?: 1
                var failureCount = 0

                for (attempt in 1..maxAttempts) {
                    try {
                        base.evaluate()
                    } catch (throwable: Throwable) {
                        failureCount++
                        println("Test attempt #$attempt out of $maxAttempts failed. Reason: ${throwable.message}")
                        if (failureCount == maxAttempts) {
                            // they all failed! :(
                            println("Failed too many times!")
                            throw throwable
                        }
                    }
                }

                if (maxAttempts > 1) {
                    println("Did $maxAttempts attempts and $failureCount failed.")
                }
            }
        }
}
