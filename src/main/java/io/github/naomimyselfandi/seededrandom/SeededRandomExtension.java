package io.github.naomimyselfandi.seededrandom;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Constructor;

/**
 * A JUnit 5 extension that provides deterministic randomness in tests.
 * <p>
 * This extension resolves parameters of type {@link SeededRandom}, providing
 * each with a unique, deterministic seed based on the test method and parameter
 * index. When used with a test template such as {@code @RepeatedTest} or
 * {@code @ParameterizedTest}, all invocations of the test method receive unique
 * seeds. Subclasses of `SeededRandom` are automatically supported, so long as
 * they declare a constructor that accepts a single `long`.
 * </p>
 * <p>
 * This extension also takes special consideration for lifecycle callbacks, most
 * significantly {@code &#64;BeforeEach} methods. **When {@code SeededRandom}s
 * are injected into multiple methods during the same test, the same instance is
 * provided for each parameter with the same index.** For example, suppose a
 * setup method annotated with {@code &#64;BeforeEach} accepts an instance of
 * {@code SeededRandom} as its first parameter, as does a test method. The setup
 * method and the test will receive the same instance.
 * </p>
 * <p>
 * Seeds are computed using the parameter's index and the test iteration's
 * {@linkplain org.junit.jupiter.api.TestInfo#getDisplayName() display name}
 * which includes the iteration index for all standard test templates. This
 * ensures reproducible "randomness" while avoiding accidental collisions.
 * </p>
 * <p><strong>Example usage:</strong></p>
 * <pre>
 * &#64;ExtendWith(SeededRandomExtension.class)
 * class SomeTest {
 *
 *     &#64;Test
 *     void testSomething(SeededRandom random) {
 *         UUID id = random.nextUUID();
 *         // ...
 *     }
 *
 *     &#64;RepeatedTest(5)
 *     void testSomethingElse(SeededRandom random) {
 *         int x = random.nextInt();
 *         // ...
 *     }
 *
 *     &#64;ParameterizedTest
 *     &#64;ValueSource(ints = {1, 2, 3})
 *     void testAThirdThing(int x, SeededRandom random) {
 *         int y = random.nextInt();
 *         // ...
 *     }
 *
 * }
 * </pre>
 */
public class SeededRandomExtension implements ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
            SeededRandomExtension.class.getCanonicalName()
    );

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return SeededRandom.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        long index = parameterContext.getIndex() + 1L;
        Class<?> type = parameterContext.getParameter().getType();
        return extensionContext.getStore(NAMESPACE.append(type)).getOrComputeIfAbsent("p" + index, key -> {
            long hash = extensionContext.getDisplayName().hashCode();
            long seed = (index << 32) + hash;
            return construct(type, seed);
        });
    }

    /**
     * Construct an instance of a random number generator with a given seed.
     * @param type The random number generator implementation.
     * @param seed The seed for the generator.
     * @return The newly constructed generator.
     */
    protected Object construct(Class<?> type, long seed) {
        return doConstruction(type, seed);
    }

    static Object doConstruction(Class<?> type, long seed) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor(long.class);
            constructor.setAccessible(true);
            return constructor.newInstance(seed);
        } catch (ReflectiveOperationException e) {
            String message = "Failed to construct " + type.getCanonicalName() + " with seed " + seed + ".";
            throw new RuntimeException(message, e);
        }
    }

}
