package com.example.bmikalkulacka;

/**
 * Čistá aplikační logika pro výpočet BMI (bez závislosti na Android UI),
 * aby ji bylo možné snadno otestovat.
 */
public final class BmiCalculator {

    /** Horní hranice (bez rovnosti) jednotlivých kategorií podle WHO. */
    private static final double[] CATEGORY_LIMITS = {18.5, 25.0, 30.0, 35.0, 40.0};

    private BmiCalculator() {
    }

    /**
     * Vypočítá BMI = hmotnost v kg / (výška v m)^2.
     *
     * @param heightCm výška v centimetrech (musí být kladná)
     * @param weightKg hmotnost v kilogramech (musí být kladná)
     * @return hodnota BMI
     */
    public static double calculate(double heightCm, double weightKg) {
        if (heightCm <= 0 || weightKg <= 0) {
            throw new IllegalArgumentException("Výška i hmotnost musí být kladné.");
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Vrátí index kategorie BMI odpovídající pořadí položek
     * v poli {@code R.array.bmi_categories}.
     */
    public static int categoryIndex(double bmi) {
        for (int i = 0; i < CATEGORY_LIMITS.length; i++) {
            if (bmi < CATEGORY_LIMITS[i]) {
                return i;
            }
        }
        return CATEGORY_LIMITS.length;
    }
}
