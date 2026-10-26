package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public final class MatchingEngine {

    private MatchingEngine() {
    }

    public static List<Recipe> getSuggestedRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantry) {

        Map<String, List<PantryItem>> byName = new HashMap<>();

        for (PantryItem item : pantry) {
            String key = normalizeName(item.name);

            if (key.isEmpty() || item.quantity <= 0) {
                continue;
            }

            List<PantryItem> list = byName.get(key);

            if (list == null) {
                list = new ArrayList<>();
                byName.put(key, list);
            }

            list.add(item);
        }

        List<Recipe> result = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (recipe != null && canMake(recipe.ingredients, byName)) {
                result.add(recipe);
            }
        }

        return result;
    }

    private static boolean canMake(
            String ingredients,
            Map<String, List<PantryItem>> pantry) {

        if (ingredients == null || ingredients.trim().isEmpty()) {
            return false;
        }

        boolean foundRequirement = false;

        for (String rawLine : ingredients.split("[;\\r\\n]+")) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(":", 2);
            String name = normalizeName(parts[0]);

            if (name.isEmpty()) {
                return false;
            }

            double needed = 1;
            String unitText = "";

            if (parts.length == 2) {
                String[] amount = parts[1].trim().split("\\s+", 2);

                try {
                    needed = Double.parseDouble(amount[0]);
                } catch (NumberFormatException e) {
                    return false;
                }

                if (amount.length > 1) {
                    unitText = amount[1];
                }
            }

            if (needed <= 0) {
                return false;
            }

            List<PantryItem> available = pantry.get(name);

            if (available == null) {
                return false;
            }

            Unit requiredUnit = parseUnit(unitText);
            double requiredBase = needed * requiredUnit.factor;
            double availableBase = 0;

            for (PantryItem item : available) {
                Unit pantryUnit = parseUnit(item.unit);

                if (pantryUnit.type.equals(requiredUnit.type)) {
                    availableBase += item.quantity * pantryUnit.factor;
                }
            }

            if (availableBase + 0.000001 < requiredBase) {
                return false;
            }

            foundRequirement = true;
        }

        return foundRequirement;
    }

    private static String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        return name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private static Unit parseUnit(String unit) {
        String value = unit == null
                ? ""
                : unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return new Unit("weight", 1000);

            case "g":
            case "gram":
            case "grams":
                return new Unit("weight", 1);

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return new Unit("volume", 1000);

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return new Unit("volume", 1);

            case "dozen":
            case "dozens":
                return new Unit("count", 12);

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
            case "item":
            case "items":
            case "":
                return new Unit("count", 1);

            default:
                return new Unit("other:" + value, 1);
        }
    }

    private static class Unit {
        String type;
        double factor;

        Unit(String type, double factor) {
            this.type = type;
            this.factor = factor;
        }
    }
}