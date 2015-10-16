package com.company;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Automate {

    private final List<StateTransitions> states = new ArrayList<StateTransitions>();
    private final Set<String> transitions = new TreeSet<String>();

    public List<StateTransitions> getStates() {
        return states;
    }

    public Set<String> getTransitions() {
        return transitions;
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
            Pattern pattern = Pattern.compile("\\{([\\w,]+:?)\\}");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String group = matcher.group(1);
                automate.getTransitions().addAll(Arrays.asList(group.split(",")));
            } else {
                throw new IllegalStateException("File format is incorrect");
            }

            scanner.nextLine(); // finite state
            scanner.nextLine(); // start state
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

                    int toName = reindex.indexOf(toState);
                    if (toName == -1) {
                        toName = reindex.size();
                        reindex.add(toState);
                    }

                    int size = states.size();
                    StateTransitions stateTransitions;
                    if (size <= fromName) {
                        // create state transitions if its not yet created
                        states.addAll(new ArrayList<StateTransitions>(fromName - size + 1));
                        stateTransitions = new StateTransitions(State.fromName(fromName));
                        states.set(fromName, stateTransitions);
                    } else {
                        stateTransitions = states.get(fromName);
                    }

                    stateTransitions.transition(transition, State.fromName(toName));
                } else {
                    throw new IllegalStateException("File format is incorrect");
                }
            }

            scanner.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }

        return automate;
    }

    @Override
    public String toString() {
        return "Automate{" +
                "states=" + states +
                ", transitions=" + transitions +
                '}';
    }
}
