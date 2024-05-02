package es.eduardo.gymtracker.routines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.eduardo.gymtracker.exercises.Exercise;

public class Routine {
    private String name;
    private String imageUrl;
    private int days;
    private int totalExercises;
    private List<Exercise> exercises;

    public Routine(String name, String imageUrl, int days,int totalExercises ,List<Exercise> exercises) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.days = days;
        this.totalExercises = totalExercises;
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getDays() {
        return days;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public int getTotalExercises() {
        return totalExercises;
    }
}