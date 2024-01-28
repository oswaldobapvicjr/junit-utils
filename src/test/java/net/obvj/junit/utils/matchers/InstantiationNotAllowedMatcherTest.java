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

package net.obvj.junit.utils.matchers;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import net.obvj.junit.utils.TestUtils;

/**
 * Unit tests for the {@link InstantiationNotAllowedMatcher} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.1.0
 */
class InstantiationNotAllowedMatcherTest
{

    public static class MyClassPrivateConstructorOnly
    {

        private MyClassPrivateConstructorOnly()
        {
            // Do nothing
        }
    }

    @Test
    void instantiationNotAllowed_classWithPrivateConstructorAndThrowingException()
    {
        assertThat(TestUtils.class, instantiationNotAllowed());
    }

    @Test
    void instantiationNotAllowed_classWithPublicConstructor()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(String.class, instantiationNotAllowed()));
    }

    @Test
    void instantiationNotAllowed_classWithPrivateConstructorButInstantiationAllowed()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(MyClassPrivateConstructorOnly.class, instantiationNotAllowed()));
    }

    @Test
    void instantiationNotAllowed_throwingCorrectException_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class));
    }

    @Test
    void instantiationNotAllowed_throwingNotSameException_failure()
    {
        assertThrows(AssertionError.class, () -> assertThat(TestUtils.class,
                instantiationNotAllowed().throwing(IllegalArgumentException.class)));
    }

    @Test
    void instantiationNotAllowed_throwingNull_failure()
    {
        assertThrows(NullPointerException.class,
                () -> assertThat(TestUtils.class, instantiationNotAllowed().throwing(null)));
    }

    @Test
    void instantiationNotAllowed_throwingSameExceptionAndMessage_success()
    {
        assertThat(TestUtils.class,
                instantiationNotAllowed().throwing(UnsupportedOperationException.class).withMessage("Utility class"));
    }

    @Test
    void instantiationNotAllowed_throwingSameExceptionButNotSameMessage_failure()
    {
        assertThrows(AssertionError.class,
                () -> assertThat(TestUtils.class,
                        instantiationNotAllowed().throwing(UnsupportedOperationException.class)
                                .withMessage("invalid message")));
    }

    @Test
    void instantiationNotAllowed_withMessageOnlyAndSameMessage_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().withMessage("Utility class"));
    }

    @Test
    void instantiationNotAllowed_withMessageMatcherAndMessageMatches_success()
    {
        assertThat(TestUtils.class, instantiationNotAllowed().withMessage(CoreMatchers.startsWith("Utility")));
    }

}
