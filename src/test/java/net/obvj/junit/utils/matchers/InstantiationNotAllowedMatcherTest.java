package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;

/**
 * Unit tests for the {@link InstantiationNotAllowedMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class InstantiationNotAllowedMatcherTest
{

    @Test
    public void instantiationNotAllowed_classWithPrivateConstructorAndInstantiationNotAllowed()
    {
        assertThat(TestUtils.class, instantiationNotAllowed());
    }

    @Test(expected = AssertionError.class)
    public void instantiationNotAllowed_classWithPublicConstructor()
    {
        assertThat(String.class, instantiationNotAllowed());
    }

    @Test(expected = AssertionError.class)
    public void instantiationNotAllowed_classWithPrivateConstructorButInstantiationAllowed()
    {
        assertThat(Runtime.class, instantiationNotAllowed());
    }

}
