![junit-utils logo](resources/junit-utils_logo.svg)

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/oswaldobapvicjr/junit-utils/Java%20CI%20with%20Maven)](https://github.com/oswaldobapvicjr/junit-utils/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/codecov/c/github/oswaldobapvicjr/junit-utils)](https://codecov.io/gh/oswaldobapvicjr/junit-utils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils)
[![Javadoc](https://javadoc.io/badge2/net.obvj/junit-utils/javadoc.svg)](https://javadoc.io/doc/net.obvj/junit-utils)

Common utilities for working with JUnit:

- assertion of **exceptions**, as well as exception details, such as message and cause
- assertion of strings contents
- testing that a class **cannot be instantiated**

----

## Examples

> **Note:** Consider the following static import declarations for readability:

```java
import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
```

### Asserting exceptions

The following assertion is true if the examined method throws a NullPointerException:

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

The following assertion is particularly useful for utility classes:

```java
assertThat(TestUtils.class, instantiationNotAllowed());
```

 A matching class shall have all constructors declared as private and throw an exception inside the default constructor.

### Testing the contents of a string

The following examples represent some successful assertions using the Advanced String matcher:

```java
assertThat("The quick brown fox jumps over the lazy dog", containsAll("fox", "dog"));
assertThat("The quick brown fox jumps over the lazy dog", containsAny("FOX", "dragon").ignoreCase());
assertThat("The quick brown fox jumps over the lazy dog", containsNone("centaur"));
```

### Testing numbers

Sometimes, it's more meaningful to check whether a number is positive or negative than testing the value itself, especially in situations where the exact value is unpredictable:

```java
assertThat(stopwatch.elapsedTime(),           isPositive());
assertThat(duration.compareTo(otherDuration), isNegative());
```

----

## How to include it

If you are using Maven, add **junit-utils** as a dependency to your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>junit-utils</artifactId>
    <version>1.3.0</version>
</dependency>
```

If you use other dependency management systems (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils).
