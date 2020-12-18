package com.charusmita.crdt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.addSet = Collections.synchronizedSet(new HashSet<>());
        this.removeSet = Collections.synchronizedSet(new HashSet<>());
    }

    public LastWriterWinsSet(Set<Entry<T>> addSet, Set<Entry<T>> removeSet) {
        this.addSet = addSet;
        this.removeSet = removeSet;
    }

    /**
     * Returns the newly created calling Set. Or if the calling set is not empty, the contents from the Add Set ZA
     * and the contents from the Remove Set ZR are cleared and then the current set is returned
     *
     * @return the (cleared/emptied) calling Set
     */
    @Override
    public LastWriterWinsSet<T> newSet() {
        if (!this.addSet.isEmpty())
            this.addSet.clear();
        if (!this.removeSet.isEmpty())
            this.removeSet.clear();
        return this;
    }

    /**
     * Adding an element with timestamp to the Add set ZA of CRDT
     * If there is already an entry in ZA for e, its timestamp is set to t if t is more recent
     * than the currently-stored timestamp.
     * Otherwise, a new entry is inserted into ZA consisting of the element e and the given time t.
     *
     * @param element   Element which needs to be stored in the set
     * @param timestamp The timestamp to have a temporal ordering of elements
     * @return the timestamp if successfully added or -1 for already existing element
     */
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

    /**
     * Adding an element with timestamp to the Remove set ZR of CRDT
     * If there is already an entry in ZR for e, its timestamp is set to t if t is more recent
     * than the currently-stored timestamp.
     * Otherwise, a new entry is inserted into ZR consisting of the element e and the given time t.
     *
     * @param element   Element which needs to be stored in the set
     * @param timestamp The timestamp to have a temporal ordering of elements
     * @return the timestamp if successfully added or -1 for already existing element
     */
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

    /**
     * An element e is in the CRDT set, if the element e is in both Add Set ZA and Remove Set ZR.
     * If the timestamp of the entry from ZA is more recent than that of the entry from ZR, the
     * element is in the set.
     * If the element isn’t present, or the entry from ZR is newer than that from ZA, the element
     * is not in the set.
     *
     * @param element Element whose presence needs to be checked in the set
     * @return true if element is present according to above mentioned conditions otherwise false
     */
    @Override
    public boolean exists(T element) {
        Optional<Entry<T>> elementInAddSet = addSet.stream()
                .filter(x -> x.getElement().equals(element))
                .findFirst();

        Optional<Entry<T>> elementInRemoveSet = removeSet.stream()
                .filter(x -> x.getElement().equals(element))
                .findFirst();

        if (elementInAddSet.isPresent() && elementInRemoveSet.isPresent()) {
            return elementInAddSet.get().getTimestamp() > elementInRemoveSet.get().getTimestamp();
        }
        return false;
    }

    /**
     * For getting all the contents of the calling CRDT set, both AddSet ZA and RemoveSet ZR are
     * scanned, selecting only those elements that are present in ZA without also being present in ZR,
     * or where the timestamp for the element in ZA is newer than the timestamp for the element in ZR.
     *
     * @return a Set of all elements of data type T which are present in the calling set
     */
    @Override
    public Set<T> getAllElements() {

        Set<T> removeSetElements = removeSet.stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        Set<T> elementInAddSetOnly = addSet.stream()
                .filter(x -> !(removeSetElements.contains(x.getElement())))
                .map(Entry::getElement)
                .collect(Collectors.toSet());

        Set<T> commonElements = removeSet.stream()
                .filter(removeSetEntry -> addSet.stream()
                        .anyMatch(addSetEntry -> addSetEntry.getElement().equals(removeSetEntry.getElement())
                                && addSetEntry.getTimestamp() > removeSetEntry.getTimestamp()))
                .map(Entry::getElement)
                .collect(Collectors.toSet());

        return Stream.of(elementInAddSetOnly, commonElements).flatMap(Set::stream)
                .collect(Collectors.toSet());

    }
}
