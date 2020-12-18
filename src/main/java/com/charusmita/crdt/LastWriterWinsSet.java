package com.charusmita.crdt;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * LastWriterWinSet stores one instance of each element in the set, and associates it with a timestamp.
 *
 * @param <T> Generic data type for element
 */
public class LastWriterWinsSet<T> implements ZSet<T> {
    private final Set<Entry<T>> addSet;
    private final Set<Entry<T>> removeSet;

    public Set<Entry<T>> getAddSet() {
        return addSet;
    }

    public Set<Entry<T>> getRemoveSet() {
        return removeSet;
    }

    public LastWriterWinsSet() {
        this.addSet = new HashSet<>();
        this.removeSet = new HashSet<>();
    }

    public LastWriterWinsSet(Set<Entry<T>> addSet, Set<Entry<T>> removeSet) {
        this.addSet = addSet;
        this.removeSet = removeSet;
    }

    public LastWriterWinsSet<T> newSet() {
        if (!this.addSet.isEmpty())
            this.addSet.clear();
        if (!this.removeSet.isEmpty())
            this.removeSet.clear();
        return this;
    }

    @Override
    public int add(T element, int timestamp) {
        Optional<Entry<T>> first = addSet.stream()
                .filter(x -> x.getElement().equals(element))
                .findFirst();
        if (first.isPresent()) {
            if (first.get().getTimestamp() < timestamp) {
                addSet.remove(first.get());
                addSet.add(new Entry<>(element, timestamp));
                return timestamp;
            } else {
                return -1;
            }
        }
        addSet.add(new Entry<>(element, timestamp));
        return timestamp;
    }

    @Override
    public int remove(T element, int timestamp) {
        Optional<Entry<T>> first = removeSet.stream()
                .filter(x -> x.getElement().equals(element))
                .findFirst();
        if (first.isPresent()) {
            if (first.get().getTimestamp() < timestamp) {
                removeSet.remove(first.get());
                removeSet.add(new Entry<>(element, timestamp));
                return timestamp;
            } else {
                return -1;
            }
        }
        removeSet.add(new Entry<>(element, timestamp));
        return timestamp;
    }
}
