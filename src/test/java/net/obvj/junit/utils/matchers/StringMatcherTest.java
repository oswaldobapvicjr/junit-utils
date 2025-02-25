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

import static net.obvj.junit.utils.matchers.StringMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link StringMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
class StringMatcherTest
{
    private static final String THE_QUICK_BROWN_FOX = "The quick brown fox jumps over the lazy dog";
    private static final String DOG = "dog";
    private static final String FOX = "fox";
    private static final String DRAGON = "dragon";
    private static final String MANTICORE = "manticore";
    private static final String LAZY_UPPER = "LAZY";

    @Test
    void containsAll_allSubstringsFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(DOG, FOX));
    }

    @Test
    void containsAll_unexpectedSubstring_fails()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(THE_QUICK_BROWN_FOX, containsAll(DRAGON)));
    }

    @Test
    void containsAll_nullSource_suceeds()
    {
        assertThrows(AssertionError.class, () -> assertThat(null, containsAll(DOG)));
    }

    @Test
    void containsAny_oneSubstringsFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAny(DRAGON, FOX));
    }

    @Test
    void containsAny_noneOfSubstringsFound_fails()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(THE_QUICK_BROWN_FOX, containsAny(DRAGON)));
    }

    @Test
    void containsNone_noSubstringFound_suceeds()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsNone(DRAGON, MANTICORE));
    }

    @Test
    void containsNone_unexpectedString_fails()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(THE_QUICK_BROWN_FOX, containsNone(DRAGON, MANTICORE, DOG)));
    }

    @Test
    void ignoreCase_substringMatches_success()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAll(LAZY_UPPER).ignoreCase());
        assertThat(THE_QUICK_BROWN_FOX, containsAny(LAZY_UPPER).ignoreCase());
    }

    @Test
    void ignoreCase_substringNotMatches_fails()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(THE_QUICK_BROWN_FOX, containsAll(DRAGON).ignoreCase()));
    }

    @Test
    void containsAllInSequence_oneSubstringOnlyAndFound_success()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAllInSequence(FOX));
    }

    @Test
    void containsAllInSequence_oneSubstringOnlyAndNotFound_fails()
    {
        AssertionError error = assertThrows(AssertionError.class,  () -> assertThat(THE_QUICK_BROWN_FOX, containsAllInSequence(DRAGON)));
        assertLines(error.getMessage(), "", "Expected: a string containing ALL (in sequence) of the specified substrings [dragon]",
                "     but: the substring \"dragon\" was not found in: \"" + THE_QUICK_BROWN_FOX + "\"");
    }

    @Test
    void containsAllInSequence_twoSubstringsAndBothFoundInSequenceFar_success()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAllInSequence(FOX, DOG));
    }

    @Test
    void containsAllInSequence_twoSubstringsAndBothFoundInSequenceClose_success()
    {
        assertThat(THE_QUICK_BROWN_FOX, containsAllInSequence("lazy", " dog"));
    }

    @Test
    void containsAllInSequence_secondSubstringNotInSequence_fails()
    {
        AssertionError error = assertThrows(AssertionError.class,  () -> assertThat(THE_QUICK_BROWN_FOX, containsAllInSequence(DOG, FOX)));
        assertLines(error.getMessage(), "", "Expected: a string containing ALL (in sequence) of the specified substrings [dog, fox]",
                "     but: the substring \"fox\" was not found after \"dog\" in: \"" + THE_QUICK_BROWN_FOX + "\"");
    }

    void assertLines(String actualString, String... expectedlines)
    {
        List<String> expectedLines = Arrays.asList(expectedlines);
        List<String> actualStringAsList = Arrays.asList(actualString.split(System.getProperty("line.separator")));
        assertLinesMatch(expectedLines, actualStringAsList);
    }

}
