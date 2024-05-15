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

import static net.obvj.junit.utils.matchers.ExceptionMatcher.exception;
import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsNoException;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAny;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
/**
 * Unit tests for the {@link ExceptionMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
class ExceptionMatcherTest
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

    private static String[] extractMessageLines(Throwable t)
    {
        return t.getMessage().split("\\R");
    }

    // ================================
    // Test methods - start
    // ================================

    @Test
    void throwsException_nullWithRunnableNotThrowingException_succeeeds()
    {
        assertThat(() -> MESSAGE2.contains(MESSAGE1), throwsException(null));
    }

    @Test
    void throwsException_nullWithRunnableThrowingException_failure()
    {
        try
        {
            assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(null));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("no exception"));
            assertThat(lines[3].trim(), equalTo("but:"));
            assertThat(lines[4].trim(), equalTo("was java.lang.NullPointerException"));
        }
    }

    @Test
    void throwsNoException_runnableNotThrowingException_succeeeds()
    {
        assertThat(() -> MESSAGE2.contains(MESSAGE1), throwsNoException());
    }

    @Test
    void throwsNoException_runnableThrowingException_failure()
    {
        try
        {
            assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsNoException());
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("no exception"));
            assertThat(lines[3].trim(), equalTo("but:"));
            assertThat(lines[4].trim(), equalTo("was java.lang.NullPointerException"));
        }
    }

    @Test
    void throwsException_noArgumentWithRunnableThrowingException_success()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException());
    }

    @Test
    void throwsException_noArgumentWithRunnableNotThrowingException_failue()
    {
        try
        {
            assertThat(() -> MESSAGE2.equals(MESSAGE1), throwsException());
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.Exception"));
            assertThat(lines[3].trim(), equalTo("but:"));
            assertThat(lines[4].trim(), equalTo("no exception was thrown"));
        }
    }

    @Test
    void throwsException_correctExceptionWithNoCause_succeeds()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(NullPointerException.class));
    }

    @Test
    void throwsException_parentExceptionExpectedAndChildExceptionThrown_succeeds()
    {
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(RuntimeException.class));
        assertThat(() -> NULL_STRING.equals(MESSAGE1), throwsException(Exception.class));
    }

    @Test
    void throwsException_correctExceptionWithCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(), throwsException(IllegalArgumentException.class));
    }

    @Test
    void throwsException_incorrectException_fails()
    {
        try
        {
            assertThat(() -> NULL_STRING.equals(MESSAGE1),
                    throwsException(IllegalArgumentException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("but:"));
            assertThat(lines[4].trim(), equalTo("was java.lang.NullPointerException"));
        }
    }

    @Test
    void throwsException_runnableThrowingNoException_fails()
    {
        try
        {
            assertThat(() -> MESSAGE1.equals(MESSAGE1),
                    throwsException(NullPointerException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.NullPointerException"));
            assertThat(lines[3].trim(), equalTo("but:"));
            assertThat(lines[4].trim(), equalTo("no exception was thrown"));
        }
    }

    @Test
    void withCause_correctCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(NullPointerException.class));
    }

    @Test
    void withCause_parentCauseExpectedAndIsChildCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class).withCause(RuntimeException.class));
    }

    @Test
    void withCause_incorrectCause_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withCause(FileNotFoundException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("and cause: java.io.FileNotFoundException"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the cause was: java.lang.NullPointerException"));
        }
    }

    @Test
    void withCause_setButNoCause_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withCause(NullPointerException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("and cause: java.lang.NullPointerException"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the cause was null"));
        }
    }

    @Test
    void withCause_causeMatcherAndCorrectCauseClass_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(NullPointerException.class)));
    }

    @Test
    void withCause_causeMatcherAndCauseClassIsChild_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withCause(exception(RuntimeException.class)));
    }

    @Test
    void withCause_causeMatcherAndIncorrectCauseClass_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withCause(exception(FileNotFoundException.class)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("and cause: {"));
            assertThat(lines[4].trim(), equalTo("java.io.FileNotFoundException"));
            assertThat(lines[5].trim(), equalTo("}"));
            assertThat(lines[6].trim(), equalTo("but:"));
            assertThat(lines[7].trim(), equalTo("the cause did not match: {"));
            assertThat(lines[8].trim(), equalTo("was java.lang.NullPointerException"));
            assertThat(lines[9].trim(), equalTo("}"));
        }
    }

    @Test
    void withCause_causeMatcherAndCorrectCauseClassAndMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                throwsException(IllegalArgumentException.class)
                        .withMessage(IAE_MESSAGE)
                        .withCause(exception(NullPointerException.class)
                                .withMessage(NPE_MESSAGE)));
    }

    @Test
    void withCause_causeMatcherAndCorrectCauseClassAndIncorrectMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                    throwsException(IllegalArgumentException.class).withCause(
                            exception(NullPointerException.class).withMessage(IAE_MESSAGE)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("and cause: {"));
            assertThat(lines[4].trim(), equalTo("java.lang.NullPointerException"));
            assertThat(lines[5].trim(), equalTo("with message: \"iae message\""));
            assertThat(lines[6].trim(), equalTo("}"));
            assertThat(lines[7].trim(), equalTo("but:"));
            assertThat(lines[8].trim(), equalTo("the cause did not match: {"));
            assertThat(lines[9].trim(), equalTo("the message was \"npe message\""));
            assertThat(lines[10].trim(), equalTo("}"));
        }
    }


    @Test
    void withNoCause_nullAndNoCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withNoCause());
    }

    @Test
    void withNoCause_nullButHasCause_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_CAUSE_NPE.run(),
                    throwsException(IllegalArgumentException.class).withNoCause());
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("and cause: no exception"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the cause was: java.lang.NullPointerException"));
        }
    }

    @Test
    void withMessage_equalToAndStringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(equalTo(MESSAGE_ERR_0001_FULL)));
    }

    @Test
    void withMessage_equalToAndStringNotMatching_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class).withMessage(equalTo(MESSAGE1)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message: \"message1\""));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessage_combinedStringMatcher_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(containsAny(MESSAGE1).ignoreCase()));
    }

    @Test
    void withMessage_combinedMatcherAndStringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class)
                        .withMessage(either(startsWith(ERR_0001)).or(containsAny(MESSAGE1).ignoreCase())));
    }

    @Test
    void withMessage_combinedMatcherAndStringNotMatching_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class)
                        .withMessage(either(startsWith(MESSAGE1)).or(containsAny(MESSAGE2).ignoreCase())));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message: (a string starting with \"message1\" or a string containing ANY of the specified substrings [message2] (ignore case))"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessage_startsWithButNoMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withMessage(startsWith(ERR_0001)));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("with message: a string starting with \"ERR-0001\""));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was null"));
        }
    }

    @Test
    void withMessage_nullAndHasNotMessage_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessage((Matcher<String>) null));
    }

    @Test
    void withMessage_nullButHasMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class)
                            .withMessage((Matcher<String>) null));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message: <null>"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessage_stringAndMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessage(MESSAGE_ERR_0001_FULL));
    }

    @Test
    void withMessage_stringAndNotMatching_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class).withMessage(MESSAGE1));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message: \"message1\""));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessageContaining_oneSubstringMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1));
    }

    @Test
    void withMessageContaining_twoSubstringsMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(ERR_0001, MESSAGE1));
    }

    @Test
    void withMessageContaining_oneSubstringNotMatching_suceeds()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class).withMessageContaining(MESSAGE2));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message2]"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessageContaining_twoSubstringsBothMatching_suceeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(ERR_0001, MESSAGE1));
    }

    @Test
    void withMessageContaining_twoSubstringsButOneNotMatching_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class).withMessageContaining(ERR_0001, MESSAGE2));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [ERR-0001, message2]"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessageContaining_oneSubstringMatchingButExceptionNotMatching_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withMessageContaining(MESSAGE2));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message2]"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("was java.lang.IllegalStateException"));
        }
    }

    @Test
    void withMessageContaining_oneSubstringButNoMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                    throwsException(IllegalArgumentException.class)
                            .withMessageContaining(MESSAGE1));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalArgumentException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message1]"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was null"));
        }
    }

    @Test
    void withMessageContaining_nullAndNoMessage_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_IAE_WITH_NO_CAUSE_AND_NO_MESSAGE.run(),
                throwsException(IllegalArgumentException.class).withMessageContaining((String[]) null));
    }

    @Test
    void withMessageContaining_nullButHasMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_NO_CAUSE.run(),
                    throwsException(IllegalStateException.class)
                            .withMessageContaining((String[]) null));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: []"));
            assertThat(lines[4].trim(), equalTo("but:"));
            assertThat(lines[5].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withMessageContainingAndCause_matchingMessageAndCause_succeeds()
    {
        assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1)
                        .withCause(NullPointerException.class));
    }

    @Test
    void withMessageContainingAndCause_matchingMessageButIncorrectCause_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                    throwsException(IllegalStateException.class).withMessageContaining(MESSAGE1)
                            .withCause(FileNotFoundException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message1]"));
            assertThat(lines[4].trim(), equalTo("and cause: java.io.FileNotFoundException"));
            assertThat(lines[5].trim(), equalTo("but:"));
            assertThat(lines[6].trim(), equalTo("the cause was: java.lang.NullPointerException"));
        }
    }

    @Test
    void withMessageContainingAndCause_matchingCauseButIncorrectMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                    throwsException(IllegalStateException.class).withMessageContaining(MESSAGE2)
                            .withCause(NullPointerException.class));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message2]"));
            assertThat(lines[4].trim(), equalTo("and cause: java.lang.NullPointerException"));
            assertThat(lines[5].trim(), equalTo("but:"));
            assertThat(lines[6].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void withCauseAndMessageContaining_matchingMessageButIncorrectCause_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                    throwsException(IllegalStateException.class)
                            .withCause(FileNotFoundException.class)
                            .withMessageContaining(MESSAGE1));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message1]"));
            assertThat(lines[4].trim(), equalTo("and cause: java.io.FileNotFoundException"));
            assertThat(lines[5].trim(), equalTo("but:"));
            assertThat(lines[6].trim(), equalTo("the cause was: java.lang.NullPointerException"));
        }
    }

    @Test
    void withCauseAndMessageContaining_matchingCauseButIncorrectMessage_fails()
    {
        try
        {
            assertThat(() -> RUNNABLE_THROWS_ISE_WITH_MESSAGE_AND_CAUSE_NPE.run(),
                    throwsException(IllegalStateException.class)
                            .withCause(NullPointerException.class)
                            .withMessageContaining(MESSAGE2));
        }
        catch (AssertionError e)
        {
            String[] lines = extractMessageLines(e);
            assertThat(lines[1].trim(), equalTo("Expected:"));
            assertThat(lines[2].trim(), equalTo("java.lang.IllegalStateException"));
            assertThat(lines[3].trim(), equalTo("with message containing: [message2]"));
            assertThat(lines[4].trim(), equalTo("and cause: java.lang.NullPointerException"));
            assertThat(lines[5].trim(), equalTo("but:"));
            assertThat(lines[6].trim(), equalTo("the message was \"[ERR-0001] message1\""));
        }
    }

    @Test
    void throwsException_checkedException_success()
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
