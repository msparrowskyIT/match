package com.mwroblewski.utils;

public class Utils {

    public static <T extends IUtils<T>> T handleInPlace(T t, boolean inPlace){
        if(inPlace)
            return t;
        else
            return t.getDeepCopy(t);
    }

}
