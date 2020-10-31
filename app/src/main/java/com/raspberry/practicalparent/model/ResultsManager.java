package com.raspberry.practicalparent.model;

import androidx.annotation.NonNull;

import com.raspberry.practicalparent.model.Results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultsManager implements Iterable<Results>{
    private static List<Results> results = new ArrayList<>();
    private static ResultsManager instance;

    public static ResultsManager getInstance() {
        if (instance == null) {
            instance = new ResultsManager();
        }
        return instance;
    }

    public List<Results> getList() {
        return results;
    }

    public void setList(List<Results> list) {
        results = list;
    }

    private ResultsManager() {
        // Private for singleton support
    }

    public void add(Results result) {
        results.add(result);
    }

    @NonNull
    @Override
    public Iterator<Results> iterator() {
        return results.iterator();
    }
}
