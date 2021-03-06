package net.obvj.junit.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that matches if a {@link Number} is negative.
 * <p>
 * This is particularly useful in situations where the exact value of an operation is
 * unpredictable.
 * <p>
 * For example:
 *
 * <pre>
 * assertThat(duration.compareTo(otherDuration), isNegative());
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.3.0
 */
public class IsNegativeMatcher extends TypeSafeMatcher<Number>
{
    private static final String EXPECTED_SCENARIO = "a negative number";

    /**
     * Creates a matcher that matches if the examined object is a negative number.
     *
     * @return the matcher
     */
    @Factory
    public static IsNegativeMatcher isNegative()
    {
        return new IsNegativeMatcher();
    }

    /**
     * Execute the matcher business logic for the specified number.
     *
     * @param number the number to be checked
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(Number number)
    {
        return number.doubleValue() < 0;
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @param description the {@link Description} to be appended to
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(EXPECTED_SCENARIO);
    }

}
