/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.foundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Verifies generic immutable copying of weather and other source-data lists.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
class CollectionCopiesTest {

    /**
     * A missing source list maps to an immutable empty list.
     */
    @Test
    void nullSourceReturnsEmptyList() {
        assertEquals(List.of(), CollectionCopies.<String>immutableOrEmpty(null));
    }

    /**
     * Copying isolates the resulting list from later source changes.
     */
    @Test
    void sourceMutationsDoNotChangeSnapshot() {
        var source = new ArrayList<>(List.of("first"));
        var snapshot = CollectionCopies.immutableOrEmpty(source);
        source.add("second");
        assertEquals(List.of("first"), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("third"));
    }

    /**
     * Null elements are rejected rather than silently removed.
     */
    @Test
    void nullElementsAreRejected() {
        assertThrows(NullPointerException.class,
                () -> CollectionCopies.immutableOrEmpty(Arrays.asList("first", null)));
    }

    /**
     * A list of subtypes can be copied to a supertype list without casts.
     */
    @Test
    void subtypeListsAreAccepted() {
        List<Integer> input = List.of(1, 2, 3);
        List<Number> numbers = CollectionCopies.immutableOrEmpty(input);
        assertEquals(input, numbers);
    }
}
