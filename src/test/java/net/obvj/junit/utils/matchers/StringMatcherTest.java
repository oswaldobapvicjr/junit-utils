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

import static net.obvj.junit.utils.matchers.StringMatcher.containsAll;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAny;
import static net.obvj.junit.utils.matchers.StringMatcher.containsNone;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit tests for the {@link StringMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class StringMatcherTest
{
    private static final String THE_QUICK_BROWN_FOX = "The quick brown fox jumps over the lazy dog";
    private static final String DOG = "dog";
    private static final String FOX = "fox";
    private static final String DRAGON = "dragon";
    private static final String MANTICORE = "manticore";
    private static final String LAZY_UPPER = "LAZY";

    @Test
    public void containsAll_allSubstringsFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(DOG, FOX));
    }

    @Test(expected = AssertionError.class)
    public void containsAll_unexpectedSubstring_fails()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(DRAGON));
    }

    @Test(expected = AssertionError.class)
    public void containsAll_nullSource_suceeds()
    {
        assertThat(null, containsAll(DOG));
    }

    @Test
    public void containsAny_oneSubstringsFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAny(DRAGON, FOX));
    }

    @Test(expected = AssertionError.class)
    public void containsAny_noneOfSubstringsFound_fails()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAny(DRAGON));
    }

    @Test
    public void containsNone_noSubstringFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsNone(DRAGON, MANTICORE));
    }

    @Test(expected = AssertionError.class)
    public void containsNone_unexpectedString_fails()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsNone(DRAGON, MANTICORE, DOG));
    }

    @Test
    public void ignoreCase_substringMatches_success()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(LAZY_UPPER).ignoreCase());
        assertThat(THE_QUICK_BROWN_FOX, containsAny(LAZY_UPPER).ignoreCase());
    }

    @Test(expected = AssertionError.class)
    public void ignoreCase_substringNotMatches_fails()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(DRAGON).ignoreCase());
    }

}
