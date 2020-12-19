package com.charusmita.crdt.redis;

import com.charusmita.crdt.Entry;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LastWriterWinsRedisSetTest {

    /**
     * Test for newSet() operation on {@link com.charusmita.crdt.redis.LastWriterWinsRedisSet}
     */
    @Test
    public void newSet_AddValues_ReturnsNewSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();

        //act
        lastWriterWinsRedisSet.add("Test1", 1);
        LastWriterWinsRedisSet<String> expectedLastWriterWinSet = lastWriterWinsRedisSet.newSet();

        //assert
        assertTrue(expectedLastWriterWinSet.getAddSet().isEmpty());
        assertTrue(expectedLastWriterWinSet.getRemoveSet().isEmpty());
    }

    /**
     * Test to add new {@link com.charusmita.crdt.Entry} objects with non-conflicting timestamps
     */
    @Test
    public void add_AddingNewEntriesWithNewTimestamps_AddsToAddSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test2", 2);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getAddSet().stream()
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsRedisSet.getAddSet().size(), 2);
        assertTrue(expectedElements.contains(testEntry1.getElement()));
        assertTrue(expectedElements.contains(testEntry2.getElement()));
    }

    /**
     * Test to add {@link com.charusmita.crdt.Entry} object with more recent timestamp
     * The entry value gets updated in the Add set
     */
    @Test
    public void add_AddingEntriesWithMoreRecentTimestamp_ModifiesExistingEntryInAddSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test1", 4);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getAddSet().stream()
                .collect(Collectors.toSet());

        assertEquals(lastWriterWinsRedisSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to add {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void add_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInAddSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsRedisSet.add("Test1", 4);
        lastWriterWinsRedisSet.add("Test1", 1);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getAddSet().stream()
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsRedisSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove new {@link com.charusmita.crdt.Entry} objects with non-conflicting timestamps
     */
    @Test
    public void remove_AddingNewEntriesWithNewTimestamps_AddsToRemoveSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        lastWriterWinsRedisSet.remove("Test1", 1);
        lastWriterWinsRedisSet.remove("Test2", 2);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getRemoveSet().stream()
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsRedisSet.getRemoveSet().size(), 2);
        assertTrue(expectedElements.contains(testEntry1.getElement()));
        assertTrue(expectedElements.contains(testEntry2.getElement()));
    }

    /**
     * Test to remove {@link com.charusmita.crdt.Entry} object with more recent timestamp
     * The entry value gets updated in the remove set
     */
    @Test
    public void remove_AddingEntriesWithMoreRecentTimestamp_ModifiesExistingEntryInRemoveSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsRedisSet.remove("Test1", 1);
        lastWriterWinsRedisSet.remove("Test1", 4);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getRemoveSet().stream()
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsRedisSet.getRemoveSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void remove_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInRemoveSet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsRedisSet.remove("Test1", 4);
        lastWriterWinsRedisSet.remove("Test1", 1);

        //assert
        Set<String> expectedElements = lastWriterWinsRedisSet.getRemoveSet().stream()
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsRedisSet.getRemoveSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to check {@link com.charusmita.crdt.Entry} element exists in the calling
     * set or not. Positive test case when timestamp in ZA more recent than timestamp
     * in ZR
     */
    @Test
    public void exists_ExistingElementCheck_ReturnsTrue() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();

        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test2", 3);
        lastWriterWinsRedisSet.remove("Test2", 2);

        //act
        boolean expected = lastWriterWinsRedisSet.exists("Test2");

        //assert
        assertTrue(expected);
    }


    /**
     * Test to check {@link com.charusmita.crdt.Entry} element exists in the calling
     * set or not. Negative test case when timestamp in ZA less recent than timestamp
     * in ZR
     */
    @Test
    public void exists_LessRecentExistingElementCheck_ReturnsFalse() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();

        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test2", 2);
        lastWriterWinsRedisSet.remove("Test2", 3);

        //act
        boolean expected = lastWriterWinsRedisSet.exists("Test2");

        //assert
        assertFalse(expected);
    }

    /**
     * Test to check {@link com.charusmita.crdt.Entry} element exists in the calling
     * set or not. Negative test case when entry not in set
     */
    @Test
    public void exists_NonexistentElementCheck_ReturnsFalse() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();

        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test2", 2);
        lastWriterWinsRedisSet.remove("Test2", 3);

        //act
        boolean expected = lastWriterWinsRedisSet.exists("Test3");

        //assert
        assertFalse(expected);
    }

    /**
     * Test to check {@link com.charusmita.crdt.Entry} elements existing only in AddSet and RemoveSet as well.
     * Returns all elements whose timestamp of add is more recent than that of remove.
     */
    @Test
    public void getAllElements_WithElementsOnlyInAddSet_ReturnsElements() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();
        Set<String> actualSet = Stream.of("Test2", "Test4").collect(Collectors.toSet());

        lastWriterWinsRedisSet.add("Test1", 1);
        lastWriterWinsRedisSet.add("Test2", 3);
        lastWriterWinsRedisSet.add("Test4", 6);
        lastWriterWinsRedisSet.remove("Test3", 2);
        lastWriterWinsRedisSet.remove("Test1", 5);

        //act
        Set<String> expectedSet = lastWriterWinsRedisSet.getAllElements();

        //assert
        assertEquals(expectedSet, actualSet);
    }

    /**
     * Test to check {@link com.charusmita.crdt.Entry} elements returns empty set when
     * no elements in Add set or remove set
     */
    @Test
    public void getAllElements_NoElementsAdded_ReturnsEmptySet() {
        //prepare
        LastWriterWinsRedisSet<String> lastWriterWinsRedisSet = new LastWriterWinsRedisSet<>();

        //act
        Set<String> expectedSet = lastWriterWinsRedisSet.getAllElements();

        //assert
        assertEquals(expectedSet, Collections.emptySet());
    }
}