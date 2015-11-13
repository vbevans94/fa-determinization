package com.company;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Automate {

    private final List<StateTransitions> states = new ArrayList<StateTransitions>();
    private final Set<String> transitions = new TreeSet<String>();
    private final Set<State> finalStates = new HashSet<State>();
    private final Set<State> initialStates = new HashSet<State>();

    public List<StateTransitions> getStates() {
        return states;
    }

    public Set<String> getTransitions() {
        return transitions;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public Set<State> getInitialStates() {
        return initialStates;
    }

    public Set<Integer> getInitialStateNames() {
        Set<Integer> names = new HashSet<Integer>();
        for (State state : getInitialStates()) {
            names.add(state.getName());
        }
        return names;
    }

    public void output() {
        int size = states.size();
        System.out.format("%d%n", size);

        for (StateTransitions stateTransitions : states) {
            // number of transitions for current state
            List<Map.Entry<String, State>> transitions = stateTransitions.getTransitions();
            int transitionCount = transitions.size();
            System.out.format("%d ", transitionCount);

            for (Map.Entry<String, State> entry : transitions) {
                System.out.format("%s %d ", entry.getKey(), entry.getValue().getName());
            }
            System.out.format("%n");
        }
        // output initial states(actually state)
        outputStates(initialStates);
        // output final states
        outputStates(finalStates);
    }

    private static void outputStates(Set<State> states) {
        for (State state : states) {
            System.out.format("%d ", state.getName());
        }
        System.out.format("%n");
    }

    public static Automate fromFile(String fileName) {
        Automate automate = new Automate();

        try {
            Scanner reader = new Scanner(new BufferedReader(new FileReader(fileName)));
            reader.useLocale(Locale.US);

            int stateCount = reader.nextInt();

            for (int fromState = 0; fromState < stateCount; fromState++) {
                // number of transitions for current state
                int transitionCount = reader.nextInt();

                StateTransitions stateTransitions = new StateTransitions(State.fromName(fromState));

                for (int j = 0; j < transitionCount; j++) {
                    String transition = reader.next("\\w");
                    automate.transitions.add(transition); // remember all transitions
                    int toState = reader.nextInt();

                    stateTransitions.transition(transition, State.fromName(toState));
                }

                automate.states.add(stateTransitions);
            }

            reader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }

        return automate;
    }

    public static Automate fromPrevStepFile(String fileName) {
        Automate automate = new Automate();
        List<StateTransitions> states = automate.states;

        List<String> reindex = new ArrayList<String>(); // new names of

        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
            scanner.useLocale(Locale.US);

            String line = scanner.nextLine();
            Pattern pattern = Pattern.compile("\\{([\\w,\\$]+:?)\\}");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String group = matcher.group(1);
                fillFromGroup(automate.getTransitions(), group);
            } else {
                incorrectInput();
            }

            // read final state
            List<String> finalStates = new ArrayList<String>();
            line = scanner.nextLine();
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                String group = matcher.group(1);

                fillFromGroup(finalStates, group);
            } else {
                return incorrectInput();
            }

            List<String> initialStates = new ArrayList<String>();
            line = scanner.nextLine();
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                String group = matcher.group(1);

                fillFromGroup(initialStates, group);
            } else {
                return incorrectInput();
            }
            scanner.nextLine(); // Q
            scanner.nextLine(); // T

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                // read state transition
                pattern = Pattern.compile("\\(([\\w\\$]+?),([\\w]+?)\\)=>(.+?)$");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String fromState = matcher.group(1);
                    String transition = matcher.group(2);
                    String toState = matcher.group(3);

                    int fromName = reindex.indexOf(fromState);
                    if (fromName == -1) {
                        fromName = reindex.size();
                        reindex.add(fromState);
                    }

                    // add states if it's re-indexed
                    addReindexed(automate.finalStates, finalStates, fromState, fromName);
                    addReindexed(automate.initialStates, initialStates, fromState, fromName);

                    int toName = reindex.indexOf(toState);
                    if (toName == -1) {
                        toName = reindex.size();
                        reindex.add(toState);
                    }

                    // add to corresponding sets if reindexed
                    addReindexed(automate.finalStates, finalStates, toState, toName);
                    addReindexed(automate.initialStates, initialStates, toState, toName);

                    int size = states.size();
                    StateTransitions stateTransitions;
                    if (size <= fromName) {
                        // create state transitions if its not yet created
                        states.addAll(new ArrayList<StateTransitions>(fromName - size + 1));
                        stateTransitions = new StateTransitions(State.fromName(fromName));
                        states.add(fromName, stateTransitions);
                    } else {
                        stateTransitions = states.get(fromName);
                    }

                    stateTransitions.transition(transition, State.fromName(toName));
                } else {
                    return incorrectInput();
                }
            }

            scanner.close();
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }

        return automate;
    }

    private static void addReindexed(Set<State> reindexedStates, List<String> states, String state, int reindexedName) {
        if (states.contains(state)) {
            State newState = State.fromName(reindexedName);
            newState.addToSet(reindexedName); // to make them different as equals use only set
            reindexedStates.add(newState);
        }
    }

    private static void fillFromGroup(Collection<String> states, String group) {
        states.addAll(Arrays.asList(group.split(",")));
    }

    private static Automate incorrectInput() {
        throw new IllegalStateException("File format is incorrect");
    }

    @Override
    public String toString() {
        return "Automate{" +
                "states=" + states +
                ", transitions=" + transitions +
                '}';
    }
}
