package com.example.smartpantrymanager;

/** One ingredient stored in the pantry. */
public class PantryItem {
    public int id;
    public String name;
    public double quantity;
    public String unit;
    /** Optional, stored as yyyy-MM-dd. Null or empty when not set. */
    public String expiryDate;

    public PantryItem(int id, String name, double quantity,
                      String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}