package es.eduardo.gymtracker.routines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.eduardo.gymtracker.exercises.Exercise;

public class Routine {
    private String name;
    private List<Exercise> exercises;
    private int days;
    private Map<String, Map<String, Integer>> exerciseLogs; // Map<ExerciseName, Map<Day, Weight>>

    public Routine(String name, List<Exercise> exercises, int days) {
        this.name = name;
        this.exercises = exercises;
        this.days = days;
        this.exerciseLogs = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public int getDays() {
        return days;
    }

    public Map<String, Map<String, Integer>> getExerciseLogs() {
        return exerciseLogs;
    }

    public void logExercise(String exerciseName, String day, int weight) {
        if (!exerciseLogs.containsKey(exerciseName)) {
            exerciseLogs.put(exerciseName, new HashMap<>());
        }
        exerciseLogs.get(exerciseName).put(day, weight);
    }
}