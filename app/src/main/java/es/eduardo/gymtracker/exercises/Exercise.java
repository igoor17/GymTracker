package es.eduardo.gymtracker.exercises;

/**
 * The type Exercise.
 */
public class Exercise {

    private String name;
    private String description;
    private String imageUrl;
    private String muscleGroup;

    /**
     * Instantiates a new Exercise.
     *
     * @param name        the name
     * @param description the description
     * @param imageUrl    the image url
     * @param muscleGroup the muscle group
     */
    public Exercise(String name, String description, String imageUrl, String muscleGroup) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.muscleGroup = muscleGroup;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Gets muscle group.
     *
     * @return the muscle group
     */
    public String getMuscleGroup() {
        return muscleGroup;
    }
}