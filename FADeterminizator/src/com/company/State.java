package com.company;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by vbevans94 on 10/15/15.
 */
public class State {

    private int name;
    private final Set<Integer> set = new TreeSet<Integer>();

    private State() {
    }

    public static State fromName(int name) {
        State state = new State();
        state.setName(name);
        return state;
    }

    public static State empty() {
        return new State();
    }

    public void addToSet(int state) {
        set.add(state);
    }

    public void addToSet(Set<Integer> stateSet) {
        set.addAll(stateSet);
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    public Set<Integer> getSet() {
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return set.equals(state.set);

    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public String toString() {
        return "State{" +
                "name=" + name +
                ", set=" + set +
                '}';
    }
}
