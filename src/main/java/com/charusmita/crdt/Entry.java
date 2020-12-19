package com.charusmita.crdt;

import java.io.Serializable;

/**
 * An Entry is a class which consists of an element of datatype T and timestamp
 * to be used in {@link com.charusmita.crdt.LastWriterWinsSet}
 *
 * @param <T> Datatype of the element to be stored as {@link com.charusmita.crdt.Entry}
 */
public class Entry<T> implements Serializable {

    private T element;
    private int timestamp;

    public Entry() {
    }

    public Entry(T element, int timestamp) {
        this.element = element;
        this.timestamp = timestamp;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

}
