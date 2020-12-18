package com.charusmita.crdt;

public interface ZSet<T> {

    /**
     * Adding an element with timestamp to the Add set ZA of CRDT
     * If there is already an entry in ZA for e, its timestamp is set to t if t is more recent
     * than the currently-stored timestamp.
     * Otherwise, a new entry is inserted into ZA consisting of the element e and the given time t.
     *
     * @param element Element which needs to be stored in the set
     * @param timestamp The timestamp to have a temporal ordering of elements
     *
     * @return the timestamp if successfully added or -1 for already existing element
     */
    int add(T element, int timestamp);

    /**
     * Adding an element with timestamp to the Remove set ZR of CRDT
     * If there is already an entry in ZR for e, its timestamp is set to t if t is more recent
     * than the currently-stored timestamp.
     * Otherwise, a new entry is inserted into ZR consisting of the element e and the given time t.
     *
     * @param element Element which needs to be stored in the set
     * @param timestamp The timestamp to have a temporal ordering of elements
     *
     * @return the timestamp if successfully added or -1 for already existing element
     */
    int remove(T element, int timestamp);
}