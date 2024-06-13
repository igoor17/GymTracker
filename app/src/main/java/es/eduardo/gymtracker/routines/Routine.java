package es.eduardo.gymtracker.routines;

import es.eduardo.gymtracker.exercises.Exercise;

import java.util.List;

/**
 * Represents a workout routine with its basic details and exercises.
 */
public class Routine {
    private String name;             // Name of the routine
    private String imageUrl;         // URL of the routine's image
    private int days;                // Number of days the routine spans
    private int totalExercises;      // Total number of exercises in the routine
    private List<Exercise> exercises;// List of exercises in the routine

    /**
     * Constructor to initialize a Routine object.
     *
     * @param name           Name of the routine.
     * @param imageUrl       URL of the routine's image.
     * @param days           Number of days the routine spans.
     * @param totalExercises Total number of exercises in the routine.
     * @param exercises      List of exercises in the routine.
     */
    public Routine(String name, String imageUrl, int days, int totalExercises, List<Exercise> exercises) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.days = days;
        this.totalExercises = totalExercises;
        this.exercises = exercises;
    }

    /**
     * Returns the name of the routine.
     *
     * @return The name of the routine.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the URL of the routine's image.
     *
     * @return The URL of the routine's image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the number of days the routine spans.
     *
     * @return The number of days the routine spans.
     */
    public int getDays() {
        return days;
    }

    /**
     * Returns the list of exercises in the routine.
     *
     * @return The list of exercises in the routine.
     */
    public List<Exercise> getExercises() {
        return exercises;
    }

    /**
     * Returns the total number of exercises in the routine.
     *
     * @return The total number of exercises in the routine.
     */
    public int getTotalExercises() {
        return totalExercises;
    }
}
