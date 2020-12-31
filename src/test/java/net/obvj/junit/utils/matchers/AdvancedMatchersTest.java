package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
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
        assertThat(AdvancedMatchers.class, instantiationNotAllowed());
    }

    @Test
    public void throwsException_validClass_createExceptionMatcherAccordingly()
    {
        ExceptionMatcher matcher = throwsException(FileNotFoundException.class);
        assertThat(matcher.getExpectedException(), is(equalTo(FileNotFoundException.class)));
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

}
