/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.foundation;

import java.util.List;

/**
 * Provides defensive immutable list copying for source-data collections.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public final class CollectionCopies {

    private CollectionCopies() {
    }

    /**
     * Copies a nullable list to a non-null immutable snapshot.
     *
     * <p>A null input produces an empty list. Null elements are rejected,
     * preserving the behaviour of {@link List#copyOf(java.util.Collection)}.</p>
     *
     * @param <T> type of elements in the returned list
     * @param values source values, or null
     * @return an immutable snapshot, or an empty list if values is null
     * @throws NullPointerException if a source element is null
     */
    public static <T> List<T> immutableOrEmpty(List<? extends T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
