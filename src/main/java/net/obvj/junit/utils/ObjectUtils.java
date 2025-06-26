/*
 * Copyright 2025 obvj.net
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

/**
 * Utility methods for working with objects.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.10.0
 */
public class ObjectUtils
{

    private ObjectUtils()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Returns the identity string of the specified object, mimicking the default
     * {@code Object.toString()} format: {@code ClassName@HashCode}.
     * <p>
     * This is particularly useful when the object's {@code toString()} method has been
     * overridden and the default identity information is still needed for debugging or
     * logging.
     *
     * @param object the object whose identity string is to be returned
     * @return a string in the format {@code ClassName@HashCode}, or {@code "null"} if the
     *         object is {@code null}
     */
    public static String toIdentityString(Object object)
    {
        return toIdentityString(object, "null");
    }

    /**
     * Returns the identity string of the specified object, mimicking the default
     * {@code Object.toString()} format: {@code ClassName@HashCode}.
     * <p>
     * This is particularly useful when the object's {@code toString()} method has been
     * overridden and the default identity information is still needed for debugging or
     * logging.
     *
     * @param object        the object whose identity string is to be returned
     * @param defaultString the default string to be returned if the object is null
     * @return a string in the format {@code ClassName@HashCode}, or {@code defaultString} if
     *         the object is {@code null}
     */
    public static String toIdentityString(Object object, String defaultString)
    {
        return object == null ? defaultString
                : object.getClass().getName() + "@"
                        + Integer.toHexString(System.identityHashCode(object));
    }
}
