package io.github.naomimyselfandi.seededrandom;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Testing a test extension is kind of weird. We're using JUnit's test machinery
// to test how our extension interacts with that test machinery, not because we
// actually want to use it. Don't let that throw you off - the actual logic is
// quite simple.

@ExtendWith(SeededRandomExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SeededRandomExtensionTest {

    private final List<SeededRandom> previouslyGenerated = new ArrayList<>();

    private SeededRandom fromSetupMethod;

    @BeforeEach
    void setup(SeededRandom random) {
        fromSetupMethod = random;
    }

    @RepeatedTest(4)
    void test(SeededRandom random, SeededRandom randomWithDifferentIndex, RepetitionInfo repetitionInfo) {
        assertSame(fromSetupMethod, random);
        assertNotSame(fromSetupMethod, randomWithDifferentIndex);
        for (SeededRandom previous : previouslyGenerated) {
            assertNotSame(previous, random);
            assertNotEquals(previous.getInitialSeed(), random.getInitialSeed());
        }
        previouslyGenerated.add(random);
        // If this fails, our test is invalid.
        assertEquals(repetitionInfo.getCurrentRepetition(), previouslyGenerated.size());
    }

    @Test
    void test_CanExtendSeededRandom(TestSeededRandom random) {
        assertNotNull(random);
        assertNotSame(fromSetupMethod, random);
    }

    @Test
    @SuppressWarnings("CatchMayIgnoreException")
    void test_ReportsConstructionErrors() {
        try {
            Object ignored = SeededRandomExtension.doConstruction(void.class, 0L);
            fail("should have thrown");
        } catch (RuntimeException e) {
            assertInstanceOf(NoSuchMethodException.class, e.getCause());
        }
    }

}
