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

}
