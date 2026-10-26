package com.example.smartpantrymanager;

/** A recipe from the local database. */
public class Recipe {
    public int id;
    public String name;
    /** One "Name: amount unit" requirement per line. */
    public String ingredients;
    public String steps;
    public boolean favorite;

    public Recipe(int id, String name, String ingredients,
                  String steps, boolean favorite) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.favorite = favorite;
    }
}