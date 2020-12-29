package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
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
    public void instantiationNotAllowed_classWithPrivateConstructorAndThrowingException()
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

    @Test
    public void instantiationNotAllowed_throwingCorrectException_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class));
    }

    @Test(expected = AssertionError.class)
    public void instantiationNotAllowed_throwingNotSameException_failure()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().throwing(IllegalArgumentException.class));
    }

    @Test(expected = NullPointerException.class)
    public void instantiationNotAllowed_throwingNull_failure()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().throwing(null));
    }

    @Test
    public void instantiationNotAllowed_throwingSameExceptionAndMessage_success()
    {
        assertThat(TestUtils.class,
                instantiationNotAllowed().throwing(UnsupportedOperationException.class).withMessage("Utility class"));
    }

    @Test(expected = AssertionError.class)
    public void instantiationNotAllowed_throwingSameExceptionButNotSameMessage_failure()
    {
        assertThat(TestUtils.class,
                instantiationNotAllowed().throwing(UnsupportedOperationException.class).withMessage("invalid message"));
    }

    @Test
    public void instantiationNotAllowed_withMessageOnlyAndSameMessage_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().withMessage("Utility class"));
    }

    @Test
    public void instantiationNotAllowed_withMessageMatcherAndMessageMatches_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().withMessage(CoreMatchers.startsWith("Utility")));
    }

}
