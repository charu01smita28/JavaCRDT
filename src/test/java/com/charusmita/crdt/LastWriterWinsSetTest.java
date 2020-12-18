package com.charusmita.crdt;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        LastWriterWinsSet.add("Test1", 1);
        LastWriterWinsSet.add("Test2", 2);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getAddSet().size(), 2);
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
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        LastWriterWinsSet.add("Test1", 1);
        LastWriterWinsSet.add("Test1", 4);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to add {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void add_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInAddSet() {
        //prepare
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        LastWriterWinsSet.add("Test1", 4);
        LastWriterWinsSet.add("Test1", 1);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getAddSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getAddSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove new {@link com.charusmita.crdt.Entry} objects with non-conflicting timestamps
     */
    @Test
    public void remove_AddingNewEntriesWithNewTimestamps_AddsToRemoveSet() {
        //prepare
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry1 = new Entry<>("Test1", 1);
        Entry<String> testEntry2 = new Entry<>("Test2", 2);

        //act
        LastWriterWinsSet.remove("Test1", 1);
        LastWriterWinsSet.remove("Test2", 2);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getRemoveSet().size(), 2);
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
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        LastWriterWinsSet.remove("Test1", 1);
        LastWriterWinsSet.remove("Test1", 4);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getRemoveSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }

    /**
     * Test to remove {@link com.charusmita.crdt.Entry} object with less recent timestamp
     * The current entry value is skipped
     */
    @Test
    public void remove_AddingEntriesWithLessRecentTimestamp_NoModificationToExistingEntryInRemoveSet() {
        //prepare
        LastWriterWinsSet<String> LastWriterWinsSet = new LastWriterWinsSet<>();
        Entry<String> testEntry = new Entry<>("Test1", 4);

        //act
        LastWriterWinsSet.remove("Test1", 4);
        LastWriterWinsSet.remove("Test1", 1);

        //assert
        Set<String> expectedElements = LastWriterWinsSet.getRemoveSet().stream()
                .map(Entry::getElement)
                .collect(Collectors.toSet());
        assertEquals(LastWriterWinsSet.getRemoveSet().size(), 1);
        assertTrue(expectedElements.contains(testEntry.getElement()));
    }
}