package com.charusmita.crdt;

import java.util.HashSet;
import java.util.Set;

/**
 * LastWriterWinSet stores one instance of each element in the set, and associates it with a timestamp.
 * @param <T> Generic data type for element
 */
public class LastWriterWinsSet<T> implements ZSet<T>{
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
        this.removeSet =removeSet;
    }

    public LastWriterWinsSet<T> newSet() {
        return this;
    }

    @Override
    public int add(T element, int timestamp) {
        return timestamp;
    }

    @Override
    public int remove(T element, int timestamp) {
        return timestamp;
    }
}
