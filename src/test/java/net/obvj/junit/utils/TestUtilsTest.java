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

package net.obvj.junit.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TestUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
class TestUtilsTest
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
    void assertInstantiationNotAllowed_withClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class);
    }

    @Test
    void assertInstantiationNotAllowed_withExpectedThrowableAndClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, UnsupportedOperationException.class);
    }

    @Test
    void assertInstantiationNotAllowed_withAllParamsAndClassWithPrivateConstructor_suceeds()
            throws ReflectiveOperationException
    {
        TestUtils.assertInstantiationNotAllowed(TestUtils.class, UnsupportedOperationException.class, "Utility class");
    }

    @Test
    void assertInstantiationNotAllowed_withUnexpectedMessageAndClassWithPrivateConstructor_fails()
            throws ReflectiveOperationException
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertInstantiationNotAllowed(TestUtils.class,
                        UnsupportedOperationException.class, "Invalid message"));
    }

    @Test
    void assertInstantiationNotAllowed_withUnexpectedThrowableAndClassWithPrivateConstructor_fails()
            throws ReflectiveOperationException
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertInstantiationNotAllowed(TestUtils.class,
                        NullPointerException.class, "Utility class"));
    }

    @Test
    void assertInstantiationNotAllowed_classWithPublicConstructor_fails()
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
    void assertInstantiationNotAllowed_classWithPrivateConstructor_fails()
    {
        TestUtils.assertException(AssertionError.class, null, null, () ->
        {
            try
            {
                TestUtils.assertInstantiationNotAllowed(ClassWithPrivateConstructor.class);
            }
            catch (ReflectiveOperationException e)
            {
                fail("Got " + e.getClass().getName());
                e.printStackTrace();
            }
        });
    }

    static class ClassWithPrivateConstructor
    {
        private ClassWithPrivateConstructor()
        {
            // Empty on purpose
        }
    }

    @Test
    void assertException_throwableAndExpectedClass()
    {
        TestUtils.assertException(IllegalArgumentException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    void assertException_supplierAndExpectedClass()
    {
        TestUtils.assertException(IllegalStateException.class, ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    void assertException_runnableAndExpectedClass()
    {
        TestUtils.assertException(UnsupportedOperationException.class, UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    void assertException_throwableAndExpectedClassAndMessage()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    void assertException_runnableAndExpectedClassAndMessage()
    {
        TestUtils.assertException(UnsupportedOperationException.class, MESSAGE1, UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    void assertException_supplierAndExpectedClassAndMessage()
    {
        TestUtils.assertException(IllegalStateException.class, MESSAGE1, ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    void assertException_throwableAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    void assertException_supplierAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(IllegalStateException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_STATE_SUPPLIER);
    }

    @Test
    void assertException_runnableAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(UnsupportedOperationException.class, MESSAGE1, IllegalArgumentException.class,
                UNSUPPORTED_OPERATION_RUNNABLE);
    }

    @Test
    void assertException_supplierAndExpectedClassButExceptionNotThrown_fails()
    {
        TestUtils.assertException(AssertionError.class,
                String.format(TestUtils.EXPECTED_BUT_NOT_THROWN, UnsupportedOperationException.class.getName()),
                () -> TestUtils.assertException(UnsupportedOperationException.class, NO_EXCEPTION_SUPPLIER));
    }

    @Test
    void assertException_runnableAndExpectedClassButExceptionNotThrown_fails()
    {
        TestUtils.assertException(AssertionError.class,
                String.format(TestUtils.EXPECTED_BUT_NOT_THROWN, UnsupportedOperationException.class.getName()),
                () -> TestUtils.assertException(UnsupportedOperationException.class, NO_EXCEPTION_RUNNABLE));
    }

    @Test
    void assertException_invalidThrowable_fails()
    {
        assertThrows(AssertionError.class, () -> TestUtils
                .assertException(IllegalStateException.class, ILLEGAL_ARGUMENT_EXCEPTION));
    }

    @Test
    void assertException_invalidMessage_fails()
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertException(IllegalArgumentException.class, "message2",
                        ILLEGAL_ARGUMENT_EXCEPTION));
    }

    @Test
    void assertException_invalidCause_fails()
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertException(IllegalArgumentException.class, MESSAGE1,
                        IllegalStateException.class, ILLEGAL_ARGUMENT_EXCEPTION));
    }

    @Test
    void assertStringContains_allStringsFound_suceeds()
    {
        TestUtils.assertStringContains(THE_QUICK_BROWN_FOX, ANIMALS);
    }

    @Test
    void assertStringContains_oneStringNotFound_fails()
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertStringContains(THE_QUICK_BROWN_FOX, "fox", "cat"));
    }

    @Test
    void assertStringDoesNotContain_noStringsFound_suceeds()
    {
        TestUtils.assertStringDoesNotContain(THE_QUICK_BROWN_FOX, "sphinx", "quartz");
    }

    @Test
    void assertStringDoesNotContain_oneStringFound_fails()
    {
        assertThrows(AssertionError.class,
                () -> TestUtils.assertStringDoesNotContain(THE_QUICK_BROWN_FOX, "sphinx", "brown"));
    }

    @Test
    void assertPositiveNumber_positiveNumber_success()
    {
        TestUtils.assertPositiveNumber(999);
    }

    @Test
    void assertPositiveNumber_negativeNumber_success()
    {
        assertThrows(AssertionError.class, () -> TestUtils.assertPositiveNumber(-999));
    }

    @Test
    void assertNegativeNumber_negativeNumber_success()
    {
        TestUtils.assertNegativeNumber(-999);
    }

    @Test
    void assertNegativeNumber_positiveNumber_success()
    {
        assertThrows(AssertionError.class, () -> TestUtils.assertNegativeNumber(999));
    }

}
