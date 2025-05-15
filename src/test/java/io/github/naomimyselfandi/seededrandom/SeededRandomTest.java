package io.github.naomimyselfandi.seededrandom;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("SpellCheckingInspection")
class SeededRandomTest {

    @ParameterizedTest
    @CsvSource({
            "413,c400b47f-b749-fe68-a079-92c1952e98d2,eeac3783-e376-8493-cedd-28876abcbae2",
            "612,ad17c609-7aaa-1b9f-006f-ddbbe0508aab,a8d104e6-589d-c424-4e4a-4221bc57af12",
    })
    void nextUUID(int seed, UUID first, UUID second) {
        SeededRandom random = new SeededRandom(seed);
        assertEquals(first, random.nextUUID());
        assertEquals(second, random.nextUUID());
    }

    @ParameterizedTest
    @CsvSource({
            "413,a,e",
            "612,a,d",
    })
    void pick(int seed, String first, String second) {
        SeededRandom random = new SeededRandom(seed);
        assertEquals(first, random.pick("a", "b", "c", "d", "e", "f"));
        assertEquals(second, random.pick("a", "b", "c", "d", "e", "f"));
    }

    @ParameterizedTest
    @CsvSource({
            "413,c,b,a",
            "612,b,c,a",
    })
    void shuffle(int seed, String first, String second, String third) {
        SeededRandom random = new SeededRandom(seed);
        assertEquals(Arrays.asList(first, second, third), random.shuffle("a", "b", "c"));
    }

    @ParameterizedTest
    @CsvSource({
            "413,c,b,a",
            "612,b,c,a",
    })
    void iterator(int seed, String first, String second, String third) {
        SeededRandom random = new SeededRandom(seed);
        Iterator<String> iterator = random.iterator("a", "b", "c");
        assertEquals(first, iterator.next());
        assertEquals(second, iterator.next());
        assertEquals(third, iterator.next());
        assertFalse(iterator.hasNext());
    }

}
