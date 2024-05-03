package es.eduardo.gymtracker.exercises;

public class Exercise {

    private String name;
    private String description;
    private String imageUrl;
    private String muscleGroup;

    public Exercise(String name, String description, String imageUrl, String muscleGroup) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.muscleGroup = muscleGroup;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }
}