package ru.vk.itmo.test.emelyanovvitaliy.dao;

import java.lang.foreign.MemorySegment;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Peeking {@link Iterator} wrapper.
 *
 * @author incubos
 */
final class WeightedPeekingEntryIterator
        implements Iterator<TimestampedEntry<MemorySegment>>,
        Comparable<WeightedPeekingEntryIterator> {
    private final int weight;
    private final Iterator<TimestampedEntry<MemorySegment>> delegate;
    private TimestampedEntry<MemorySegment> next;

    WeightedPeekingEntryIterator(
            final int weight,
            final Iterator<TimestampedEntry<MemorySegment>> delegate) {
        this.weight = weight;
        this.delegate = delegate;
        this.next = delegate.hasNext() ? delegate.next() : null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public TimestampedEntry<MemorySegment> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final TimestampedEntry<MemorySegment> result = next;
        next = delegate.hasNext() ? delegate.next() : null;
        return result;
    }

    TimestampedEntry<MemorySegment> peek() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return next;
    }

    @Override
    public int compareTo(final WeightedPeekingEntryIterator other) {
        // First compare keys
        int result =
                MemorySegmentComparator.INSTANCE.compare(
                        peek().key(),
                        other.peek().key());
        if (result != 0) {
            return result;
        }

        // Then compare weights if keys are equal
        return Integer.compare(weight, other.weight);
    }
}
