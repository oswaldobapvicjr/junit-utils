package net.obvj.junit.utils.matchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.hamcrest.*;

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
    private ExceptionMatcher exceptionMatcher;

    /**
     * Creates a matcher that matches if the examined class cannot be instantiated, which is
     * particularly useful for utility classes.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat(TestUtils.class, instantiationNotAllowed());
     * </pre>
     *
     * @return the matcher
     */
    @Factory
    public static InstantiationNotAllowedMatcher instantiationNotAllowed()
    {
        return new InstantiationNotAllowedMatcher();
    }

    /**
     * Assigns an expected Exception <b>(optional step)</b>.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .throwing(IllegalStateException.class));
     * }
     * </pre>
     *
     * The matcher matches if the actual exception class is either the same as, or is a child
     * of, the Exception represented by the specified parameter.
     * <p>
     * For example, if the constructor throws a {@code NullPointerException}, all of the
     * following assertions are valid:
     *
     * <pre>
     * {@code
     * throwing(IllegalStateException.class);
     * throwing(RuntimeException.class);
     * throwing(Exception.class);
     * }
     * </pre>
     *
     * <p>
     * In other words, the matcher tests whether the actual exception can be converted to the
     * specified class.
     * </p>
     *
     * @param exception the expected exception class; must not be null
     * @return the matcher, incremented with an expected exception
     * @throws NullPointerException if the specified class is null
     *
     * @since 1.2.1
     */
    public InstantiationNotAllowedMatcher throwing(Class<? extends Exception> exception)
    {
        Objects.requireNonNull(exception, "the expected exception must not be null");
        exceptionMatcher = ExceptionMatcher.throwsException(exception);
        return this;
    }

    /**
     * Assigns an expected message for Exception validation <b>(optional)</b>.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .throwing(IllegalStateException.class)
     *             .withMessage("instantiation not allowed"));
     * }
     * </pre>
     *
     * The following example is also valid for a test where <b>any</b> Exception is
     * acceptable, provided that the message matches:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .withMessage("instantiation not allowed"));
     * }
     * </pre>
     *
     * @param message the message for exception validation
     * @return the matcher, incremented with the specified message for testing
     *
     * @since 1.2.1
     */
    public InstantiationNotAllowedMatcher withMessage(String message)
    {
        return withMessage(CoreMatchers.equalTo(message));
    }

    /**
     * Assigns an external Matcher to be used in combination for the exception message
     * validation <b>(optional)</b>.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .throwing(IllegalStateException.class)
     *             .withMessage(containsString("not allowed")));
     * }
     * </pre>
     *
     * The following example is also valid for a test where <b>any</b> Exception is
     * acceptable, provided that the message matches:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .withMessage(endsWith("not allowed")));
     * }
     * </pre>
     *
     * @param matcher the matcher to be used in combination for exception message validation
     * @return the matcher, incremented with the specified matcher for exception testing
     *
     * @since 1.2.1
     */
    public InstantiationNotAllowedMatcher withMessage(Matcher<String> matcher)
    {
        if (exceptionMatcher == null)
        {
            exceptionMatcher = ExceptionMatcher.throwsException(Exception.class);
        }
        exceptionMatcher.withMessage(matcher);
        return this;
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

            if (exceptionMatcher != null)
            {
                return exceptionMatcher.validateFully(exception.getCause(), mismatch);
            }
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
        if (exceptionMatcher != null)
        {
            description.appendText(", throwing");
            exceptionMatcher.describeTo(description);
        }
    }

}
