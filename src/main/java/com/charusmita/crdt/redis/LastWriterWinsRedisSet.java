package com.charusmita.crdt.redis;

import com.charusmita.crdt.ZSet;
import org.redisson.Redisson;
import org.redisson.RedissonScoredSortedSet;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LastWriterWinsRedisSet stores one instance of each element in the set with a specific score ( timestamp in our case )
 * Redis ZSet is implemented as the {@link org.redisson.api.RScoredSortedSet } in the Redisson JAVA Client. In this the
 * entry is stored as a key-value pair with a score. That is, key is the type of set (Add or Remove) recreated per object
 * and value is the element with score as the timestamp.
 * <p/>
 * All operations become simpler as Redis ZSet automatically orders most recent elements according to score (timestamp)
 *
 * <p/>
 * @param <T> Generic data type for element
 */
public class LastWriterWinsRedisSet<T> implements ZSet<T> {

    private final RScoredSortedSet<T> addSet;
    private final RScoredSortedSet<T> removeSet;

    public RScoredSortedSet<T> getAddSet() {
        return addSet;
    }

    public RScoredSortedSet<T> getRemoveSet() {
        return removeSet;
    }

    public LastWriterWinsRedisSet(RedissonScoredSortedSet<T> addSet, RedissonScoredSortedSet<T> removeSet) {
        this.addSet = addSet;
        this.removeSet = removeSet;
    }

    public LastWriterWinsRedisSet() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

        if (!redisson.getScoredSortedSet("simpleAdd").isEmpty()) {
            redisson.getScoredSortedSet("simpleAdd").clear();
        }

        if (!redisson.getScoredSortedSet("simpleRemove").isEmpty()) {
            redisson.getScoredSortedSet("simpleRemove").clear();
        }

        this.addSet = redisson.getScoredSortedSet("simpleAdd");
        this.removeSet = redisson.getScoredSortedSet("simpleRemove");
    }

    /**
     * Returns the newly created calling Set. Or if the calling set is not empty, the contents from the Add Set ZA
     * and the contents from the Remove Set ZR are cleared and then the current set is returned
     *
     * @return the (cleared/emptied) calling Set
     */
    public LastWriterWinsRedisSet<T> newSet() {
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
     * <p/>
     * We dont need to compare timestamps here as Redis automatically stores element according to score
     *
     * @param element   Element which needs to be stored in the set
     * @param timestamp The timestamp/score to have a temporal ordering of elements
     * @return the timestamp if successfully added or -1 for already existing element
     */
    @Override
    public int add(T element, int timestamp) {
        if (this.getAddSet().contains(element))
            return -1;
        this.getAddSet().add(timestamp, element);
        return timestamp;
    }

    /**
     * Adding an element with timestamp to the Remove set ZR of CRDT
     * If there is already an entry in ZR for e, its timestamp is set to t if t is more recent
     * than the currently-stored timestamp.
     * Otherwise, a new entry is inserted into ZR consisting of the element e and the given time t.
     * <p/>
     * We dont need to compare timestamps here as Redis automatically stores element according to score
     *
     * @param element   Element which needs to be stored in the set
     * @param timestamp The timestamp/score to have a temporal ordering of elements
     * @return the timestamp if successfully added or -1 for already existing element
     */
    @Override
    public int remove(T element, int timestamp) {
        if (this.getRemoveSet().contains(timestamp))
            return -1;
        this.getRemoveSet().add(timestamp, element);
        return timestamp;
    }

    /**
     * An element e is in the CRDT set, if the element e is in both Add Set ZA and Remove Set ZR.
     * If the timestamp of the entry from ZA is more recent than that of the entry from ZR, the
     * element is in the set.
     * If the element isn’t present, or the entry from ZR is newer than that from ZA, the element
     * is not in the set.
     * <p/>
     * We have to compare timestamps of Add Set and Remove Set here as there are separate scores
     * for the same element in Add and Remove Sets.
     *
     * @param element Element whose presence needs to be checked in the set
     * @return true if element is present according to above mentioned conditions otherwise false
     */
    @Override
    public boolean exists(T element) {
        if (this.getAddSet().contains(element) && this.getRemoveSet().contains(element)) {
            return (this.getAddSet().getScore(element) > this.getRemoveSet().getScore(element));
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
                .collect(Collectors.toSet());
        Set<T> elementInAddSetOnly = addSet.stream()
                .filter(x -> !(removeSetElements.contains(x)))
                .collect(Collectors.toSet());

        Set<T> commonElements = removeSet.stream()
                .filter(removeSetEntry -> addSet.stream()
                        .anyMatch(addSetEntry -> addSetEntry.equals(removeSetEntry)
                                && this.getAddSet().getScore(addSetEntry) > this.getRemoveSet().getScore(removeSetEntry)))
                .collect(Collectors.toSet());

        return Stream.of(elementInAddSetOnly, commonElements).flatMap(Set::stream)
                .collect(Collectors.toSet());

    }
}