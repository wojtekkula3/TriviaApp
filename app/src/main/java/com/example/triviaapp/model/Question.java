package com.example.triviaapp.model;

public class Question {
    private String name;
    private boolean isTrue;
    private boolean isAnwsered = false;

    public Question() {
    }

    public Question(String name, boolean isTrue) {
        this.name = name;
        this.isTrue = isTrue;
    }

    public Question(String name, boolean isTrue, boolean isAnwsered) {
        this.name = name;
        this.isTrue = isTrue;
        this.isAnwsered = isAnwsered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    public boolean isAnwsered() {
        return isAnwsered;
    }

    public void setAnwsered(boolean anwsered) {
        isAnwsered = anwsered;
    }
}
