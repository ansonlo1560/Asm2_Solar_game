package com.example.asm2_solar_game;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.TextView;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView brightness;
    private final Handler handler = new Handler();
    private Runnable runnable = () -> {

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        brightness = findViewById(R.id.brightness);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.brightness), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                // Call the getScreenBrightness method
                int brightnessValue = getScreenBrightness(MainActivity.this);
                // Optionally, do something with the brightness value
                System.out.println("Brightness retrieved in onCreate: " + brightnessValue);

                // Update the TextView with the brightness value
                brightness.setText("Brightness: " + brightnessValue);
                // 70% -> do show XXX

                // Re-post the Runnable to run again after 100ms
                handler.postDelayed(this, 100);
            }
        };

        // Start the loop
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private int getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 125;
        int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defVal);
        System.out.println("The brightness now +: " + brightness);
        return brightness;
    }
}