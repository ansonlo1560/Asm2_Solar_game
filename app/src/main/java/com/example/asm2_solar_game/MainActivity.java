package com.example.asm2_solar_game;

import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.content.ContentResolver;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Color;
import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;

public class MainActivity extends AppCompatActivity {
    private TextView brightness;
    private View colorBlock;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator(); // 用於顏色插值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        brightness = findViewById(R.id.brightness);
        colorBlock = findViewById(R.id.colorBlock);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.brightness), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                int brightnessValue = getScreenBrightness(MainActivity.this);
                System.out.println("Brightness retrieved in onCreate: " + brightnessValue);
                brightness.setText("Brightness: " + brightnessValue);

                // 更新顏色和大小
                updateColorAndSizeBasedOnBrightness(brightnessValue);

                handler.postDelayed(this, 100);
            }
        };

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
        return brightness;
    }

    // 根據亮度值更新顏色和大小
    private void updateColorAndSizeBasedOnBrightness(int brightnessValue) {
        // 顏色漸變
        int startColor = Color.parseColor("#FF0000"); // 紅色 (亮度0)
        int endColor = Color.parseColor("#00FF00");   // 綠色 (亮度255)
        float fraction = brightnessValue / 255.0f;
        int interpolatedColor = (int) argbEvaluator.evaluate(fraction, startColor, endColor);
        colorBlock.setBackgroundTintList(ColorStateList.valueOf(interpolatedColor)); // 使用Tint改變顏色

        // 大小變化
        int minWidth = 50;
        int maxWidth = 200;
        int newWidth = minWidth + (int) ((maxWidth - minWidth) * fraction);
        ViewGroup.LayoutParams params = colorBlock.getLayoutParams();
        params.width = dpToPx(newWidth);
        colorBlock.setLayoutParams(params);
    }

    // dp轉px輔助方法
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}