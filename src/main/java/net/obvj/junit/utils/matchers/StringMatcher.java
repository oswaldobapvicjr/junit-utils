/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.junit.utils.matchers;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * A Matcher that evaluates the content of a string of characters against one or more
 * substrings or regular expressions.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class StringMatcher extends TypeSafeDiagnosingMatcher<String>
{
    private static final String EXPECTED_SCENARIO = "a string containing %s of the specified substrings %s";
    private static final String EXPECTED_PATTERN_SCENARIO = "a string matching %s of the specified patterns %s";
    private static final String EXPECTED_STRING_NOT_FOUND = "the substring \"%s\" was not found in: \"%s\"";
    private static final String EXPECTED_STRING_NOT_FOUND_AFTER = "the substring \"%s\" was not found after \"%s\" in: \"%s\"";
    private static final String NONE_OF_THE_STRINGS_FOUND = "none of the specified substrings was found in: \"%s\"";
    private static final String UNEXPECTED_STRING_FOUND = "the unexpected string \"%s\" was found in: \"%s\"";
    private static final String NONE_OF_THE_PATTERNS_MATCHES = "none of the specified patterns matches: \"%s\"";
    private static final String PATTERN_NOT_MATCHED = "the pattern \"%s\" does not match: \"%s\"";
    private static final String UNEXPECTED_PATTERN_MATCHED = "the pattern \"%s\" matches: \"%s\"";


    /**
     * Defines the matching strategy to be applied.
     */
    protected enum Strategy
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
         * Matches if all of the specified substrings are found in the tested string in the
         * sequence they are declared.
         *
         * @since 1.9.0
         */
        ALL_IN_SEQUENCE
        {
            @Override
            public boolean evaluate(String string, List<String> substrings, CaseStrategy caseStrategy,
                    Description mismatch)
            {
                int lastIndex = -1;
                for (int i = 0; i < substrings.size(); i++)
                {
                    String substring = substrings.get(i);
                    int currentIndex = caseStrategy.indexOf(string,  substring, lastIndex + 1);

                    if (currentIndex == -1)
                    {
                        if (i == 0)
                        {
                            mismatch.appendText(String.format(EXPECTED_STRING_NOT_FOUND, substring, string));
                        }
                        else
                        {
                            mismatch.appendText(String.format(EXPECTED_STRING_NOT_FOUND_AFTER, substring, substrings.get(i - 1), string));
                        }
                        return false;
                    }

                    lastIndex = currentIndex + substring.length() - 1;
                }
                return true;
            }

            @Override
            public String toString()
            {
                return "ALL (in sequence)";
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
        },

        /**
         * Matches if the tested string matches any of the specified regular expressions.
         *
         * @since 1.11.0
         */
        ANY_PATTERN
        {
            @Override
            public boolean evaluate(String string, List<String> patterns, CaseStrategy caseStrategy,
                    Description mismatch)
            {
                // Compile all patterns first, so an invalid one fails even when another one matches
                List<Pattern> compiledPatterns = patterns.stream().map(caseStrategy::compile)
                        .collect(Collectors.toList());

                if (compiledPatterns.stream().anyMatch(pattern -> pattern.matcher(string).matches()))
                {
                    return true;
                }
                mismatch.appendText(String.format(NONE_OF_THE_PATTERNS_MATCHES, string));
                return false;
            }

            @Override
            public String expectedScenario()
            {
                return EXPECTED_PATTERN_SCENARIO;
            }

            @Override
            public String toString()
            {
                return "ANY";
            }
        },

        /**
         * Matches if the tested string matches all of the specified regular expressions.
         *
         * @since 1.11.0
         */
        ALL_PATTERNS
                {
                    @Override
                    public boolean evaluate(String string, List<String> patterns, CaseStrategy caseStrategy,
                                            Description mismatch)
                    {
                        // Compile all patterns first, so an invalid one fails even when another one doesn't match
                        List<Pattern> compiledPatterns = patterns.stream().map(caseStrategy::compile)
                                .collect(Collectors.toList());

                        for (int i = 0; i < compiledPatterns.size(); i++)
                        {
                            if (!compiledPatterns.get(i).matcher(string).matches())
                            {
                                mismatch.appendText(String.format(PATTERN_NOT_MATCHED, patterns.get(i), string));
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    public String expectedScenario()
                    {
                        return EXPECTED_PATTERN_SCENARIO;
                    }

                    @Override
                    public String toString()
                    {
                        return "ALL";
                    }
                },

        /**
         * Matches if the tested string matches none of the specified regular expressions.
         *
         * @since 1.11.0
         */
        NONE_PATTERNS
                {
                    @Override
                    public boolean evaluate(String string, List<String> patterns, CaseStrategy caseStrategy,
                                            Description mismatch)
                    {
                        // Compile all patterns first, so an invalid one fails even when another one matches
                        List<Pattern> compiledPatterns = patterns.stream().map(caseStrategy::compile)
                                .collect(Collectors.toList());

                        for (int i = 0; i < compiledPatterns.size(); i++)
                        {
                            if (compiledPatterns.get(i).matcher(string).matches())
                            {
                                mismatch.appendText(String.format(UNEXPECTED_PATTERN_MATCHED, patterns.get(i), string));
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    public String expectedScenario()
                    {
                        return EXPECTED_PATTERN_SCENARIO;
                    }

                    @Override
                    public String toString()
                    {
                        return "NONE";
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

        /**
         * Returns the format string for the "expected" part of the test description.
         *
         * @return the format string, taking the strategy and the substrings as arguments
         * @since 1.11.0
         */
        public String expectedScenario()
        {
            return EXPECTED_SCENARIO;
        }
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

            @Override
            public int indexOf(String string, String substring, int fromIndex)
            {
                return string != null ? string.indexOf(substring, fromIndex): -1;
            }

            @Override
            public Pattern compile(String regex)
            {
                return Pattern.compile(regex);
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

            @Override
            public int indexOf(String string, String substring, int fromIndex)
            {
                return string != null && substring != null ? string.toLowerCase().indexOf(substring.toLowerCase(), fromIndex): -1;
            }

            @Override
            public Pattern compile(String regex)
            {
                return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
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

        /**
         * Returns the index of the first occurrence of the specified substring within the
         * specified string, starting at the specified index.
         *
         * @param string    the string to check
         * @param substring the substring to search for
         * @param fromIndex the index from which to start the search
         * @return the index the index of the first occurrence of the specified substring within
         *         the specified string, starting at the specified index
         * @since 1.9.0
         */
        public abstract int indexOf(String string, String substring, int fromIndex);

        /**
         * Compiles the specified regular expression into a pattern.
         *
         * @param regex the expression to be compiled
         * @return the compiled pattern
         * @throws java.util.regex.PatternSyntaxException if the expression's syntax is invalid
         * @since 1.11.0
         */
        public abstract Pattern compile(String regex);
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
    public static StringMatcher containsAll(String... substrings)
    {
        return new StringMatcher(Strategy.ALL, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>all</b> of the
     * specified substrings in order.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAllInSequence("quick", "brown")) //PASS
     * assertThat("the quick brown fox", containsAllInSequence("brown", "quick")) //FAIL
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     * @since 1.9.0
     */
    public static StringMatcher containsAllInSequence(String... substrings)
    {
        return new StringMatcher(Strategy.ALL_IN_SEQUENCE, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>any</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("fox", "dragon"))
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
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
    public static StringMatcher containsNone(String... substrings)
    {
        return new StringMatcher(Strategy.NONE, CaseStrategy.DEFAULT, substrings);
    }

    /**
     * Creates a matcher that matches if the examined string matches <b>any</b> of the
     * specified regular expressions.
     * <p>
     * Each pattern must match the entire string, as in {@link String#matches(String)}.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", matchesAny(".*fox", "dragon")) //PASS
     * assertThat("the quick brown fox", matchesAny("fox", "dragon"))   //FAIL
     * </pre>
     *
     * An invalid pattern causes a {@link java.util.regex.PatternSyntaxException} when the
     * matcher is evaluated.
     *
     * @param patterns the regular expressions to be tested
     * @return the matcher
     * @since 1.11.0
     */
    public static StringMatcher matchesAny(String... patterns)
    {
        return new StringMatcher(Strategy.ANY_PATTERN, CaseStrategy.DEFAULT, patterns);
    }

    /**
     * Creates a matcher that matches if the examined string matches <b>all</b> of the
     * specified regular expressions.
     * <p>
     * Each pattern must match the entire string, as in {@link String#matches(String)}.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", matchesAll(".*fox", "the.*")) //PASS
     * assertThat("the quick brown fox", matchesAll(".*fox", "dragon")) //FAIL
     * </pre>
     *
     * An invalid pattern causes a {@link java.util.regex.PatternSyntaxException} when the
     * matcher is evaluated.
     *
     * @param patterns the regular expressions to be tested
     * @return the matcher
     * @since 1.11.0
     */
    public static StringMatcher matchesAll(String... patterns)
    {
        return new StringMatcher(Strategy.ALL_PATTERNS, CaseStrategy.DEFAULT, patterns);
    }

    /**
     * Creates a matcher that matches if the examined string matches <b>none</b> of the
     * specified regular expressions.
     * <p>
     * Each pattern must match the entire string, as in {@link String#matches(String)}.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", matchesNone(".*dragon", ".*manticore")) //PASS
     * assertThat("the quick brown fox", matchesNone(".*fox", ".*dragon"))       //FAIL
     * </pre>
     *
     * An invalid pattern causes a {@link java.util.regex.PatternSyntaxException} when the
     * matcher is evaluated.
     *
     * @param patterns the regular expressions to be tested
     * @return the matcher
     * @since 1.11.0
     */
    public static StringMatcher matchesNone(String... patterns)
    {
        return new StringMatcher(Strategy.NONE_PATTERNS, CaseStrategy.DEFAULT, patterns);
    }

    /**
     * Tells the matcher to compare strings irrespective of case.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("FOX").ignoreCase())
     * </pre>
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
     * @param string   the string to be checked
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(String string, Description mismatch)
    {
        return strategy.evaluate(string, substrings, caseStrategy, mismatch);
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @param description the {@link Description} to be appended to
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(String.format(strategy.expectedScenario(), strategy, substrings));
        if (caseStrategy != CaseStrategy.DEFAULT)
        {
            description.appendText(" (").appendText(caseStrategy.description).appendText(")");
        }
    }

    /**
     * @return the strategy
     */
    protected Strategy getStrategy()
    {
        return strategy;
    }

    /**
     * @return the substrings
     */
    protected List<String> getSubstrings()
    {
        return substrings;
    }

}
