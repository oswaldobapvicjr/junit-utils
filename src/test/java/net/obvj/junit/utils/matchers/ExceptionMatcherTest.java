package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;

/**
 * Unit tests for the {@link ExceptionMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class ExceptionMatcherTest
{
    private static final String MESSAGE1 = "message1";
    private static final String MESSAGE11 = "message11";
    private static final String NULL_STRING = null;

    private static final Runnable RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE = () ->
    {
        throw new IllegalStateException(MESSAGE1);
    };

    private static final Runnable RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE = () ->
    {
        throw new IllegalStateException(MESSAGE1, new NullPointerException());
    };

    private static final Runnable RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE = () ->
    {
        throw new IllegalArgumentException(new NullPointerException());
    };

    private static final Runnable RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE = () ->
    {
        throw new IllegalArgumentException();
    };

    @Test
    public void throwsException_nullWithRunnableNotThrowingException_succeeeds()
    {
        assertThat(() -> MESSAGE11.contains(MESSAGE1), throwsException(null));
    }

    @Test
    public void throwsException_correctExceptionWithNotCause_succeeds()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(NullPointerException.class));
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
    public void throwsException_noException_fails()
    {
        assertThat(() -> MESSAGE1.equals(MESSAGE1), throwsException(NullPointerException.class));
    }

    @Test
    public void withCause_correctCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(NullPointerException.class));
    }

    @Test(expected = AssertionError.class)
    public void withCause_incorrectCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(FileNotFoundException.class));
    }

    @Test(expected = AssertionError.class)
    public void withCause_noCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withCause(NullPointerException.class));
    }

    @Test
    public void withCauseNull_noCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withCause(null));
    }

    @Test(expected = AssertionError.class)
    public void withCauseNull_hasCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(null));
    }

    @Test
    public void withMessageContaining_expectedMessageMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_expectedMessageNotMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE11));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_expectedMessageMatchingButIncorrectException_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(MESSAGE11));
    }

    @Test(expected = AssertionError.class)
    public void withMessageContaining_setButNoMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(MESSAGE1));
    }

    @Test
    public void withMessageContainingNull_noMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(null));
    }

    @Test(expected = AssertionError.class)
    public void withMessageNull_butHasMessage_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining(null));
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
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE11)
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
                        .withMessageContaining(MESSAGE11));
    }
}
