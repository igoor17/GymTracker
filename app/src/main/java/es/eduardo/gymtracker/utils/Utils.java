package es.eduardo.gymtracker.utils;

public class Utils {

    public static String checkAdult(int age, float result) {
        String category;
        if (age >= 2 && age <= 19) {
            category = getAdultCategory(result);
        } else {
            category = getChildCategory(result);
        }
        return category;
    }

    private static String getAdultCategory(float result) {
        String category;
        if (result < 15) {
            category = "Severe Thinness";
        } else if (result >= 15.0 && result <= 16.0) {
            category = "Moderate Thinness";
        } else if (result > 16 && result <= 18.5) {
            category = "Mild Thinness";
        } else if (result > 18.5 && result <= 25) {
            category = "Normal";
        } else if (result > 25 && result <= 30) {
            category = "Overweight";
        } else if (result > 30 && result <= 35) {
            category = "Obese Class I";
        } else if (result > 35 && result <= 40) {
            category = "Obese Class II";
        } else {
            category = "Obese Class III";
        }
        return category;
    }

    private static String getChildCategory(float result) {
        String category;
        if (result < 15) {
            category = "very severely underweight";
        } else if (result >= 15.0 && result <= 16.0) {
            category = "severely underweight";
        } else if (result > 16 && result <= 18.5) {
            category = "underweight";
        } else if (result > 18.5 && result <= 25) {
            category = "normal (healthy weight)";
        } else if (result > 25 && result <= 30) {
            category = "overweight";
        } else if (result > 30 && result <= 35) {
            category = "moderately obese";
        } else if (result > 35 && result <= 40) {
            category = "severely obese";
        } else {
            category = "very severely obese";
        }
        return category;
    }

    public static String getSuggestions(float result) {
        String suggestion;
        if (result < 18.5) {
            suggestion = "A BMI of under 18.5 indicates that a person has insufficient weight, so they may need to put on some weight. They should ask a doctor or dietitian for advice.";
        } else if (result >= 18.5 && result < 25) {
            suggestion = "A BMI of 18.5–24.9 indicates that a person has a healthy weight for their height. By maintaining a healthy weight, they can lower their risk of developing serious health problems.";
        } else if (result >= 25 && result < 30) {
            suggestion = "A BMI of 25–29.9 indicates that a person is slightly overweight. A doctor may advise them to lose some weight for health reasons. They should talk with a doctor or dietitian for advice.";
        } else {
            suggestion = "A BMI of over 30 indicates that a person has obesity. Their health may be at risk if they do not lose weight. They should talk with a doctor or dietitian for advice.";
        }
        return suggestion;
    }
}