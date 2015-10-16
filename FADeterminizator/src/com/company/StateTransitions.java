package com.company;

import java.util.*;

public class StateTransitions {

    private final State state;

    private final List<Map.Entry<String, State>> transitions = new ArrayList<Map.Entry<String, State>>();

    public StateTransitions(State state) {
        this.state = state;
    }

    public List<Map.Entry<String, State>> getTransitions() {
        return transitions;
    }

    public void transition(String transition, State state) {
        transitions.add(new AbstractMap.SimpleEntry<String, State>(transition, state));
    }

    public Set<Integer> toStates(String state) {
        Set<Integer> statesSet = new HashSet<Integer>();
        for (Map.Entry<String, State> entry : transitions) {
            if (entry.getKey().equals(state)) {
                statesSet.add(entry.getValue().getName());
            }
        }
        return statesSet;
    }

    @Override
    public String toString() {
        return "StateTransitions{" +
                "state=" + state +
                ", transitions=" + transitions +
                '}';
    }
}
