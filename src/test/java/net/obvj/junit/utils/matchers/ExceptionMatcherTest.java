package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAny;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;

import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * Unit tests for the {@link ExceptionMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ExceptionMatcherTest
{
    private static final String ERR_0001 = "ERR-0001";
    private static final String MESSAGE1 = "message1";
    private static final String MESSAGE2 = "message2";
    private static final String MESSAGE_ERR_0001_FULL = "[ERR-0001] message1";
    private static final String NULL_STRING = null;

    private static final Runnable RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE = () ->
    {
        throw new IllegalStateException(MESSAGE_ERR_0001_FULL);
    };

    private static final Runnable RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE = () ->
    {
        throw new IllegalStateException(MESSAGE_ERR_0001_FULL, new NullPointerException());
    };

    private static final Runnable RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE = () ->
    {
        throw new IllegalArgumentException(new NullPointerException());
    };

    private static final Runnable RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE = () ->
    {
        throw new IllegalArgumentException();
    };

    // ================================
    // Test methods - start
    // ================================

    @Test
    public void throwsException_nullWithRunnableNotThrowingException_succeeeds()
    {
        assertThat(() -> MESSAGE2.contains(MESSAGE1), throwsException(null));
    }

    @Test(expected = AssertionError.class)
    public void throwsException_nullWithRunnableThrowingException_fails()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(null));
    }

    @Test
    public void throwsException_correctExceptionWithNoCause_succeeds()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(NullPointerException.class));
    }

    @Test
    public void throwsException_parentExceptionExpectedAndChildExceptionThrown_succeeds()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(RuntimeException.class));
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(Exception.class));
    }

    @Test
    public void throwsException_correctExceptionWithCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(), throwsException(IllegalArgumentException.class));
    }

    @Test(expected = AssertionError.class)
    public void throwsException_incorrectException_fails()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(IllegalArgumentException.class));
    }

    @Test(expected = AssertionError.class)
    public void throwsException_runnableThrowingNoException_fails()
    {
        assertThat(() -> MESSAGE1.equals(MESSAGE1), throwsException(NullPointerException.class));
    }

    @Test
    public void withCause_correctCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(NullPointerException.class));
    }

    @Test
    public void withCause_parentCauseExpectedAndIsChildCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(RuntimeException.class));
    }

    @Test(expected = AssertionError.class)
    public void withCause_incorrectCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(FileNotFoundException.class));
    }

    @Test(expected = AssertionError.class)
    public void withCause_setButNoCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withCause(NullPointerException.class));
    }

    @Test
    public void withCause_nullAndNoCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withCause(null));
    }

    @Test(expected = AssertionError.class)
    public void withCause_nullButHasCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(null));
    }

    @Test
    public void withMessage_equalToAndStringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(equalTo(MESSAGE_ERR_0001_FULL)));
    }

    @Test(expected = AssertionError.class)
    public void withMessage_equalToAndStringNotMatching_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(equalTo(MESSAGE1)));
    }

    @Test
    public void withMessage_combinedStringMatcher_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(containsAny(MESSAGE1).ignoreCase()));
    }

    @Test
    public void withMessage_combinedMatcherAndStringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class)
                        .withMessage(either(startsWith(ERR_0001)).or(containsAny(MESSAGE1).ignoreCase())));
    }

    @Test(expected = AssertionError.class)
    public void withMessage_combinedMatcherAndStringNotMatching_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class)
                        .withMessage(either(startsWith(MESSAGE1)).or(containsAny(MESSAGE2).ignoreCase())));
    }

    @Test(expected = AssertionError.class)
    public void withMessage_startsWithButNoMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessage(startsWith(ERR_0001)));
    }

    @Test
    public void withMessage_nullAndHasNotMessage_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessage((Matcher<String>) null));
    }

    @Test(expected = AssertionError.class)
    public void withMessage_nullButHasMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage((Matcher<String>) null));
    }

    @Test
    public void withMessage_stringAndMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(MESSAGE_ERR_0001_FULL));
    }

    @Test(expected = AssertionError.class)
    public void withMessage_stringAndNotMatching_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(MESSAGE1));
    }

    @Test
    public void withMessageContaining_oneSubstringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1));
    }

    @Test
    public void withMessageContaining_twoSubstringsMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(ERR_0001, MESSAGE1));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_oneSubstringNotMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE2));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_twoSubstringsButOneNotMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(ERR_0001, MESSAGE2));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_oneSubstringMatchingButExceptionNotMatching_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(MESSAGE2));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_oneSubstringButNoMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(MESSAGE1));
    }

    @Test
    public void withMessageContaining_nullAndNoMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining((String[]) null));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_nullButHasMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining((String[]) null));
    }

    @Test
    public void withMessageContainingAndCause_matchingMessageAndCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1)
                        .withCause(NullPointerException.class));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContainingAndCause_matchingMessageButIncorrectCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1)
                        .withCause(FileNotFoundException.class));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContainingAndCause_matchingCauseButIncorrectMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE2)
                        .withCause(FileNotFoundException.class));
    }

    @Test(expected = AssertionError.class)
    public void withCauseAndMessageContaining_matchingMessageButIncorrectCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withCause(FileNotFoundException.class)
                        .withMessageContaining(MESSAGE1));
    }

    @Test(expected = AssertionError.class)
    public void withCauseAndMessageContaining_matchingCauseButIncorrectMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withCause(FileNotFoundException.class)
                        .withMessageContaining(MESSAGE2));
    }
}
