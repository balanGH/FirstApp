package com.firstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomePage extends AppCompatActivity {

    TextView totalCostTextView;
    CheckBox checkboxEngineOil, checkboxTirePuncture, checkboxDent, checkboxCleaning, checkboxBrake;
    DatePicker datePicker;
    Button submitServiceFormBtn, navigateToMapBtn, selectLocationBtn, notifyUserBtn;
    ImageView tireImage;
    int totalAmount = 0; // To keep track of the total amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Initialize UI components
        totalCostTextView = findViewById(R.id.total_cost);
        checkboxEngineOil = findViewById(R.id.checkbox_engine_oil);
        checkboxTirePuncture = findViewById(R.id.checkbox_tire_puncture);
        checkboxDent = findViewById(R.id.checkbox_dent);
        checkboxCleaning = findViewById(R.id.checkbox_cleaning);
        checkboxBrake = findViewById(R.id.checkbox_brake);
        datePicker = findViewById(R.id.date_picker);
        submitServiceFormBtn = findViewById(R.id.submit_service_form);
        navigateToMapBtn = findViewById(R.id.nav_map);
        selectLocationBtn = findViewById(R.id.select_location_btn);
        notifyUserBtn = findViewById(R.id.notify_user);

        // Costs
        int engineOilCost = 500;
        int tirePunctureCost = 300;
        int dentCost = 1000;
        int cleaningCost = 200;
        int brakeCost = 800;

        // Listener to dynamically update the total cost
        View.OnClickListener checkboxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalAmount = 0;

                if (checkboxEngineOil.isChecked()) totalAmount += engineOilCost;
                if (checkboxTirePuncture.isChecked()) totalAmount += tirePunctureCost;
                if (checkboxDent.isChecked()) totalAmount += dentCost;
                if (checkboxCleaning.isChecked()) totalAmount += cleaningCost;
                if (checkboxBrake.isChecked()) totalAmount += brakeCost;

                // Update the total cost dynamically
                totalCostTextView.setText("Total: ₹" + totalAmount);
            }
        };

        // Attach the listener to all checkboxes
        checkboxEngineOil.setOnClickListener(checkboxListener);
        checkboxTirePuncture.setOnClickListener(checkboxListener);
        checkboxDent.setOnClickListener(checkboxListener);
        checkboxCleaning.setOnClickListener(checkboxListener);
        checkboxBrake.setOnClickListener(checkboxListener);

        // Navigate to Map button listener
        navigateToMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, Vmap.class);
                startActivity(intent);
            }
        });

        // Select Service Center button listener
        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, Map.class);
                startActivity(intent);
            }
        });

        // Notify User button listener
        notifyUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(createServiceSummary());
            }
        });

        // Submit button listener
        submitServiceFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitServiceForm();
            }
        });
    }

    // Submit the form and display details in a Toast
    void submitServiceForm() {
        String serviceSummary = createServiceSummary();

        // Display a toast with the details
        Toast.makeText(this, serviceSummary, Toast.LENGTH_LONG).show();
    }

    // Create a summary of selected services
    String createServiceSummary() {
        StringBuilder services = new StringBuilder("Selected Services:\n");

        if (checkboxEngineOil.isChecked()) {
            services.append("Engine Oil Change\n");
        }
        if (checkboxTirePuncture.isChecked()) {
            services.append("Tire Puncture\n");
        }
        if (checkboxDent.isChecked()) {
            services.append("Dent Repair\n");
        }
        if (checkboxCleaning.isChecked()) {
            services.append("Cleaning\n");
        }
        if (checkboxBrake.isChecked()) {
            services.append("Brake Service\n");
        }

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-based
        int year = datePicker.getYear();
        String selectedDate = day + "/" + month + "/" + year;

        services.append("Date: ").append(selectedDate).append("\n");
        services.append("Total Amount: ₹").append(totalAmount);

        return services.toString();
    }

    // Send a notification to the user
    void sendNotification(String message) {
        String channelId = "service_channel";
        String channelName = "Service Notifications";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("Service Summary")
                .setContentText(message)
                .setStyle(new Notification.BigTextStyle().bigText(message)) // To display the full message
                .setSmallIcon(R.drawable.ic_notification) // Replace with your drawable
                .build();

        manager.notify(1, notification);
    }
}
