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

import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.obvj.junit.utils.matchers.StringMatcher.Strategy;

/**
 * Unit tests for the {@link AdvancedMatchers}.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.1
 */
public class AdvancedMatchersTest
{
    private static final String STRING1 = "string1";
    private static final String STRING2 = "string2";

    /**
     * This test serves two purposes: check that the instanatiationNotAllowed() method creates
     * the matcher accordingly, and secure that the AdvancedMatchers class cannot be
     * instantiated.
     */
    @Test
    public void instantiationNotAllowed_createMatcher()
    {
        assertThat(AdvancedMatchers.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class)
                .withMessage(containsAll("instantiation not allowed").ignoreCase()));
    }

    @Test
    public void throwsException_noArgument_createExceptionMatcherAccordingly()
    {
        ExceptionMatcher matcher = throwsException();
        assertThat(matcher.getExpectedException(), is(equalTo(Exception.class)));
    }

    @Test
    public void throwsNoException_validProcedure_success()
    {
        assertThat(() -> STRING1.contains(STRING1), throwsNoException());
    }

    @Test(expected = AssertionError.class)
    public void throwsNoException_procedureThrowsException_failure()
    {
        assertThat(() -> ((String) null).contains(STRING1), throwsNoException());
    }

    @Test
    public void throwsException_validClass_createExceptionMatcherAccordingly()
    {
        ExceptionMatcher matcher = throwsException(FileNotFoundException.class);
        assertThat(matcher.getExpectedException(), is(equalTo(FileNotFoundException.class)));
    }

    @Test
    public void throwsException_procedureThrowingCheckedException_success()
    {
        assertThat(() ->
        {
            throw new IOException("invalid");
        },
        throwsException(IOException.class).withMessage("invalid"));
    }

    @Test
    public void containsAll_moreThanOneString_createStringMatcherAccordingly()
    {
        StringMatcher matcher = containsAll(STRING1, STRING2);
        assertThat(matcher.getStrategy(), is(equalTo(Strategy.ALL)));
        assertThat(matcher.getSubstrings(), is(equalTo(Arrays.asList(STRING1, STRING2))));
    }

    @Test
    public void containsAny_moreThanOneString_createStringMatcherAccordingly()
    {
        StringMatcher matcher = containsAny(STRING1, STRING2);
        assertThat(matcher.getStrategy(), is(equalTo(Strategy.ANY)));
        assertThat(matcher.getSubstrings(), is(equalTo(Arrays.asList(STRING1, STRING2))));
    }

    @Test
    public void containsNone_moreThanOneString_createStringMatcherAccordingly()
    {
        StringMatcher matcher = containsNone(STRING1, STRING2);
        assertThat(matcher.getStrategy(), is(equalTo(Strategy.NONE)));
        assertThat(matcher.getSubstrings(), is(equalTo(Arrays.asList(STRING1, STRING2))));
    }

    @Test
    public void isPositive_validNumbers_validatesAccordingly()
    {
        assertThat(Integer.MAX_VALUE, isPositive());
        assertThat(Integer.MIN_VALUE, not(isPositive()));
    }

    @Test
    public void isNegative_validNumbers_validatesAccordingly()
    {
        assertThat(Integer.MIN_VALUE, isNegative());
        assertThat(Integer.MAX_VALUE, not(isNegative()));
    }

}
