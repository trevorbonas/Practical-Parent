package com.raspberry.practicalparent.model;

import androidx.annotation.NonNull;

import com.raspberry.practicalparent.model.Results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsManager implements Iterable<Results>{
    private static List<Results> results = new ArrayList<>();

    public ResultsManager() { }

    public void add(Results result) {
        results.add(result);
    }

    @NonNull
    @Override
    public Iterator<Results> iterator() {
        return results.iterator();
    }
}
