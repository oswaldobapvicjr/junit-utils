![junit-utils logo](resources/junit-utils_logo.svg)

[![Java 8+](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://openjdk.java.net/)
[![Known Vulnerabilities](https://snyk.io/test/github/oswaldobapvicjr/junit-utils/badge.svg)](https://snyk.io/test/github/oswaldobapvicjr/junit-utils)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/oswaldobapvicjr/junit-utils/maven.yml?branch=master&label=build)](https://github.com/oswaldobapvicjr/junit-utils/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/codecov/c/github/oswaldobapvicjr/junit-utils)](https://codecov.io/gh/oswaldobapvicjr/junit-utils)
[![Maven Central Version](https://img.shields.io/maven-central/v/net.obvj/junit-utils)](https://mvnrepository.com/artifact/net.obvj/junit-utils)
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

Including cause details:

```java
assertThat(() -> myObject.doStuff(null),
        throwsException(MyException.class).withMessageContaining("ERR-120008")
            .withCause(
                exception(NullPointerException.class)
                    .withMessage("stuff cannot be null")));
```

And more:

```java
assertThat(() -> httpClient.post(request),
        throwsException(HttpException.class)
            .with(HttpException::getStatusCode, equalTo(400));
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
assertThat("The quick brown fox jumps over the lazy dog", containsAll("dog", "fox"));
assertThat("The quick brown fox jumps over the lazy dog", containsAny("FOX", "dragon").ignoreCase());
assertThat("The quick brown fox jumps over the lazy dog", containsNone("centaur"));
assertThat("The quick brown fox jumps over the lazy dog", containsAllInSequence("fox", "dog"));
```

#### Matching against RegEx patterns

This is particularly useful when a string can legitimately take multiple formats and you only need at least one regular expression to match.

```java
assertThat("File file.json not found",         matchesAny("File .* not found"));
assertThat("HTTP-404: Not Found",              matchesAny("HTTP-\\d{3}.*", "test"));
assertThat("Transaction a1b2c3d4-e5f6 failed", matchesAny("Transaction \\w{8}-\\w{4} failed"));
```

Use `matchesAll` when a string must satisfy multiple independent constraints simultaneously (e.g., password rules, structured identifiers with both prefix and suffix patterns, or log entries containing multiple required formats):

```java
assertThat("The quick brown fox jumps over the lazy dog", matchesAll(".*fox.*", ".*dog$"));
assertThat("2026-11-22T21:35:00Z [ERROR] Failed to process request for userId=48291",
        matchesAll(".*\\[ERROR\\].*", ".*userId=\\d+.*"));
assertThat("BR-2026-AB-123456", matchesAll("^BR-\\d{4}-.*", ".*-[A-Z]{2}-\\d{6}$"));
```

Use `matchesNone` when a string must avoid every prohibited pattern, such as error signatures or disallowed formats:

```java
assertThat("2026-11-23T10:00:00Z [INFO] Request completed",
        matchesNone(".*\\[ERROR\\].*", ".*FATAL.*"));
assertThat("user_123", matchesNone("^admin_.*", ".*\\s+.*"));
```

All three matchers require each pattern to match the **entire** string, and all support `.ignoreCase()`.

### Testing numbers

Sometimes, it's more meaningful to check whether a number is positive or negative than testing the value itself, especially in situations where the exact value is unpredictable:

```java
assertThat(stopwatch.elapsedTime(),           isPositive());
assertThat(duration.compareTo(otherDuration), isNegative());
```

----

## Stargazers over time
[![Stargazers over time](https://starchart.cc/oswaldobapvicjr/junit-utils.svg?variant=adaptive)](https://starchart.cc/oswaldobapvicjr/junit-utils)

----

## How to include it

If you are using Maven, add **junit-utils** as a dependency in your pom.xml file:

```xml
<dependency>
    <groupId>net.obvj</groupId>
    <artifactId>junit-utils</artifactId>
    <version>1.10.0</version>
</dependency>
```

If you use other dependency management systems (such as Gradle, Grape, Ivy, etc.) click [here](https://maven-badges.herokuapp.com/maven-central/net.obvj/junit-utils).

----

## Contributing

If you want to contribute to the project, check the [issues](http://obvj.net/junit-utils/issues) page, or write an e-mail to [oswaldo@obvj.net](mailto:oswaldo@obvj.net).

Thanks to the people who have already contributed!

<a href="https://github.com/oswaldobapvicjr/junit-utils/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=oswaldobapvicjr/junit-utils"/>
</a>

<sup>Made with [contrib.rocks](https://contrib.rocks)</sup>

