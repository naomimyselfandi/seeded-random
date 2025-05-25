# SeededRandom

[![Maven Central](https://img.shields.io/maven-central/v/io.github.naomimyselfandi/seeded-random.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.naomimyselfandi/seeded-random)
[![GitHub](https://img.shields.io/badge/source-GitHub-blue?logo=github)](https://github.com/naomimyselfandi/seeded-random)

A lightweight utility for reproducible randomness in tests.
Built on top of JUnit 5 and Java’s `Random`, `SeededRandom` provides a test
extension and helper class to make deterministic test runs easy and expressive.

## Why use SeededRandom?

Randomness has some major advantages in testing. Running randomized tests is an
effective way to emulate realistic scenarios, which increases confidence that
the code is correct. Randomness also serves a documentation function. If a test
variable is initialized to a hardcoded value, it may be unclear whether that
value is an arbitrary placeholder or has actual meaning within the test. On the
other hand, randomness has the major downside of making tests impossible to
reproduce - *Did the test pass because I fixed the bug or because of chance?*

Seeding the random number generator provides the best of both worlds, and this
library provides a straightforward, declarative, ergonomic way to do so.

## Features

- Reproducible random values per test method and repetition
- Support for ordinary, parameterized, and repeated tests
- Helper methods like `nextUUID()`, `pick(T...)`, and `shuffle(T...)`
- - Extension mechanism to add methods for domain-specific types
- Safe and minimal with no runtime dependencies
- Compatible with Java 8 or later (tested up to Java 23)

## Usage

Add the following dependency:

```xml
<dependency>
    <groupId>io.github.naomimyselfandi</groupId>
    <artifactId>seeded-random</artifactId>
    <version>1.0.1</version>
    <scope>test</scope>
</dependency>
```

Then annotate your test case:

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

Alternatively, JUnit 5 can inject properties into lifecycle callbacks like
`@BeforeEach`, which can be even clearer:

```java
@ExtendWith(SeededRandomExtension.class)
class Point2dTest {

    private int x, y;
    private Point2d fixture;

    @BeforeEach
    void setup(SeededRandom random) {
        x = random.nextInt();
        y = random.nextInt();
        fixture = new Point2d(x, y);
        // fixture is initialized differently for each repetition of each test,
        // but deterministically. Test behavior is entirely reproducible even
        // though our initialization logic uses randomness.
    }

    @RepeatedTest(5)
    void distanceFromOrigin() {
        assertEquals(x + y, fixture.distanceFromOrigin());
    }
    
    @RepeatedTest(5)
    void distance(SeededRandom random) {
        int x2 = random.nextInt();
        int y2 = random.nextInt();
        // This receives the *same instance* as setup(), so x != x2 && y != y2.
        var dx = Math.abs(x - x2);
        var dy = Math.abs(y - y2);
        assertEquals(dx + dy, fixture.distance(new Point2d(x2, y2)));
    }

    @RepeatedTest(5)
    void testToString() {
        assertEquals("(" + x + ", " + y + ")", fixture.toString());
    }

}
```

If you want to generate random instances of your own types, simply extend `SeededRandom` and inject your subclass into your test methods.
