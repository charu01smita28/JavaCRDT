package com.charusmita.crdt;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LastWriterWinsSetTest {

    /**
     * Test for newSet() operation on {@link com.charusmita.crdt.LastWriterWinsSet}
     */
    @Test
    public void newSet_AddValues_ReturnsNewSet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();

        //act
        lastWriterWinsSet.add("Test1", 1);
        LastWriterWinsSet<String> expectedLastWriterWinsSet = lastWriterWinsSet.newSet();

        //assert
        assertTrue(expectedLastWriterWinsSet.getAddSet().isEmpty());
        assertTrue(expectedLastWriterWinsSet.getRemoveSet().isEmpty());
    }

    /**
     * Test to add new {@link com.charusmita.crdt.Entry} objects with non-conflicting timestamps
     */
    @Test
    public void add_AddingNewEntriesWithNewTimestamps_AddsToAddSet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test2", 2);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getAddSet().size(), 2);
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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test1", 4);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to add {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void add_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInAddSet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsSet.add("Test1", 4);
        lastWriterWinsSet.add("Test1", 1);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove new {@link com.charusmita.crdt.Entry} objects with non-conflicting timestamps
     */
    @Test
    public void remove_AddingNewEntriesWithNewTimestamps_AddsToRemoveSet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        lastWriterWinsSet.remove("Test1", 1);
        lastWriterWinsSet.remove("Test2", 2);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getRemoveSet().size(), 2);
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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsSet.remove("Test1", 1);
        lastWriterWinsSet.remove("Test1", 4);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getRemoveSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void remove_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInRemoveSet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        lastWriterWinsSet.remove("Test1", 4);
        lastWriterWinsSet.remove("Test1", 1);

        //assert
        Set<String> expectedElements = lastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(lastWriterWinsSet.getRemoveSet().size(), 1);
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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();

        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test2", 3);
        lastWriterWinsSet.remove("Test2", 2);

        //act
        boolean expected = lastWriterWinsSet.exists("Test2");

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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();

        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test2", 2);
        lastWriterWinsSet.remove("Test2", 3);

        //act
        boolean expected = lastWriterWinsSet.exists("Test2");

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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();

        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test2", 2);
        lastWriterWinsSet.remove("Test2", 3);

        //act
        boolean expected = lastWriterWinsSet.exists("Test3");

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
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();
        Set<String> actualSet = Stream.of("Test2","Test4").collect(Collectors.toSet());

        lastWriterWinsSet.add("Test1", 1);
        lastWriterWinsSet.add("Test2", 3);
        lastWriterWinsSet.add("Test4",6);
        lastWriterWinsSet.remove("Test3", 2);
        lastWriterWinsSet.remove("Test1",5);

        //act
        Set<String> expectedSet = lastWriterWinsSet.getAllElements();

        //assert
        assertEquals(expectedSet,actualSet);
    }

    /**
     * Test to check {@link com.charusmita.crdt.Entry} elements returns empty set when
     * no elements in Add set or remove set
     */
    @Test
    public void getAllElements_NoElementsAdded_ReturnsEmptySet() {
        //prepare
        LastWriterWinsSet<String> lastWriterWinsSet = new LastWriterWinsSet<>();

        //act
        Set<String> expectedSet = lastWriterWinsSet.getAllElements();

        //assert
        assertEquals(expectedSet, Collections.emptySet());
    }

}