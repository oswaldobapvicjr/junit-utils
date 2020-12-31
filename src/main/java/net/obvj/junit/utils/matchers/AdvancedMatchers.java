package net.obvj.junit.utils.matchers;

/**
 * A class that groups all Matchers provided by junit-utils, allowing developers
 * developers to have access to all facilities with a single static import declaration:
 *
 * <pre>
 * import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
 * </pre>
 *
 * @author oswaldo.bapvic.jr
 * @since 1.2.1
 */
public class AdvancedMatchers
{
    private AdvancedMatchers()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    /**
     * Creates a matcher that matches if the examined procedure throws a given exception.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff("p1"),
     *         throwsException(IllegalStateException.class));
     * }
     * </pre>
     *
     * The matcher matches if the actual exception class is either the same as, or is a child
     * of, the Exception represented by the specified parameter.
     * <p>
     * For example, if the examined code throws a {@code NullPointerException}, all of the
     * following assertions are valid:
     *
     * <pre>
     * {@code
     * assertThat( ... throwsException(NullPointerException.class));
     * assertThat( ... throwsException(RuntimeException.class));
     * assertThat( ... throwsException(Exception.class));
     * }
     * </pre>
     *
     * In other words, the matcher tests whether the actual exception can be converted to the
     * specified class.
     * <p>
     * If applicable, the matcher can also be incremented to validate the exception message
     * and cause.
     * <p>
     * For example:
     *
     * <pre>
     * {@code
     * assertThat(() -> obj.doStuff(""),
     *         throwsException(IllegalArgumentException.class)
     *             .withMessage("The argument must not be empty"));
     * }
     * </pre>
     *
     * @param exception the expected exception class. A null value is allowed, and means that
     *                  no exception is expected
     * @return the matcher
     */
    public static ExceptionMatcher throwsException(Class<? extends Exception> exception)
    {
        return ExceptionMatcher.throwsException(exception);
    }

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
     * <p>
     * First, the matcher verifies that all declared constructors are private, then it tries
     * to create a new instance using the default constructor.
     * <p>
     * A matching class shall have all constructors declared as {@code private} and throw an
     * exception inside the constructor, so the class will never be instantiated.
     * <p>
     * For example:
     *
     * <pre>
     * private MyClass()
     * {
     *     throw new IllegalStateException("Instantiation not allowed");
     * }
     * </pre>
     *
     * If applicable, the matcher can also be incremented to validate the exception thrown by
     * the constructor:
     *
     * <pre>
     * {@code
     * assertThat(TestUtils.class, instantiationNotAllowed()
     *         .throwing(IllegalStateException.class)
     *             .withMessage("Instantiation not allowed"));
     * }
     * </pre>
     *
     * @return the matcher
     */
    public static InstantiationNotAllowedMatcher instantiationNotAllowed()
    {
        return InstantiationNotAllowedMatcher.instantiationNotAllowed();
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>all</b> of the
     * specified substrings (regardless of the order they appear in the string).
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAll("fox", "the"))
     * </pre>
     *
     * By default, the matcher is <b>case-sensitive</b>, but this behavior can be modified
     * with an additional method call.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAll("FOX", "The").ignoreCase())
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsAll(String... substrings)
    {
        return StringMatcher.containsAll(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>any</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("fox", "quick"))
     * </pre>
     *
     * By default, the matcher is <b>case-sensitive</b>, but this behavior can be modified
     * with an additional method call.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsAny("FOX", "QuIcK").ignoreCase())
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsAny(String... substrings)
    {
        return StringMatcher.containsAny(substrings);
    }

    /**
     * Creates a matcher that matches if the examined string contains <b>none</b> of the
     * specified substrings.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat("the quick brown fox", containsNone("cat", "mouse"))
     * </pre>
     *
     * @param substrings the substrings to be tested
     * @return the matcher
     */
    public static StringMatcher containsNone(String... substrings)
    {
        return StringMatcher.containsNone(substrings);
    }

}
