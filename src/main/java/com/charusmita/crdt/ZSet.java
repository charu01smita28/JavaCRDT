package com.charusmita.crdt;

import java.util.Set;

public interface ZSet<T> {

    /**
     * Returns the newly created calling Set. Or if the calling set is not empty, the contents from the Add Set ZA
     * and the contents from the Remove Set ZR are cleared and then the current set is returned
     *
     * @return the (cleared/emptied) calling Set
     */
    ZSet<T> newSet();

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
    boolean exists(T element);

    /**
     * For getting all the contents of the calling CRDT set, both AddSet ZA and RemoveSet ZR are
     * scanned, selecting only those elements that are present in ZA without also being present in ZR,
     * or where the timestamp for the element in ZA is newer than the timestamp for the element in ZR.
     *
     * @return a Set of all elements of data type T which are present in the calling set
     */
    Set<T> getAllElements();
}
