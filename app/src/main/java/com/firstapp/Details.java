package com.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Car Brand Spinner
        Spinner carBrandSpinner = findViewById(R.id.carBrandSpinner);
        String[] carBrands = getResources().getStringArray(R.array.car_brands);
        int[] carBrandImages = {R.drawable.toyota, R.drawable.ford, R.drawable.honda, R.drawable.bmw}; // Replace with actual image resources
        CustomSpinnerAdapter brandAdapter = new CustomSpinnerAdapter(this, carBrands, carBrandImages);
        carBrandSpinner.setAdapter(brandAdapter);

        // Car Model Spinner
        Spinner carModelSpinner = findViewById(R.id.carModelSpinner);
        String[] carModels = getResources().getStringArray(R.array.car_models);
        int[] carModelImages = {R.drawable.corolla, R.drawable.focus, R.drawable.civic, R.drawable.series3}; // Replace with actual image resources
        CustomSpinnerAdapter modelAdapter = new CustomSpinnerAdapter(this, carModels, carModelImages);
        carModelSpinner.setAdapter(modelAdapter);

        // Fuel Mode Spinner
        Spinner fuelModeSpinner = findViewById(R.id.fuelModeSpinner);
        String[] fuelModes = getResources().getStringArray(R.array.fuel_modes);
        int[] fuelModeImages = {R.drawable.petrol, R.drawable.diesel, R.drawable.electric, R.drawable.hybrid}; // Replace with actual image resources
        CustomSpinnerAdapter fuelAdapter = new CustomSpinnerAdapter(this, fuelModes, fuelModeImages);
        fuelModeSpinner.setAdapter(fuelAdapter);

        // Initialize the Submit Button
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String selectedBrand = carBrandSpinner.getSelectedItem().toString();
            String selectedModel = carModelSpinner.getSelectedItem().toString();
            String selectedFuelMode = fuelModeSpinner.getSelectedItem().toString();

            // Display the selected values
            Toast.makeText(this, "Car Brand: " + selectedBrand + "\nModel: " + selectedModel + "\nFuel Mode: " + selectedFuelMode, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Details.this, WelcomePage.class);
            startActivity(intent);
        });

        // Navigation Bar Buttons
        Button navHomeBtn = findViewById(R.id.nav_home);
        Button navMapBtn = findViewById(R.id.nav_map);
        Button navDetailsBtn = findViewById(R.id.nav_details);

        navHomeBtn.setOnClickListener(v -> startActivity(new Intent(Details.this, WelcomePage.class)));
        navMapBtn.setOnClickListener(v -> startActivity(new Intent(Details.this, Vmap.class)));
        navDetailsBtn.setOnClickListener(v -> startActivity(new Intent(Details.this, Details.class)));
    }
}