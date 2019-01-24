package com.mwroblewski.aisd.heap;

import java.lang.reflect.Array;
import java.util.Comparator;

public class Heap<T> {

    private T[] data;
    private int capacity;
    private int lastIndex;
    private final Class<T> classType;
    private final Comparator<T> comparator;

    public Heap(int capacity, Comparator<T> comparator, Class<T> classType) {
        this.data = (T[]) Array.newInstance(classType, capacity);
        this.capacity = capacity;
        this.lastIndex = -1;
        this.comparator = comparator;
        this.classType = classType;
    }

    public Heap(Comparator<T> comparator, Class<T> classType) {
        this(16, comparator, classType);
    }

    public Heap(T[] data, Comparator<T> comparator, Class<T> classType) {
        this(data.length, comparator, classType);
        for(int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
            this.lastIndex++;
            this.heapUp();
        }
    }

    private void swap(int i, int j) {
        T tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    private void extend(){
        T[] newData = (T[]) Array.newInstance(this.classType, 2*this.capacity);
        this.capacity *= 2;
        for(int i = 0; i < this.data.length; i++)
            newData[i] = this.data[i];
        this.data = newData;
    }

    private void heapUp() {
        int i = this.lastIndex;
        while (i > 0) {
            int p = (i - 1) / 2;
            int cmp = this.comparator.compare(data[p], data[i]);
            if (cmp == 1)
                return;
            else {
                swap(p, i);
                i = p;
            }
        }
    }

    private void heapDown() {
        int i = 0;
        int j = 1;
        while (j < this.lastIndex) {
            if(j+1 < this.lastIndex && this.comparator.compare(this.data[j+1], data[j+1]) == 1)
                j++;

            if(this.comparator.compare(this.data[i], data[j]) > 0)
                return;
            else{
                this.swap(i,j);
                i = j;
                j = 2*j+1;
            }
        }
    }

    public void push(T item){
        if(this.lastIndex + 1 == capacity)
            this.extend();

        this.data[++this.lastIndex] = item;
        this.heapUp();
    }

    public T pop(){
        if(this.lastIndex == -1)
            return null;
        T tmp = data[0];
        data[0] = data[this.lastIndex--];
        this.heapDown();

        return tmp;
    }
}
