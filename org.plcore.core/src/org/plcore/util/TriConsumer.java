package org.plcore.util;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result.  This is the three-arity specialization of Consumer.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <W> the type of the third argument to the operation
 *
 */
@FunctionalInterface
public interface TriConsumer<T, U, W> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param w the third input argument
     */
    void accept(T t, U u, W w);

}
