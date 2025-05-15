# SeededRandom

A lightweight utility for reproducible randomness in tests.
Built on top of JUnit 5 and Java’s `Random`, `SeededRandom` provides a test
extension and helper class to make deterministic test runs easy and expressive.

## Why use SeededRandom?

- Avoid flakiness in randomized tests
- Get stable behavior across test iterations
- Great for randomized property tests, data generation, and more

## Features

- Reproducible random values per test method and repetition
- Optional support for parameterized and repeated tests
- Helper methods like `nextUUID()`, `pick(T...)`, and `shuffle(T...)`
- Safe and minimal with no runtime dependencies
- Compatible with Java 8 or later

## Usage

Add the following dependency:

```xml
<dependency>
    <groupId>io.github.naomimyselfandi</groupId>
    <artifactId>seeded-random</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

Then annotated your test case:

```java
@ExtendWith(SeededRandomExtension.class)
class MyTest {

    @RepeatedTest(5)
    void testSomething(SeededRandom random) {
        int i = random.nextInt();
        UUID id = random.nextUUID();
        Weekday day = random.pick(Weekday.values());
        List<String> order = random.shuffle("a", "b", "c");
        // Test stuff!
    }

}
```

That's it!
