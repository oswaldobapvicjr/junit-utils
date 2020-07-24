package net.obvj.junit.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * A Matcher that matches if a procedure throws an expected exception.
 * <p>
 * For example:
 *
 * <pre>
 * {@code
 * assertThat(() -> obj.doStuff("p1"),
 *         throwsException(NullPointerException.class));
 * }
 * </pre>
 *
 * <p>
 * Exception details, such as message and cause, can also be evaluated using the fluent
 * interface. For example:
 * </p>
 *
 * <pre>
 * {@code
 * assertThat(() -> obj.doStuff("p1"),
 *         throwsException(IllegalStateException.class)
 *             .withCause(FileNotFoundException.class));
 *
 * assertThat(() -> obj.doStuff(null),
 *         throwsException(IllegalArgumentException.class)
 *             .withMessageContaining("argument cannot be null"));
 * }
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ExceptionMatcher extends TypeSafeDiagnosingMatcher<Runnable>
{
    private static final String NEW_LINE_INDENT = "\n          ";

    private final Class<? extends Exception> expectedException;

    private boolean checkCauseFlag = false;
    private boolean checkMessageFlag = false;

    private Class<? extends Throwable> expectedCause;
    private String expectedMessage;


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
     * Creates a matcher that matches if the examined procedure throws an expected exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalStateException.class));
     * }
     * </pre>
     *
     * @param expectedException the exception to be checked
     * @return the matcher
     */
    @Factory
    public static ExceptionMatcher throwsException(Class<? extends Exception> expectedException)
    {
        return new ExceptionMatcher(expectedException);
    }

    /**
     * Increments the matcher with an expected cause for evaluation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalStateException.class)
     *             .withCause(FileNotFoundException.class));
     * }
     * </pre>
     *
     * @param expectedCause the cause to be checked
     * @return the matcher, incremented with a given cause for testing
     */
    public ExceptionMatcher withCause(Class<? extends Throwable> expectedCause)
    {
        this.expectedCause = expectedCause;
        checkCauseFlag = true;
        return this;
    }

    /**
     * Increments the matcher with expected message content for evaluation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessageContaining("argument cannot be null");
     * }
     * </pre>
     *
     * @param expectedMessage a substring of the exception message to be checked
     * @return the matcher, incremented with a given message for testing
     */
    public ExceptionMatcher withMessageContaining(String expectedMessage)
    {
        this.expectedMessage = expectedMessage;
        checkMessageFlag = true;
        return this;
    }

    /**
     * Execute the matcher business logic for the specified procedure.
     *
     * @param runnable a procedure supposed to produce an exception to be evaluated
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(Runnable runnable, Description mismatch)
    {
        try
        {
            runnable.run();
            mismatch.appendText(NEW_LINE_INDENT).appendText("no exception was thrown");
            return expectedException == null;
        }
        catch (Exception exception)
        {
            return validateException(exception, mismatch);
        }
    }

    /**
     * Validates the exception class, message and cause.
     *
     * @param exception the exception to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     */
    private boolean validateException(Exception exception, Description mismatch)
    {
        if (!exception.getClass().equals(expectedException))
        {
            mismatch.appendText(NEW_LINE_INDENT).appendText("was ")
                    .appendText(nullSafeClassNameToText(exception.getClass()));
            return false;
        }
        if (checkMessageFlag && expectedMessage != null)
        {
            String message = exception.getMessage();
            if (message == null)
            {
                mismatch.appendText(NEW_LINE_INDENT).appendText("the message was null");
                return false;
            }
            if (!message.contains(expectedMessage))
            {
                mismatch.appendText(NEW_LINE_INDENT).appendText("the message was").appendValue(message);
                return false;
            }
        }
        if (checkCauseFlag)
        {
            Throwable cause = exception.getCause();
            if (cause == null && expectedCause != null)
            {
                mismatch.appendText(NEW_LINE_INDENT).appendText("the cause was null");
                return false;
            }
            if (cause != null && !cause.getClass().equals(expectedCause))
            {
                mismatch.appendText(NEW_LINE_INDENT).appendText("the cause was: ")
                        .appendText(nullSafeClassNameToText(cause.getClass()));
                return false;
            }
        }
        return true;
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @see org.hamcrest.SelfDescribing#describeTo(Description)
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText(nullSafeClassNameToText(expectedException));
        if (checkMessageFlag)
        {
            description.appendText(NEW_LINE_INDENT).appendText("and message containing: ").appendValue(expectedMessage);
        }
        if (checkCauseFlag)
        {
            description.appendText(NEW_LINE_INDENT).appendText("and cause: ")
                    .appendText(nullSafeClassNameToText(expectedCause));
        }
    }

    /**
     * Returns a string containing the given class canonical name, or {@code "null"} if the
     * specified class reference is null.
     *
     * @param clazz the class to be parsed
     * @return the class canonical name, or the string: {@code "null"}
     */
    private String nullSafeClassNameToText(Class<?> clazz)
    {
        return clazz != null ? clazz.getCanonicalName() : "null";
    }

}
