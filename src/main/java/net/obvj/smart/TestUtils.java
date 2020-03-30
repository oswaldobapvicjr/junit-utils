package net.obvj.smart;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Common utilities for working with unit tests.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TestUtils
{
    protected static final String EXPECTED_BUT_NOT_THROWN = "Expected but not thrown: \"%s\"";
    private static final String EXPECTED_STRING_NOT_FOUND = "Expected string \"%s\" not found in: \"%s\"";
    private static final String UNEXPECTED_STRING_FOUND = "Unexpected string \"%s\" found in: \"%s\"";

    private TestUtils()
    {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Tests that no instances of an utility class are created.
     *
     * @param targetClass the class to be tested
     * @throws AssertionError               if the target class is instantiated
     * @throws ReflectiveOperationException in case of errors getting constructor metadata or
     *                                      instantiating the private constructor
     */
    public static void assertNoInstancesAllowed(Class<?> targetClass) throws ReflectiveOperationException
    {
        assertNoInstancesAllowed(targetClass, null);
    }

    /**
     * Tests that no instances of an utility class are created.
     *
     * @param targetClass            the class to be tested
     * @param expectedThrowableClass the expected throwable to be checked in case the
     *                               constructor is called
     * @throws AssertionError               if the target class is instantiated or a different
     *                                      throwable class is received
     * @throws ReflectiveOperationException in case of errors getting constructor metadata or
     *                                      instantiating the private constructor
     */
    public static void assertNoInstancesAllowed(Class<?> targetClass, Class<? extends Throwable> expectedThrowableClass)
            throws ReflectiveOperationException
    {
        assertNoInstancesAllowed(targetClass, expectedThrowableClass, null);
    }

    /**
     * Tests that no instances of an utility class are created.
     *
     * @param targetClass            the class to be tested
     * @param expectedThrowableClass the expected throwable to be checked in case the
     *                               constructor is called
     * @param expectedErrorMessage   the expected error message to be checked in case the
     *                               constructor is called
     * @throws AssertionError               if the target class is instantiated or a different
     *                                      throwable class is received
     * @throws ReflectiveOperationException in case of errors getting constructor metadata or
     *                                      instantiating the private constructor
     */
    public static void assertNoInstancesAllowed(Class<?> targetClass, Class<? extends Throwable> expectedThrowableClass,
            String expectedErrorMessage) throws ReflectiveOperationException
    {
        try
        {
            Constructor<?> constructor = targetClass.getDeclaredConstructor();
            assertTrue("Constructor should be private", Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
            throw new AssertionError("Class was instantiated");
        }
        catch (InvocationTargetException ite)
        {
            Throwable cause = ite.getCause();

            if (expectedThrowableClass != null)
            {
                assertThat(cause, is(instanceOf(expectedThrowableClass)));
            }
            if (expectedErrorMessage != null)
            {
                assertThat(cause.getMessage(), is(expectedErrorMessage));
            }
        }
    }

    /**
     * A utility method to assert that a given throwable matches the expected class.
     *
     * @param expectedThrowable the expected throwable class
     * @param throwable         the throwable to be validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, Throwable throwable)
    {
        assertException(expectedThrowable, null, null, throwable);
    }

    /**
     * A utility method to assert that a given throwable matches the expected class and
     * message.
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param throwable         the throwable to be validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Throwable throwable)
    {
        assertException(expectedThrowable, expectedMessage, null, throwable);
    }

    /**
     * A utility method to assert that a given throwable matches the expected class, cause and
     * message.
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param throwable         the throwable to be validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Throwable throwable)
    {
        assertThat("Unexpected throwable class:", throwable, is(instanceOf(expectedThrowable)));

        if (expectedMessage != null)
        {
            assertThat("Unexpected message:", throwable.getMessage(), is(expectedMessage));
        }
        if (expectedCause != null)
        {
            assertThat("Unexpected cause:", throwable.getCause(), is(instanceOf(expectedCause)));
        }
    }

    /**
     * A utility method to assert the expected exception thrown by a supplying function.
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(AgentConfigurationException.class, "Invalid agents file",
     *           () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"))
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param supplier          the supplying function that throws an exception to be
     *                          validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, Supplier<?> supplier)
    {
        assertException(expectedThrowable, null, null, supplier);
    }

    /**
     * A utility method to assert the expected exception and message thrown by a supplying
     * function.
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(AgentConfigurationException.class, "Invalid agents file",
     *           () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"))
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param supplier          the supplying function that throws an exception to be
     *                          validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Supplier<?> supplier)
    {
        assertException(expectedThrowable, expectedMessage, null, supplier);
    }

    /**
     * A utility method to assert the expected exception, message and cause thrown by a
     * supplying function.
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
     *           () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"))
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param supplier          the supplying function that throws an exception to be
     *                          validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Supplier<?> supplier)
    {
        try
        {
            supplier.get();
        }
        catch (Throwable throwable)
        {
            assertException(expectedThrowable, expectedMessage, expectedCause, throwable);
            return;
        }
        throw new AssertionError(String.format(EXPECTED_BUT_NOT_THROWN, expectedThrowable.getCanonicalName()));
    }

    /**
     * A utility method to assert the expected exception thrown by a given procedure, that is,
     * a function that accepts no arguments and returns void (e.g., a Runnable's {@code run()}
     * method).
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(IllegalStateException.class, () -> agent.start())
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param runnable          the runnable that produces an exception to be * validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, Runnable runnable)
    {
        assertException(expectedThrowable, null, null, runnable);
    }

    /**
     * A utility method to assert the expected exception and message thrown by a given
     * procedure, that is, a function that accepts no arguments and returns void (e.g., a
     * Runnable's {@code run()} method).
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(IllegalStateException.class, "Agent already started",
     *           () -> agent.start())
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param runnable          the runnable that produces an exception to be * validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Runnable runnable)
    {
        assertException(expectedThrowable, expectedMessage, null, runnable);
    }

    /**
     * A utility method to assert the expected exception, message and cause thrown by a given
     * procedure, that is, a function that accepts no arguments and returns void (e.g., a
     * Runnable's {@code run()} method).
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(AgentConfigurationException.class, "Agent already started", IllegalStateException.class,
     *           () -> agent.start())
     * </code>
     *
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param runnable          the runnable that produces an exception to be * validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Runnable runnable)
    {
        try
        {
            runnable.run();
        }
        catch (Throwable throwable)
        {
            assertException(expectedThrowable, expectedMessage, expectedCause, throwable);
            return;
        }
        throw new AssertionError(String.format(EXPECTED_BUT_NOT_THROWN, expectedThrowable.getCanonicalName()));
    }

    /**
     * Tests that the given test string contains all of the expected strings.
     *
     * @param testString      the string to be tested
     * @param expectedStrings the strings to be expected inside the {@code testString}
     */
    public static void assertStringContains(String testString, String... expectedStrings)
    {
        Arrays.stream(expectedStrings)
                .forEach(expectedString -> assertTrue(
                        String.format(EXPECTED_STRING_NOT_FOUND, expectedString, testString),
                        testString.contains(expectedString)));
    }

    /**
     * Tests that the given test string does not contain any of the expected strings.
     *
     * @param testString      the string to be tested
     * @param expectedStrings the strings not to be expected inside the {@code testString}
     */
    public static void assertStringDoesNotContain(String testString, String... expectedStrings)
    {
        Arrays.stream(expectedStrings)
                .forEach(expectedString -> assertFalse(
                        String.format(UNEXPECTED_STRING_FOUND, expectedString, testString),
                        testString.contains(expectedString)));
    }
}
