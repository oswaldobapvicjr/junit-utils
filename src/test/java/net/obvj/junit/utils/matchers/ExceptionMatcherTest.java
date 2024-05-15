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

import static net.obvj.junit.utils.matchers.ExceptionMatcher.*;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAny;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;

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
    private static final String IAE_MESSAGE = "iae message";
    private static final String NPE_MESSAGE = "npe message";
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
        throw new IllegalArgumentException(IAE_MESSAGE, new NullPointerException(NPE_MESSAGE));
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
    public void throwsException_nullWithRunnableThrowingException_failure()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(null));
    }

    @Test
    public void throwsNoException_runnableNotThrowingException_succeeeds()
    {
        assertThat(() -> MESSAGE2.contains(MESSAGE1), throwsNoException());
    }

    @Test(expected = AssertionError.class)
    public void throwsNoException_runnableThrowingException_failure()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsNoException());
    }

    @Test
    public void throwsException_noArgumentWithRunnableThrowingException_success()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException());
    }

    @Test(expected = AssertionError.class)
    public void throwsException_noArgumentWithRunnableNotThrowingException_failue()
    {
        assertThat(() -> MESSAGE2.equals(MESSAGE1), throwsException());
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
    public void withCause_causeMatcherAndCorrectCauseClass_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(NullPointerException.class)));
    }

    @Test
    public void withCause_causeMatcherAndCauseClassIsChild_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(RuntimeException.class)));
    }

    @Test(expected = AssertionError.class)
    public void withCause_causeMatcherAndIncorrectCauseClass_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(FileNotFoundException.class)));
    }

    @Test
    public void withCause_causeMatcherAndCorrectCauseClassAndMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withMessage(IAE_MESSAGE)
                        .withCause(exception(NullPointerException.class)
                                .withMessage(NPE_MESSAGE)));
    }

    @Test(expected = AssertionError.class)
    public void withCause_causeMatcherAndCorrectCauseClassAndIncorrectMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(NullPointerException.class)
                                .withMessage(IAE_MESSAGE)));
    }


    @Test
    public void withNoCause_nullAndNoCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withNoCause());
    }

    @Test(expected = AssertionError.class)
    public void withNoCause_nullButHasCause_fails()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withNoCause());
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

    @Test
    public void throwsException_checkedException_success()
    {
        assertThat(() ->
        {
            throw new IOException(new FileNotFoundException());
        },
        throwsException(IOException.class).withCause(FileNotFoundException.class));
    }

    @Test
    void with_validFunctionAndMatcherMatching_success()
    {
        assertThat(() ->
        {
            throw new MyCustomException(MESSAGE1, ERR_0001, 1910);
        },
        throwsException(MyCustomException.class)
                .with(MyCustomException::getCustomString, equalTo(ERR_0001)));
    }

    @Test
    void with_validFunctionAndMatcherNotMatching_exception()
    {
        try
        {
            assertThat(() ->
            {
                throw new MyCustomException(MESSAGE1, ERR_0001, 1910);
            },
            throwsException(MyCustomException.class)
                    .with(MyCustomException::getCustomString, startsWith("ERR-0002")));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo(MyCustomException.class.getCanonicalName()));
            assertThat(lines[3].trim(), equalTo("and the function #1: a string starting with \"ERR-0002\""));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the value retrieved by the function #1 was \"ERR-0001\""));
        }
    }

    @Test
    void with_multipleValidFunctionsAndMatchersMatching_success()
    {
        assertThat(() ->
        {
            throw new MyCustomException(MESSAGE1, ERR_0001, 1910);
        },
        throwsException(MyCustomException.class)
                .with(MyCustomException::getCustomString, equalTo(ERR_0001))
                .with(MyCustomException::getCode, equalTo(1910))
                .with(MyCustomException::getLocalizedMessage, equalTo(MESSAGE1)));
    }

    @Test
    void with_multipleValidFunctionsAndOneMatcherNotMatching_exception()
    {
        try
        {
            assertThat(() ->
            {
                throw new MyCustomException(MESSAGE1, ERR_0001, 1910);
            },
            throwsException(MyCustomException.class)
                    .with(MyCustomException::getCustomString, equalTo(ERR_0001))
                    .with(MyCustomException::getCode, equalTo(1910))
                    .with(MyCustomException::getLocalizedMessage, endsWith(MESSAGE2)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo(MyCustomException.class.getCanonicalName()));
            assertThat(lines[3].trim(), equalTo("and the function #1: \"ERR-0001\""));
            assertThat(lines[4].trim(), equalTo("and the function #2: <1910>"));
            assertThat(lines[5].trim(), equalTo("and the function #3: a string ending with \"message2\""));
            assertThat(lines[6].trim(), equalTo("but:"));
            assertThat(lines[7].trim(), equalTo("the value retrieved by the function #3 was \"message1\""));
        }
    }

    @Test
    void with_mixedMessageContainingAndValidFunctionsAndAllMatching_success()
    {
        assertThat(() ->
        {
            throw new MyCustomException(MESSAGE2, ERR_0001, 1911);
        },
        throwsException(MyCustomException.class)
                .withMessageContaining(MESSAGE2)
                .with(MyCustomException::getCustomString, equalTo(ERR_0001))
                .with(MyCustomException::getCode, equalTo(1911)));
    }

    @Test
    void with_mixedMessageContainingAndValidFunctionsAndOneMatcherNotMatching_exception()
    {
        try
        {
            assertThat(() ->
            {
                throw new MyCustomException(MESSAGE2, ERR_0001, 1910);
            },
            throwsException(MyCustomException.class)
                    .withMessageContaining(MESSAGE2)
                    .with(MyCustomException::getCustomString, equalTo(ERR_0001))
                    .with(MyCustomException::getCode, equalTo(1911)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo(MyCustomException.class.getCanonicalName()));
            assertThat(lines[3].trim(), equalTo("with message containing: [message2]"));
            assertThat(lines[4].trim(), equalTo("and the function #1: \"ERR-0001\""));
            assertThat(lines[5].trim(), equalTo("and the function #2: <1911>"));
            assertThat(lines[6].trim(), equalTo("but:"));
            assertThat(lines[7].trim(), equalTo("the value retrieved by the function #2 was <1910>"));
        }
    }

}
