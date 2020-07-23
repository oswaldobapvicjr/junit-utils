package net.obvj.junit.utils.matchers;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * A Matcher that evaluates the content of a string against one or more substrings.
 *
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class StringMatcher extends TypeSafeDiagnosingMatcher<String>
{
    private static final String EXPECTED_SCENARIO = "a string containing %s of the specified substrings %s";
    private static final String EXPECTED_STRING_NOT_FOUND = "the substring \"%s\" was not found in: \"%s\"";
    private static final String NONE_OF_THE_STRINGS_FOUND = "none of the specified substrings was found in: \"%s\"";
    private static final String UNEXPECTED_STRING_FOUND = "the unexpected string \"%s\" was found in: \"%s\"";

    /**
     * Defines the matching strategy to be applied.
     */
    private enum Strategy
    {
        /**
         * Matches if all of the specified substrings are found in the tested string.
         */
        ALL
        {
            @Override
            public boolean evaluate(String string, List<String> substrings, Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (!string.contains(substring))
                    {
                        mismatch.appendText(String.format(EXPECTED_STRING_NOT_FOUND, substring, string));
                        return false;
                    }
                }
                return true;
            }
        },

        /**
         * Matches if any of the specified substrings is found in the tested string.
         */
        ANY
        {
            @Override
            public boolean evaluate(String string, List<String> substrings, Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (string.contains(substring))
                    {
                        return true;
                    }
                }
                mismatch.appendText(String.format(NONE_OF_THE_STRINGS_FOUND, string));
                return false;
            }
        },

        /**
         * Matches if none of the specified substrings is found in the tested string.
         */
        NONE
        {
            @Override
            public boolean evaluate(String string, List<String> substrings, Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (string.contains(substring))
                    {
                        mismatch.appendText(String.format(UNEXPECTED_STRING_FOUND, substring, string));
                        return false;
                    }
                }
                return true;
            }
        };

        /**
         * Execute the matcher business logic.
         *
         * @param string     the string to be evaluated
         * @param substrings the substrings to be checked
         * @param mismatch   the description to be used for reporting in case of mismatch
         */
        public abstract boolean evaluate(String string, List<String> substrings, Description mismatch);

    }

    private final Strategy strategy;
    private final List<String> substrings;

    /**
     * Builds this Matcher with a specialized strategy and a list of substrings to be tested.
     *
     * @param strategy   the evaluation strategy
     * @param substrings the substrings to be evaluated
     */
    private StringMatcher(Strategy strategy, String... substrings)
    {
        this.strategy = strategy;
        this.substrings = Arrays.asList(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>all</b> of the
     * specified substrings (regardless of the order they appear in the string).
     * <p/>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAll("fox", "the"))
     * </pre>
     */
    @Factory
    public static Matcher<String> containsAll(String... substrings)
    {
        return new StringMatcher(Strategy.ALL, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>any</b> of the
     * specified substrings.
     * <p/>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("fox", "bird"))
     * </pre>
     */
    @Factory
    public static Matcher<String> containsAny(String... substrings)
    {
        return new StringMatcher(Strategy.ANY, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>none</b> of the
     * specified substrings.
     * <p/>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsNone("cat", "mouse"))
     * </pre>
     */
    @Factory
    public static Matcher<String> containsNone(String... substrings)
    {
        return new StringMatcher(Strategy.NONE, substrings);
    }

    /**
     * Execute the matcher business logic for the specified string.
     *
     * @param string   the string to be checked
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(String string, Description mismatch)
    {
        return strategy.evaluate(string, substrings, mismatch);
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @see org.hamcrest.SelfDescribing#describeTo(Description)
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(String.format(EXPECTED_SCENARIO, strategy, substrings));
    }

}
