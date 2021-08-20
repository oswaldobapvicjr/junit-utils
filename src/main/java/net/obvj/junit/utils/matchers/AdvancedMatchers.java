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

import org.hamcrest.Matcher;

/**
 * A class that groups all Matchers provided by junit-utils, so that they can be accessed
 * with a single static import declaration.
 * <p>
 * For example:
 *
 * <blockquote>
 *
 * <pre>
 * {@code import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;}
 *
 * {@code ...}
 *
 * {@code assertThat(() -> obj.doStuff(),}
 * {@code         throwsException(IllegalStateException.class)}
 * {@code             .withMessage(containsAll("invalid state").ignoreCase()));}
 * </pre>
 *
 * </blockquote>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.1
 */
public class AdvancedMatchers
{
    private AdvancedMatchers()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    /**
     * Creates a matcher that matches if the examined procedure throws any exception.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"), throwsException());}
     * </pre>
     *
     * </blockquote>
     *
     * <strong>Note:</strong> This has the same effect as calling:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"),}
     * {@code         throwsException(Exception.class));}
     * </pre>
     *
     * </blockquote>
     *
     * @return the matcher
     * @since 1.3.1
     */
    public static ExceptionMatcher throwsException()
    {
        return ExceptionMatcher.throwsException();
    }

    /**
     * Creates a matcher that matches if the examined procedure throws a given exception.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"),}
     * {@code         throwsException(IllegalStateException.class));}
     * </pre>
     *
     * </blockquote>
     *
     * The matcher matches if the actual exception class is either the same as, or is a child
     * of, the Exception represented by the specified parameter.
     * <p>
     * For example, if the examined code throws a {@code NullPointerException}, all of the
     * following assertions are valid:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat( ... throwsException(NullPointerException.class));}
     * {@code assertThat( ... throwsException(RuntimeException.class));}
     * {@code assertThat( ... throwsException(Exception.class));}}
     * </pre>
     *
     * </blockquote>
     *
     * In other words, the matcher tests whether the actual exception can be converted to the
     * specified class.
     * <p>
     * If applicable, the matcher can also be incremented to validate the exception message
     * and cause.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff(""),}
     * {@code         throwsException(IllegalArgumentException.class)}
     * {@code             .withMessage("The argument must not be empty"));}
     * </pre>
     *
     * </blockquote>
     *
     * @param exception the expected exception class. A null value is allowed, and means that
     *                  no exception is expected
     * @return the matcher
     */
    public static ExceptionMatcher throwsException(Class<? extends Exception> exception)
    {
        return ExceptionMatcher.throwsException(exception);
    }

    /**
     * Creates a matcher that matches if the examined procedure throws no exception.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"), throwsNoException());}
     * </pre>
     *
     * </blockquote>
     *
     * <strong>Note:</strong> This has the same effect as calling:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"), throwsException(null));}
     * </pre>
     *
     * </blockquote>
     *
     * @return the matcher
     * @since 1.3.1
     */
    public static Matcher<Runnable> throwsNoException()
    {
        return ExceptionMatcher.throwsNoException();
    }

    /**
     * Creates a matcher that matches if the examined class cannot be instantiated, which is
     * particularly useful for utility classes.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * assertThat(TestUtils.class, instantiationNotAllowed());
     * </pre>
     *
     * </blockquote>
     *
     * <p>
     * First, the matcher verifies all declared constructors, and then it tries to create a
     * new instance using the default constructor.
     * <p>
     * A matching class shall have all constructors declared as {@code private} and throw an
     * exception inside the default constructor, so it can never be instantiated.
     * <p>
     * For example:
     *
     * <blockquote>
     *
     * <pre>
     * private MyClass()
     * {
     *     throw new IllegalStateException("Instantiation not allowed");
     * }
     * </pre>
     *
     * </blockquote>
     *
     * If applicable, the matcher can also be incremented to validate the exception thrown by
     * the constructor:
     *
     * <blockquote>
     *
     * <pre>
     * {@code assertThat(TestUtils.class, instantiationNotAllowed()}
     * {@code         .throwing(IllegalStateException.class)}
     * {@code             .withMessage("Instantiation not allowed"));}
     * </pre>
     *
     * </blockquote>
     *
     * @return the matcher
     */
    public static InstantiationNotAllowedMatcher instantiationNotAllowed()
    {
        return InstantiationNotAllowedMatcher.instantiationNotAllowed();
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>all</b> of the
     * specified substrings (regardless of the order they appear in the string).
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat("the quick brown fox", containsAll("fox", "the"))
     * </pre>
     * </blockquote>
     *
     * By default, the matcher is <b>case-sensitive</b>, but this behavior can be modified
     * with an additional method call.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat("the quick brown fox", containsAll("FOX", "The").ignoreCase())
     * </pre>
     * </blockquote>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsAll(String... substrings)
    {
        return StringMatcher.containsAll(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>any</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat("the quick brown fox", containsAny("fox", "dragon"))
     * </pre>
     * </blockquote>
     *
     * By default, the matcher is <b>case-sensitive</b>, but this behavior can be modified
     * with an additional method call.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat("the quick brown fox", containsAny("FOX", "dragon").ignoreCase())
     * </pre>
     * </blockquote>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsAny(String... substrings)
    {
        return StringMatcher.containsAny(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>none</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat("the quick brown fox", containsNone("cat", "mouse"))
     * </pre>
     * </blockquote>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsNone(String... substrings)
    {
        return StringMatcher.containsNone(substrings);
    }

    /**
     * A matcher that matches if a {@link Number} is positive.
     * <p>
     * This is particularly useful in situations where the exact value of an operation is
     * unpredictable.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat(stopwatch.elapsedTime(TimeUnit.MILLISECONDS), isPositive());
     * </pre>
     * </blockquote>
     *
     * @return the matcher
     * @since 1.3.0
     */
    public static IsPositiveMatcher isPositive()
    {
        return IsPositiveMatcher.isPositive();
    }

    /**
     * A matcher that matches if a {@link Number} is negative.
     * <p>
     * This is particularly useful in situations where the exact value of an operation is
     * unpredictable.
     * <p>
     * For example:
     *
     * <blockquote>
     * <pre>
     * assertThat(duration.compareTo(otherDuration), isNegative());
     * </pre>
     * </blockquote>
     *
     * @return the matcher
     * @since 1.3.0
     */
    public static IsNegativeMatcher isNegative()
    {
        return IsNegativeMatcher.isNegative();
    }

}
