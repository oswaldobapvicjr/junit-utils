package net.obvj.junit.utils.matchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * A Matcher that checks that instantiation is not allowed for a given class, which is
 * particularly useful for utility classes, for example.
 * <p>
 * First, the matcher verifies that all declared constructors are private, then it tries
 * to produce a new instance using the default constructor, via Reflection.
 * <p>
 * A <b>matching class</b> should have all constructors declared as {@code private} and
 * throw an exception, so the class will never be instantiated.
 * <p>
 * For example:
 *
 * <pre>
 * private MyClass()
 * {
 *     throw new UnsupportedOperationException("Instantiation not allowed");
 * }
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
public class InstantiationNotAllowedMatcher extends TypeSafeDiagnosingMatcher<Class<?>>
{
    /**
     * Creates a matcher that matches if the examined class cannot be instantiated, which is
     * particularly useful for utility classes.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat(TestUtils.class, instanceNotAllowed())
     * </pre>
     *
     * @return the matcher
     */
    @Factory
    public static Matcher<Class<?>> instantiationNotAllowed()
    {
        return new InstantiationNotAllowedMatcher();
    }

    /**
     * Execute the matcher business logic.
     *
     * @param clazz    the class to be checked
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */
    @Override
    protected boolean matchesSafely(Class<?> clazz, Description mismatch)
    {
        // First, check that all constructors are private
        for (Constructor<?> constructor : clazz.getDeclaredConstructors())
        {
            if (!Modifier.isPrivate(constructor.getModifiers()))
            {
                mismatch.appendText("the constructor \"" + constructor + "\" is not private");
                return false;
            }
        }

        // Then, try to create an instance using the default constructor
        return checkInstantiationNotAllowed(clazz, mismatch);
    }

    /**
     * Checks that no instances of the given class are created.
     *
     * @param clazz    the class to be checked
     * @param mismatch the description to be used for reporting in case of mismatch
     * @return a flag indicating whether or not the matching has succeeded
     */

    private boolean checkInstantiationNotAllowed(Class<?> clazz, Description mismatch)
    {
        try
        {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();

            mismatch.appendText("instantiation via Reflection was allowed by the constructor \"" + constructor + "\"");
            return false;
        }
        catch (ReflectiveOperationException exception)
        {
            // the constructor fails to create a new instance
            return true;
        }
    }

    /**
     * Describes the "expected" pat of the test description.
     *
     * @see org.hamcrest.SelfDescribing#describeTo(Description)
     */
    @Override
    public void describeTo(Description description)
    {
        description.appendText("a class which cannot be instantiated");
    }

}
