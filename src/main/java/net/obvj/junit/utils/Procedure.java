/*
 * Copyright 2023 obvj.net
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
 * A functional interface that can be used to implement any generic block of code that
 * potentially throws an exception.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.5.0
 */
public interface Procedure
{
    /**
     * Executes this procedure.
     *
     * @throws Throwable although catching {@code Throwable} is typically discouraged, this
     *                   allows that checked exceptions can also be passed via lambda
     *                   expressions or method references
     */
    void execute() throws Throwable;
}
