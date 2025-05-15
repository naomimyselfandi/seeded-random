package io.github.naomimyselfandi.seededrandom;

import java.util.*;

/**
 * A random number generator with some useful extensions for testing. Instances
 * of this class are always constructed with a fixed seed, ensuring that tests
 * are reproducible.
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;ExtendWith(SeededRandomExtension.class)
 * &#64;RepeatedTest(5)
 * void testSomething(SeededRandom random) {
 *     UUID id = random.nextUUID();
 *     Color color = random.next(Color.values());
 *     List&lt;String&gt; order = random.shuffle("alpha", "beta", "gamma");
 *     // ...
 * }
 * </pre>
 * @see SeededRandomExtension
 */
public class SeededRandom extends Random {

    private final long initialSeed;

    /**
     * Construct a new {@code SeededRandom} with a given seed.
     * @see SeededRandomExtension Instances of this class are usually provided
     * via a test extension instead.
     * @param initialSeed The initial seed.
     */
    public SeededRandom(long initialSeed) {
        super(initialSeed);
        this.initialSeed = initialSeed;
    }

    /**
     * Generate a {@code UUID} by generating two {@code long}s.
     * @return The generated {@code UUID}.
     */
    public UUID nextUUID() {
        return new UUID(nextLong(), nextLong());
    }

    /**
     * Select one of the given candidates at random.
     * @param candidates The candidates to pick from.
     * @return One of the candidates at random.
     * @param <T> The type of the candidates.
     * @throws IllegalArgumentException if no candidates are given.
     */
    public <T> T pick(Iterable<? extends T> candidates) {
        List<T> list = toList(candidates);
        return list.get(nextInt(list.size()));
    }

    /**
     * Select one of the given candidates at random.
     * @param candidates The candidates to pick from.
     * @return One of the candidates at random.
     * @param <T> The type of the candidates.
     * @throws IllegalArgumentException if no candidates are given.
     */
    @SafeVarargs
    public final <T> T pick(T... candidates) {
        return pick(Arrays.asList(candidates));
    }

    /**
     * Create a list out of some elements in random order. The input is not
     * modified.
     * @param elements The elements to shuffle.
     * @return A list containing those elements in a random order.
     * @param <T> The type of the elements.
     */
    public <T> List<T> shuffle(Iterable<? extends T> elements) {
        List<T> list = toList(elements);
        Collections.shuffle(list, this);
        return list;
    }

    /**
     * Create a list out of some elements in random order. The input is not
     * modified.
     * @param elements The elements to shuffle.
     * @return A list containing those elements in a random order.
     * @param <T> The type of the elements.
     */
    @SafeVarargs
    public final <T> List<T> shuffle(T... elements) {
        return shuffle(Arrays.asList(elements));
    }

    /**
     * Create an iterator that returns the given elements in a random order.
     * @param elements The elements to iterate over.
     * @return An iterator over those elements in a random order.
     * @param <T> The type of the elements.
     */
    public <T> Iterator<T> iterator(Iterable<? extends T> elements) {
        return this.<T>shuffle(elements).iterator();
    }

    /**
     * Create an iterator that returns the given elements in a random order.
     * @param elements The elements to iterate over.
     * @return An iterator over those elements in a random order.
     * @param <T> The type of the elements.
     */
    @SafeVarargs
    public final <T> Iterator<T> iterator(T... elements) {
        return iterator(Arrays.asList(elements));
    }

    /**
     * Get this instance's initial seed.
     * @return This instance's initial seed.
     */
    public long getInitialSeed() {
        return initialSeed;
    }

    /**
     * A {@code SeededRandom}'s string form contains its initial seed. This may
     * be helpful while analyzing test failures.
     */
    @Override
    public String toString() {
        return "SeededRandom(" + initialSeed + ")";
    }

    private static <T> List<T> toList(Iterable<? extends T> candidates) {
        List<T> list = new ArrayList<>();
        candidates.forEach(list::add);
        return list;
    }

}
