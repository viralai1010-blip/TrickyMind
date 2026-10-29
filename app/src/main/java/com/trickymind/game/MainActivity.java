package com.trickymind.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // هذا مؤقتاً حتى نضيفو الأسئلة من بعد
                Toast.makeText(MainActivity.this, "Game logic coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
