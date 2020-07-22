package net.obvj.junit.utils;

import static org.junit.Assert.fail;

import java.util.function.Supplier;

import org.junit.Test;

/**
 * Unit tests for the {@link TestUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class TestUtilsTest
{
    private static final String THE_QUICK_BROWN_FOX = "The quick brown fox jumps over the lazy dog";
    private static final String[] ANIMALS = { "fox", "dog" };

    private static final String MESSAGE1 = "message1";
    private static final IllegalArgumentException ILLEGAL_ARGUMENT_EXCEPTION = new IllegalArgumentException(MESSAGE1,
            new NullPointerException());

    private static final Supplier<String> ILLEGAL_STATE_SUPPLIER = () ->
    {
        throw new IllegalStateException(MESSAGE1, new NullPointerException());
    };

    private static final Supplier<String> NO_EXCEPTION_SUPPLIER = () ->
    {
        return "ok";
    };

    private static final Runnable UNSUPPORTED_OPERATION_RUNNABLE = () ->
    {
        throw new UnsupportedOperationException(MESSAGE1, new IllegalArgumentException());
    };

    private static final Runnable NO_EXCEPTION_RUNNABLE = () ->
    {
    };

    @Test
    public void assertInstantiationNotAllowed_withClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class);
    }

    @Test
    public void assertInstantiationNotAllowed_withExpectedThrowableAndClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, UnsupportedOperationException.class);
    }

    @Test
    public void assertInstantiationNotAllowed_withAllParamsAndClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, UnsupportedOperationException.class, "Utility class");
    }

    @Test(expected = AssertionError.class)
    public void assertInstantiationNotAllowed_withUnexpectedMessageAndClassWithPrivateConstructor_fails()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, UnsupportedOperationException.class, "Invalid message");
    }

    @Test(expected = AssertionError.class)
    public void assertInstantiationNotAllowed_withUnexpectedThrowableAndClassWithPrivateConstructor_fails()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, NullPointerException.class, "Utility class");
    }

    @Test
    public void assertInstantiationNotAllowed_classWithPublicConstructor_fails()
    {
        TestUtils.assertException(AssertionError.class, null, null, () ->
        {
            try
            {
                TestUtils.assertInstantiationNotAllowed(String.class, UnsupportedOperationException.class);
            }
            catch (ReflectiveOperationException e)
            {
                fail("Got " + e.getClass().getName());
                e.printStackTrace();
            }
        });
    }

    @Test
    public void assertInstantiationNotAllowed_classWithPrivateConstructor_fails()
    {
        TestUtils.assertException(AssertionError.class, null, null, () ->
        {
            try
            {
                TestUtils.assertInstantiationNotAllowed(Runtime.class, UnsupportedOperationException.class);
            }
            catch (ReflectiveOperationException e)
            {
                fail("Got " + e.getClass().getName());
                e.printStackTrace();
            }
        });
    }

    @Test
    public void assertException_throwableAndExpectedClass()
    {
        TestUtils.assertException(IllegalArgumentException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void assertException_supplierAndExpectedClass()
    {
        TestUtils.assertException(IllegalStateException.class, ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    public void assertException_runnableAndExpectedClass()
    {
        TestUtils.assertException(UnsupportedOperationException.class, UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    public void assertException_throwableAndExpectedClassAndMessage()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void assertException_runnableAndExpectedClassAndMessage()
    {
        TestUtils.assertException(UnsupportedOperationException.class, MESSAGE1, UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    public void assertException_supplierAndExpectedClassAndMessage()
    {
        TestUtils.assertException(IllegalStateException.class, MESSAGE1, ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    public void assertException_throwableAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void assertException_supplierAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(IllegalStateException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    public void assertException_runnableAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(UnsupportedOperationException.class, MESSAGE1, IllegalArgumentException.class,
                UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    public void assertException_supplierAndExpectedClassButExceptionNotThrown_fails()
    {
        TestUtils.assertException(AssertionError.class,
                String.format(TestUtils.EXPECTED_BUT_NOT_THROWN, UnsupportedOperationException.class.getName()),
                () -> TestUtils.assertException(UnsupportedOperationException.class, NO_EXCEPTION_SUPPLIER));
    }

    @Test
    public void assertException_runnableAndExpectedClassButExceptionNotThrown_fails()
    {
        TestUtils.assertException(AssertionError.class,
                String.format(TestUtils.EXPECTED_BUT_NOT_THROWN, UnsupportedOperationException.class.getName()),
                () -> TestUtils.assertException(UnsupportedOperationException.class, NO_EXCEPTION_RUNNABLE));
    }

    @Test(expected = AssertionError.class)
    public void assertException_invalidThrowable_fails()
    {
        TestUtils.assertException(IllegalStateException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void assertException_invalidMessage_fails()
    {
        TestUtils.assertException(IllegalArgumentException.class, "message2", ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void assertException_invalidCause_fails()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, IllegalStateException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void assertStringContains_allStringsFound_suceeds()
    {
        TestUtils.assertStringContains(THE_QUICK_BROWN_FOX, ANIMALS);
    }

    @Test(expected = AssertionError.class)
    public void assertStringContains_oneStringNotFound_fails()
    {
        TestUtils.assertStringContains(THE_QUICK_BROWN_FOX, "fox", "cat");
    }

    @Test
    public void assertStringDoesNotContain_noStringsFound_suceeds()
    {
        TestUtils.assertStringDoesNotContain(THE_QUICK_BROWN_FOX, "sphinx", "quartz");
    }

    @Test(expected = AssertionError.class)
    public void assertStringDoesNotContain_oneStringFound_fails()
    {
        TestUtils.assertStringDoesNotContain(THE_QUICK_BROWN_FOX, "sphinx", "brown");
    }

}
