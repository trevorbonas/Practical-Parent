package com.raspberry.practicalparent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsManager implements Iterable<Results>{
    private static List<Results> results = new ArrayList<>();

    private ResultsManager() { }

    public void add(Results result) {
        results.add(result);
    }

    @NonNull
    @Override
    public Iterator<Results> iterator() {
        return results.iterator();
    }
}
