package com.trickymind.game;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    TextView questionText, scoreText;
    Button option1, option2, option3, option4;
    ImageButton btnSpeak;
    
    TextToSpeech tts;
    boolean isTtsReady = false;
    List<Question> questionList;
    int currentQuestionIndex = 0;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnSpeak = findViewById(R.id.btnSpeak);

        // حماية النطق باش ما يطيحش التطبيق إلا كان مشكل فالهاتف
        try {
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    isTtsReady = true;
                }
            });
        } catch (Exception e) {
            isTtsReady = false;
        }

        loadQuestionsFromJson();

        btnSpeak.setOnClickListener(v -> {
            if (isTtsReady && questionList != null && currentQuestionIndex < questionList.size()) {
                tts.speak(questionList.get(currentQuestionIndex).question, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(this, "Audio not available", Toast.LENGTH_SHORT).show();
            }
        });

        option1.setOnClickListener(v -> checkAnswer(0));
        option2.setOnClickListener(v -> checkAnswer(1));
        option3.setOnClickListener(v -> checkAnswer(2));
        option4.setOnClickListener(v -> checkAnswer(3));

        loadQuestion();
    }

    private void loadQuestionsFromJson() {
        try {
            InputStream is = getAssets().open("questions.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<Question>>() {}.getType();
            questionList = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading questions", Toast.LENGTH_SHORT).show();
        }
    }

    void loadQuestion() {
        if (questionList != null && currentQuestionIndex < questionList.size()) {
            Question q = questionList.get(currentQuestionIndex);
            if (q != null) {
                questionText.setText(q.question);
                option1.setText(q.options[0]);
                option2.setText(q.options[1]);
                option3.setText(q.options[2]);
                option4.setText(q.options[3]);
                scoreText.setText("Score: " + score);
            }
        } else {
            Toast.makeText(this, "Game Over! Score: " + score, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    void checkAnswer(int selectedIdx) {
        if (questionList == null || currentQuestionIndex >= questionList.size()) return;
        Question q = questionList.get(currentQuestionIndex);
        
        if (selectedIdx == q.answer) {
            score += 10;
            Toast.makeText(this, "Correct! +10 Points\n" + q.explanation, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Wrong! Correct answer: " + q.options[q.answer] + "\n" + q.explanation, Toast.LENGTH_LONG).show();
        }
        
        currentQuestionIndex++;
        new android.os.Handler().postDelayed(this::loadQuestion, 2000);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
