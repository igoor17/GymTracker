package es.eduardo.gymtracker.utils;

import android.content.Context;

import es.eduardo.gymtracker.R;

/**
 * Utility class containing static methods for various fitness-related calculations and localization.
 */
public class Utils {

    /**
     * Returns the category based on the BMI result.
     *
     * @param result  The BMI result to determine the category.
     * @param context The context to access string resources.
     * @return The category corresponding to the BMI result.
     */
    public static String getCategory(float result, Context context) {
        String category;
        if (result < 15) {
            category = context.getString(R.string.severe_thinness);
        } else if (result >= 15.0 && result <= 16.0) {
            category = context.getString(R.string.moderate_thinness);
        } else if (result > 16 && result <= 18.5) {
            category = context.getString(R.string.mild_thinness);
        } else if (result > 18.5 && result <= 25) {
            category = context.getString(R.string.normal_weight);
        } else if (result > 25 && result <= 30) {
            category = context.getString(R.string.overweight);
        } else if (result > 30 && result <= 35) {
            category = context.getString(R.string.moderately_obese);
        } else if (result > 35 && result <= 40) {
            category = context.getString(R.string.severely_obese);
        } else {
            category = context.getString(R.string.very_severely_obese);
        }
        return category;
    }

    /**
     * Returns suggestions based on the BMI result.
     *
     * @param result  The BMI result to determine the suggestions.
     * @param context The context to access string resources.
     * @return Suggestions corresponding to the BMI result.
     */
    public static String getSuggestions(float result, Context context) {
        String suggestion;
        if (result < 18.5) {
            suggestion = context.getString(R.string.underweight_suggestion);
        } else if (result >= 18.5 && result < 25) {
            suggestion = context.getString(R.string.normal_weight_suggestion);
        } else if (result >= 25 && result < 30) {
            suggestion = context.getString(R.string.overweight_suggestion);
        } else {
            suggestion = context.getString(R.string.obese_suggestion);
        }
        return suggestion;
    }

    /**
     * Translates a muscle group name into the corresponding localized string.
     *
     * @param muscleGroup The muscle group name to be translated.
     * @param context     The context to access string resources.
     * @return The translated muscle group name or the original name if not found.
     */
    public static String getTranslatedMuscleGroup(String muscleGroup, Context context) {
        switch (muscleGroup.toLowerCase()) {
            case "back":
                return context.getString(R.string.muscle_group_back);
            case "chest":
                return context.getString(R.string.muscle_group_chest);
            case "arms":
                return context.getString(R.string.muscle_group_arms);
            case "legs":
                return context.getString(R.string.muscle_group_legs);
            case "shoulders":
                return context.getString(R.string.muscle_group_shoulders);
            case "abs":
                return context.getString(R.string.muscle_group_abs);
            default:
                return muscleGroup;
        }
    }
}
