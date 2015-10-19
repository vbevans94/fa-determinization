package com.company;

import java.util.*;

/**
 * Created by vbevans94 on 10/15/15.
 */
public class Determinizator {

    public static Automate determine(Automate automate) {
        Automate determined = new Automate();
        Set<String> transitions = automate.getTransitions(); // all transitions
        determined.getTransitions().addAll(transitions); // transitions are the same

        List<StateTransitions> states = automate.getStates();

        // form 0 state
        int stateName = 0;

        State topState = State.fromName(stateName);
        topState.addToSet(stateName); // for first state names are equal, both are 0

        Set<State> newStates = new HashSet<State>();
        newStates.add(topState);

        Queue<State> queue = new ArrayDeque<State>();
        queue.offer(topState);

        // start iterative process of creating new states of determined automate
        while (!queue.isEmpty()) {
            // build set of state which may be new
            topState = queue.poll();
            // initial states move from automate to determined
            for (State initialState : automate.getInitialStates()) {
                if (topState.getSet().contains(initialState.getName())) {
                    determined.getInitialStates().add(topState);
                    break;
                }
            }
            // transitions of determined automate
            StateTransitions topTransitions = new StateTransitions(topState);

            // for every transition
            for (String transition : transitions) {
                // build state and see if it's new
                State tempState = State.empty();

                // form new state by transitioning from every state from its set
                for (Integer state : topState.getSet()) {
                    StateTransitions stateTransitions = states.get(state);

                    tempState.addToSet(stateTransitions.toStates(transition));
                }

                // check if it's new
                if (!newStates.contains(tempState)) {
                    // it's new
                    stateName++; // set name
                    tempState.setName(stateName);

                    // final states move from automate to determined
                    for (State finalState : automate.getFinalStates()) {
                        if (tempState.getSet().contains(finalState.getName())) {
                            determined.getFinalStates().add(tempState);
                            break;
                        }
                    }

                    topTransitions.transition(transition, tempState);

                    newStates.add(tempState); // will be a part of new automate
                    queue.offer(tempState); // we have to look it on further iterations
                } else {
                    // find this state in new states
                    for (State newState : newStates) {
                        if (newState.equals(tempState)) {
                            // create transition to existing state
                            topTransitions.transition(transition, newState);
                            break;
                        }
                    }
                }
            }

            determined.getStates().add(topTransitions);
        }

        return determined;
    }
}
