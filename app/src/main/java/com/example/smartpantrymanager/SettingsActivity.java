
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(0xFFF7F8F2);

        scrollView.addView(layout);
        setContentView(scrollView);

        TextView title = new TextView(this);
        title.setText("Settings & Profile");
        title.setTextSize(28);
        title.setTextColor(0xFF245C45);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 28);
        layout.addView(title);

        addHeading(layout, "About the App");
        addBody(layout, "Smart Pantry Manager");

        addHeading(layout, "Purpose");
        addBody(
                layout,
                "Manage pantry ingredients, record quantities and units, "
                        + "and discover recipes that can be prepared with the "
                        + "ingredients available in your pantry."
        );

        addHeading(layout, "Data Storage");
        addBody(
                layout,
                "Your pantry and recipe information is stored locally "
                        + "using an SQLite database on this device."
        );

        addHeading(layout, "Recipe Suggestions");
        addBody(
                layout,
                "Recipes are suggested only when all required ingredients "
                        + "and sufficient quantities are available."
        );

        Button backButton = new Button(this);
        backButton.setText("Back to Home");
        backButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF245C45));
        backButton.setTextColor(0xFFFFFFFF);
        backButton.setOnClickListener(v -> finish());

        layout.addView(backButton);
    }

    private void addHeading(LinearLayout layout, String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextSize(20);
        heading.setTextColor(0xFF245C45);
        heading.setPadding(0, 20, 0, 8);
        layout.addView(heading);
    }

    private void addBody(LinearLayout layout, String text) {
        TextView body = new TextView(this);
        body.setText(text);
        body.setTextSize(16);
        body.setTextColor(0xFF222222);
        body.setLineSpacing(4, 1.0f);
        body.setPadding(0, 0, 0, 8);
        layout.addView(body);
    }
}
