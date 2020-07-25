package net.obvj.junit.utils.matchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
 *             .withMessageContaining("ERR-12008"));
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
    private List<String> expectedMessageSubstrings = Collections.emptyList();


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
     * Creates a matcher that matches if the examined procedure throws a given exception.
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
     * @param exception the exception to be checked
     * @return the matcher
     */
    @Factory
    public static ExceptionMatcher throwsException(Class<? extends Exception> exception)
    {
        return new ExceptionMatcher(exception);
    }

    /**
     * Assigns an expected cause for evaluation.
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
     * @param cause the cause to be checked
     * @return the matcher, incremented with a given cause for testing
     */
    public ExceptionMatcher withCause(Class<? extends Throwable> cause)
    {
        expectedCause = cause;
        checkCauseFlag = true;
        return this;
    }

    /**
     * Assigns one or more expected substrings for the exception message evaluation.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessageContaining("argument cannot be null");
     *
     * assertThat(() -> obj.doStuff(null),
     *         throwsException(MyException.class)
     *             .withMessageContaining("ERR-12008", "mandatory");
     * }
     * </pre>
     *
     * @param substrings a substring of the exception message to be checked
     * @return the matcher, incremented with a given message for testing
     */
    public ExceptionMatcher withMessageContaining(String... substrings)
    {
        if (substrings != null)
        {
            expectedMessageSubstrings = Arrays.asList(substrings);
        }
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
            return validateFully(exception, mismatch);
        }
    }

    /**
     * Validates the exception class, message and cause.
     *
     * @param exception the exception to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateFully(Exception exception, Description mismatch)
    {
        if (!validateException(exception, mismatch))
        {
            return false;
        }
        if (checkMessageFlag && !expectedMessageSubstrings.isEmpty() && !validateMessage(exception, mismatch))
        {
            return false;
        }
        return !(checkCauseFlag && !validateCause(exception, mismatch));
    }

    /**
     * Validates the exception class.
     *
     * @param exception the exception to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateException(Exception exception, Description mismatch)
    {
        if (!exception.getClass().equals(expectedException))
        {
            mismatch.appendText(NEW_LINE_INDENT).appendText("was ")
                    .appendText(nullSafeClassNameToText(exception.getClass()));
            return false;
        }
        return true;
    }

    /**
     * Validates the exception message.
     *
     * @param exception the exception which message is to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateMessage(Exception exception, Description mismatch)
    {
        String message = exception.getMessage();
        if (message == null)
        {
            mismatch.appendText(NEW_LINE_INDENT).appendText("the message was null");
            return false;
        }
        for (String substring : expectedMessageSubstrings)
        {
            if (!message.contains(substring))
            {
                mismatch.appendText(NEW_LINE_INDENT).appendText("the message was ").appendValue(message);
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the exception cause.
     *
     * @param exception the exception which cause is to be validated
     * @param mismatch  the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    private boolean validateCause(Exception exception, Description mismatch)
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
        description.appendText(NEW_LINE_INDENT).appendText(nullSafeClassNameToText(expectedException));
        if (checkMessageFlag)
        {
            description.appendText(NEW_LINE_INDENT).appendText("and message containing: ")
                    .appendText(expectedMessageSubstrings.toString());
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
     * @return the a string containing either the class canonical name or {@code "null"}
     */
    private String nullSafeClassNameToText(Class<?> clazz)
    {
        return clazz != null ? clazz.getCanonicalName() : "null";
    }

}
