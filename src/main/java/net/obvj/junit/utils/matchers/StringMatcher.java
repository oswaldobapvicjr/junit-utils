package net.obvj.junit.utils.matchers;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * A Matcher that evaluates the content of a string of characters against one or more
 * substrings.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class StringMatcher extends TypeSafeDiagnosingMatcher<CharSequence>
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
            public boolean evaluate(String string, List<String> substrings, CaseStrategy caseStrategy,
                    Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (!caseStrategy.contains(string, substring))
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
            public boolean evaluate(String string, List<String> substrings, CaseStrategy caseStrategy,
                    Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (caseStrategy.contains(string, substring))
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
            public boolean evaluate(String string, List<String> substrings, CaseStrategy caseStrategy,
                    Description mismatch)
            {
                for (String substring : substrings)
                {
                    if (caseStrategy.contains(string, substring))
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
         * @param string       the string to be evaluated
         * @param substrings   the substrings to be checked
         * @param caseStrategy the case strategy to be used
         * @param mismatch     the description to be used for reporting in case of mismatch
         */
        public abstract boolean evaluate(String string, List<String> substrings, CaseStrategy caseStrategy,
                Description mismatch);
    }

    /**
     * Defines the case strategy to be used
     */
    private enum CaseStrategy
    {
        /**
         * Matches if a string contains a substring, case-sensitive.
         */
        DEFAULT("default")
        {
            @Override
            public boolean contains(String string, String substring)
            {
                return string != null && substring != null && string.contains(substring);
            }
        },

        /**
         * Matches if a string contains a substring, irrespective of case
         */
        IGNORE_CASE("ignore case")
        {
            @Override
            public boolean contains(String string, String substring)
            {
                return string != null && substring != null && string.toLowerCase().contains(substring.toLowerCase());
            }
        };

        String description;

        private CaseStrategy(String description)
        {
            this.description = description;
        }

        /**
         * Checks if a string contains a substring.
         *
         * @param string    the string to check
         * @param substring the substring to find
         * @return {@code true} if the string contains the substring, otherwise {@code false}
         */
        public abstract boolean contains(String string, String substring);
    }

    private final Strategy strategy;
    private final List<String> substrings;

    private CaseStrategy caseStrategy;

    /**
     * Builds this Matcher with a specialized strategy and a list of substrings to be tested.
     *
     * @param strategy     the evaluation strategy
     * @param caseStrategy the case strategy to set
     * @param substrings   the substrings to be evaluated
     */
    private StringMatcher(Strategy strategy, CaseStrategy caseStrategy, String... substrings)
    {
        this.strategy = strategy;
        this.caseStrategy = caseStrategy;
        this.substrings = Arrays.asList(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>all</b> of the
     * specified substrings (regardless of the order they appear in the string).
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAll("fox", "the"))
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    @Factory
    public static StringMatcher containsAll(String... substrings)
    {
        return new StringMatcher(Strategy.ALL, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>any</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("fox", "bird"))
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    @Factory
    public static StringMatcher containsAny(String... substrings)
    {
        return new StringMatcher(Strategy.ANY, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>none</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsNone("cat", "mouse"))
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    @Factory
    public static StringMatcher containsNone(String... substrings)
    {
        return new StringMatcher(Strategy.NONE, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Tells the matcher to compare strings irrespective of case.
     *
     * @return the matcher
     */
    public StringMatcher ignoreCase()
    {
        caseStrategy = CaseStrategy.IGNORE_CASE;
        return this;
    }

    /**
     * Execute the matcher business logic for the specified string.
     *
     * @param charSequence the sequence of char values to be checked
     * @param mismatch     the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(CharSequence charSequence, Description mismatch)
    {
        String mainString = charSequence.toString();
        return strategy.evaluate(mainString, substrings, caseStrategy, mismatch);
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
        if (caseStrategy != CaseStrategy.DEFAULT)
        {
            description.appendText(" (").appendText(caseStrategy.description).appendText(")");
        }
    }

}
