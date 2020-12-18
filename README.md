![junit-utils logo](resources/junit-utils_logo.svg)

[![Build Status](https://travis-ci.org/oswaldobapvicjr/junit-utils.svg?branch=master)](https://travis-ci.org/oswaldobapvicjr/junit-utils)
[![Coverage Status](https://coveralls.io/repos/github/oswaldobapvicjr/junit-utils/badge.svg?branch=master)](https://coveralls.io/github/oswaldobapvicjr/junit-utils?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils)
[![Javadoc](https://javadoc.io/badge2/net.obvj/junit-utils/javadoc.svg)](https://javadoc.io/doc/net.obvj/junit-utils)

Common utilities for working with JUnit.

## Features

**junit-utils** provides convenient objects and methods for agile development of the following unit-testing scenarios:

- asserting **exceptions**, as well as exception details, such as message and cause
- assertion of strings contents
- testing that a class **cannot be instantiated**

----

## Examples

### Asserting exceptions

The following assertion is true if the examined method throws a NullPointerException. The **ExceptionMatcher** class is in use:

```java
assertThat(() -> myObject.doStuff(null),
        throwsException(NullPointerException.class));
```

To test the exception **message**, add `withMessageContaining` ...

```java
assertThat(() -> myObject.doStuff(null),
        throwsException(NullPointerException.class)
            .withMessageContaining("ERR-120008"));
```

... or combine a String matcher:

````java
assertThat(() -> agent.loadSchemaFile("bad-schema.xsd"),
        throwsException(AgentConfigurationException.class)
            .withMessage(
                either(startsWith("ERR-0001"))
                    .or(containsAny("invalid schema").ignoreCase())));
````

If required, you can also test the exception **cause**:

```java
assertThat(() -> myObject.doStuff(null),
        throwsException(MyException.class).withMessageContaining("ERR-120008")
            .withCause(NullPointerException.class));
```

### Testing that instantiation is not allowed

The following assertion is particularly useful for utility classes. The **InstantiationNotAllowedMatcher** class is in use:

```java
assertThat(TestUtils.class, instantiationNotAllowed());
```

### Testing the contents of a string

The following examples represent some successful assertions using the **StringMatcher** class:

```java
assertThat("The quick brown fox jumps over the lazy dog", containsAll("fox", "dog"));
assertThat("The quick brown fox jumps over the lazy dog", containsAny("FOX", "dragon").ignoreCase());
assertThat("The quick brown fox jumps over the lazy dog", containsNone("centaur"));
```


----

## How to include it

If you are using Maven, add **junit-utils** as a dependency to your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>junit-utils</artifactId>
    <version>1.2.0</version>
</dependency>
```

If you use other dependency management systems (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils).
