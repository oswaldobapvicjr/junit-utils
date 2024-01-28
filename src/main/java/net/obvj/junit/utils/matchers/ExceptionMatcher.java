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
import java.util.Collections;
import java.util.List;

import org.hamcrest.*;

import net.obvj.junit.utils.Procedure;

/**
 * A Matcher that matches if a procedure throws an expected exception.
 * <p>
 * For example:
 *
 * <pre>
 * {@code
 * assertThat(() -> obj.doStuff("p1"),
 *         throwsException(NullPointerException.class));}
 * </pre>
 *
 * <p>
 * Exception details, such as message and cause, can also be evaluated using additional
 * methods. For example:
 * </p>
 *
 * <pre>
 * {@code
 * assertThat(() -> obj.doStuff("p1"),
 *         throwsException(IllegalStateException.class)
 *             .withCause(FileNotFoundException.class));
 *
 * assertThat(() -> obj.doStuff(null),
 *         throwsException(MyException.class)
 *             .withMessageContaining("ERR-12008"));}
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ExceptionMatcher extends TypeSafeDiagnosingMatcher<Procedure>
{
    /**
     * Defines different strategies for validating the message of an expected exception.
     *
     * @since 1.2.0
     */
    private enum MessageMatchingStrategy
    {
        /**
         * Validates if a message contains one or more expected substrings.
         *
         * @since 1.2.0
         */
        EXPECTED_SUBSTRINGS
        {
            @Override
            boolean validateMessage(ExceptionMatcher parent, Throwable throwable, Description mismatch)
            {
                String message = throwable.getMessage();
                if (message == null)
                {
                    if (parent.expectedMessageSubstrings.isEmpty())
                    {
                        return true;
                    }
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the message was null");
                    return false;
                }
                for (String substring : parent.expectedMessageSubstrings)
                {
                    if (!message.contains(substring))
                    {
                        mismatch.appendText(NEW_LINE_INDENT).appendText("the message was ").appendValue(message);
                        return false;
                    }
                }
                return true;
            }

            @Override
            void describeTo(ExceptionMatcher parent, Description description)
            {
                description.appendText(NEW_LINE_INDENT).appendText("with message containing: ")
                        .appendText(parent.expectedMessageSubstrings.toString());
            }
        },

        /**
         * Uses an external matcher to validate the message of an exception.
         *
         * @since 1.2.0
         */
        EXTERNAL_MATCHER
        {
            @Override
            boolean validateMessage(ExceptionMatcher parent, Throwable throwable, Description mismatch)
            {
                String message = throwable.getMessage();
                if (message == null && parent.messageMatcher == null)
                {
                    return true;
                }
                if (parent.messageMatcher == null)
                {
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the message was ").appendValue(message);
                    return false;
                }
                boolean matcherResult = parent.messageMatcher.matches(message);
                if (!matcherResult)
                {
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the message ");
                    parent.messageMatcher.describeMismatch(message, mismatch);
                }
                return matcherResult;
            }

            @Override
            void describeTo(ExceptionMatcher parent, Description description)
            {
                description.appendText(NEW_LINE_INDENT).appendText("with message: ");
                if (parent.messageMatcher != null)
                {
                    parent.messageMatcher.describeTo(description);
                }
                else
                {
                    description.appendText("<null>");
                }
            }
        };

        /**
         * Validates the throwable's message.
         *
         * @param parent    the {@code ExceptionMatcher} instance to be handled
         * @param throwable the {@code Throwable} whose message is to be validated
         * @param mismatch  the description to be used for reporting in case of mismatch
         * @return a flag indicating whether or not the matching has succeeded
         */
        abstract boolean validateMessage(ExceptionMatcher parent, Throwable throwable, Description mismatch);

        /**
         * Describes the "expected" pat of the test description.
         */
        abstract void describeTo(ExceptionMatcher parent, Description description);
    }

    /**
     * Defines different strategies for validating the cause of an expected exception.
     *
     * @since 1.6.0
     */
    private enum CauseMatchingStrategy
    {
        /**
         * Validates that the cause matches (or is assignable from) a given class.
         *
         * @since 1.6.0
         */
        EXPECTED_CLASS
        {
            @Override
            boolean validateCause(ExceptionMatcher parent, Throwable throwable, Description mismatch)
            {
                Throwable cause = throwable.getCause();
                if (cause == null && parent.expectedCause != null)
                {
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the cause was null");
                    return false;
                }
                if (cause != null && (parent.expectedCause == null
                        || !parent.expectedCause.isAssignableFrom(cause.getClass())))
                {
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the cause was: ")
                            .appendText(nullSafeClassNameToText(cause.getClass()));
                    return false;
                }
                return true;
            }

            @Override
            void describeTo(ExceptionMatcher parent, Description description)
            {
                description.appendText(NEW_LINE_INDENT).appendText("and cause: ")
                        .appendText(nullSafeClassNameToText(parent.expectedCause));

            }
        },

        /**
         * Uses a nested {@code ExceptionMatcher} to validate the cause of an exception.
         *
         * @since 1.6.0
         */
        NESTED_MATCHER
        {
            @Override
            boolean validateCause(ExceptionMatcher parent, Throwable throwable, Description mismatch)
            {
                Procedure causeThrowingProcedure = new Procedure()
                {
                    @Override
                    public void execute() throws Throwable
                    {
                        throw throwable.getCause();
                    }
                };
                boolean matcherResult = parent.causeMatcher.matches(causeThrowingProcedure);
                if (!matcherResult)
                {
                    mismatch.appendText(NEW_LINE_INDENT).appendText("the cause did not match: (");
                    parent.causeMatcher.describeMismatch(causeThrowingProcedure, mismatch);
                    mismatch.appendText(NEW_LINE_INDENT).appendText("}");
                }
                return matcherResult;
            }

            @Override
            void describeTo(ExceptionMatcher parent, Description description)
            {
                description.appendText(NEW_LINE_INDENT).appendText("and cause: {");
                parent.causeMatcher.describeTo(description);
                description.appendText(NEW_LINE_INDENT).appendText("}");
            }
        };

        /**
         * Validates the throwable's cause.
         *
         * @param parent    the {@code ExceptionMatcher} instance to be handled
         * @param throwable the {@code Throwable} whose message is to be validated
         * @param mismatch  the description to be used for reporting in case of mismatch
         * @return a flag indicating whether or not the matching has succeeded
         */
        abstract boolean validateCause(ExceptionMatcher parent, Throwable throwable, Description mismatch);

        /**
         * Describes the "expected" pat of the test description.
         */
        abstract void describeTo(ExceptionMatcher parent, Description description);
    }

    private static final String INDENT = "          ";
    private static final String NEW_LINE_INDENT = "\n" + INDENT;

    private final Class<? extends Exception> expectedException;

    private CauseMatchingStrategy causeMatchingStrategy;
    private Class<? extends Throwable> expectedCause;
    private ExceptionMatcher causeMatcher;

    private boolean checkCauseFlag = false;
    private boolean checkMessageFlag = false;

    private MessageMatchingStrategy messageMatchingStrategy;
    private List<String> expectedMessageSubstrings = Collections.emptyList();
    private Matcher<String> messageMatcher;

    /**
     * Builds this Matcher.
     *
     * @param expectedException the expected Exception to be matched
     */
    private ExceptionMatcher(Class<? extends Exception> expectedException)
    {
        this.expectedException = expectedException;
    }

    /**
     * Creates a matcher that matches if the examined procedure throws any exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException());}
     * </pre>
     *
     * <strong>Note:</strong> This has the same effect as calling:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(Exception.class));}
     * </pre>
     *
     * @return the matcher
     * @since 1.3.1
     */
    @Factory
    public static ExceptionMatcher throwsException()
    {
        return new ExceptionMatcher(Exception.class);
    }

    /**
     * Creates a matcher that matches if the examined procedure throws a given exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalStateException.class));}
     * </pre>
     *
     * The matcher matches if the actual exception class is either the same as, or is a child
     * of, the Exception represented by the specified parameter.
     * <p>
     * For example, if the examined code throws a {@code NullPointerException}, all of the
     * following assertions are valid:
     *
     * <pre>
     * {@code
     * assertThat( ... throwsException(NullPointerException.class));
     * assertThat( ... throwsException(RuntimeException.class));
     * assertThat( ... throwsException(Exception.class));}
     * </pre>
     *
     * <p>
     * In other words, the matcher tests whether the actual exception can be converted to the
     * specified class.
     * </p>
     *
     * @param exception the expected exception class. A null value is allowed, and means that
     *                  no exception is expected
     * @return the matcher
     * @since 1.1.0
     */
    @Factory
    public static ExceptionMatcher throwsException(Class<? extends Exception> exception)
    {
        return new ExceptionMatcher(exception);
    }

    /**
     * This method behaves exactly the same as calling
     * {@link ExceptionMatcher#throwsException(Class)}.
     * <p>
     * It was created primarily to allow a nested {@code ExceptionMatcher} to validate the
     * cause of an exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalArgumentException.class)
     *             .withCause(
     *                 exception(FileNotFoundException.class)
     *                     .withMessage("File p1 not found"));}
     * </pre>
     *
     * @param exception the expected exception class. A null value is allowed, and means that
     *                  no exception is expected
     * @return a new matcher
     * @since 1.6.0
     * @see ExceptionMatcher#throwsException(Class)
     */
    @Factory
    public static ExceptionMatcher exception(Class<? extends Exception> exception)
    {
        return new ExceptionMatcher(exception);
    }

    /**
     * Creates a matcher that matches if the examined procedure throws no exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsNoException());}
     * </pre>
     *
     * <strong>Note:</strong> This has the same effect as calling:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(null));}
     * </pre>
     *
     * @return the matcher
     * @since 1.3.1
     */
    @Factory
    public static Matcher<Procedure> throwsNoException()
    {
        return new ExceptionMatcher(null);
    }

    /**
     * Assigns an expected cause for evaluation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalStateException.class).withCause(FileNotFoundException.class));
     * }
     * </pre>
     *
     * The matcher matches if the actual cause class is either the same as, or is a child of,
     * the Throwable represented by the specified parameter.
     * <p>
     * For example, if the examined cause is a {@code NullPointerException}, all of the
     * following assertions are valid:
     *
     * <pre>
     * withCause(NullPointerException.class);
     * withCause(RuntimeException.class);
     * withCause(Exception.class);
     * </pre>
     *
     * <p>
     * In other words, the matcher tests whether the actual cause can be converted to the
     * specified class.
     * </p>
     *
     * @param cause the expected cause
     * @return the matcher, incremented with a given cause for testing
     * @since 1.1.0
     */
    public ExceptionMatcher withCause(Class<? extends Throwable> cause)
    {
        expectedCause = cause;
        checkCauseFlag = true;
        causeMatchingStrategy = CauseMatchingStrategy.EXPECTED_CLASS;
        return this;
    }

    /**
     * Assigns another {@code ExceptionMatcher} to be used for the exception cause validation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalArgumentException.class)
     *             .withCause(
     *                 exception(FileNotFoundException.class)
     *                     .withMessage("File p1 not found"));}
     * </pre>
     *
     *
     * @param matcher the matcher to be used in combination for exception cause validation
     * @return the matcher, incremented with the specified matcher for testing
     * @since 1.6.0
     * @see ExceptionMatcher#exception(Class)
     */
    public ExceptionMatcher withCause(ExceptionMatcher matcher)
    {
        causeMatcher = matcher;
        checkCauseFlag = true;
        causeMatchingStrategy = CauseMatchingStrategy.NESTED_MATCHER;
        return this;
    }

    /**
     * Instruct the matcher to ensure that the expected exception has no cause.
     *
     * @return the matcher, enabled for cause cause checking, and expecting none
     * @since 1.6.0
     */
    public ExceptionMatcher withNoCause()
    {
        return withCause((Class<? extends Throwable>) null);
    }

    /**
     * Assigns an external Matcher to be used in combination for the exception message
     * validation.
     * <p>
     * For example:
     * <ul>
     * <li>Combining the ExceptionMatcher and Hamcrest's {@link CoreMatchers}:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessage(equalTo("argument cannot be null")));}
     * </pre>
     *
     * </li>
     *
     * <li>Combining the ExceptionMatcher with {@link CoreMatchers} and {@link StringMatcher}:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(MyException.class)
     *             .withMessage(either(startsWith("ERR-0001"))
     *                 .or(containsAny("division by zero").ignoreCase())));}
     * </pre>
     *
     * </li>
     * </ul>
     *
     * @param matcher the matcher to be used in combination for exception message validation
     * @return the matcher, incremented with the specified matcher for testing
     * @since 1.2.0
     */
    public ExceptionMatcher withMessage(Matcher<String> matcher)
    {
        messageMatcher = matcher;
        checkMessageFlag = true;
        messageMatchingStrategy = MessageMatchingStrategy.EXTERNAL_MATCHER;
        return this;
    }

    /**
     * Assigns an expected message for validation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessage("argument cannot be null"));}
     * </pre>
     *
     * <b>Note:</b> This has the same effect as calling:
     *
     * <pre>
     * withMessage(equalTo("argument cannot be null"));
     * </pre>
     *
     * @param message the message for exception validation
     * @return the matcher, incremented with the specified message for testing
     * @since 1.2.0
     */
    public ExceptionMatcher withMessage(String message)
    {
        return withMessage(CoreMatchers.equalTo(message));
    }

    /**
     * Assigns one or more expected substrings for exception message validation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessageContaining("argument cannot be null"));
     *
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(MyException.class)
     *             .withMessageContaining("ERR-12008", "mandatory"));}
     * </pre>
     *
     * @param substrings one or more substrings for exception message validation
     * @return the matcher, incremented with the specified substring(s) for testing
     * @since 1.1.0
     */
    public ExceptionMatcher withMessageContaining(String... substrings)
    {
        if (substrings != null)
        {
            expectedMessageSubstrings = Arrays.asList(substrings);
        }
        checkMessageFlag = true;
        messageMatchingStrategy = MessageMatchingStrategy.EXPECTED_SUBSTRINGS;
        return this;
    }

    /**
     * Execute the matcher business logic for the specified procedure.
     *
     * @param procedure a procedure supposed to produce an exception to be evaluated
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(Procedure procedure, Description mismatch)
    {
        try
        {
            procedure.execute();
            mismatch.appendText(NEW_LINE_INDENT).appendText("no exception was thrown");
            return expectedException == null;
        }
        catch (Throwable exception)
        {
            return validateFully(exception, mismatch);
        }
    }

    /**
     * Validates the exception class, message and cause.
     *
     * @param throwable the Throwable to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    protected boolean validateFully(Throwable throwable, Description mismatch)
    {
        if (!validateException(throwable, mismatch))
        {
            return false;
        }
        if (checkMessageFlag && !validateMessage(throwable, mismatch))
        {
            return false;
        }
        return !(checkCauseFlag && !validateCause(throwable, mismatch));
    }

    /**
     * Validates the exception class.
     *
     * @param throwable the Throwable to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateException(Throwable throwable, Description mismatch)
    {
        if (expectedException == null || !expectedException.isAssignableFrom(throwable.getClass()))
        {
            mismatch.appendText(NEW_LINE_INDENT).appendText("was ")
                    .appendText(nullSafeClassNameToText(throwable.getClass()));
            return false;
        }
        return true;
    }

    /**
     * Validates the exception message.
     *
     * @param throwable the Throwable whose message is to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateMessage(Throwable throwable, Description mismatch)
    {
        return messageMatchingStrategy.validateMessage(this, throwable, mismatch);
    }

    /**
     * Validates the exception cause.
     *
     * @param throwable the Throwable whose cause is to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateCause(Throwable throwable, Description mismatch)
    {
        return causeMatchingStrategy.validateCause(this, throwable, mismatch);
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @param description the {@link Description} to be appended to
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(NEW_LINE_INDENT).appendText(nullSafeClassNameToText(expectedException));
        if (checkMessageFlag)
        {
            messageMatchingStrategy.describeTo(this, description);
        }
        if (checkCauseFlag)
        {
            causeMatchingStrategy.describeTo(this, description);
        }
    }

    /**
     * Returns a string containing the given class canonical name, or {@code "null"} if the
     * specified class reference is null.
     *
     * @param clazz the class to be parsed
     * @return the a string containing either the class canonical name or {@code "null"}
     * @since 1.1.0
     */
    private static String nullSafeClassNameToText(Class<?> clazz)
    {
        return clazz != null ? clazz.getCanonicalName() : "no exception";
    }

    /**
     * @return the expectedException
     */
    protected Class<? extends Exception> getExpectedException()
    {
        return expectedException;
    }

}
