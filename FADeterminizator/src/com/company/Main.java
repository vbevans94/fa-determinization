package com.company;

public class Main {

    public static void main(String[] args) {
        Automate automate = Automate.fromPrevStepFile("in");

        Automate determined = Determinizator.determine(automate);
        determined.output();
    }
}
