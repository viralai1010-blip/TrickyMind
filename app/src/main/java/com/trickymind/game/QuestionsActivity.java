package com.trickymind.game;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        TextView display = findViewById(R.id.questionsDisplay);
        StringBuilder builder = new StringBuilder();

        try {
            InputStream is = getAssets().open("questions.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<List<Question>>() {}.getType();
            List<Question> questionList = new Gson().fromJson(reader, listType);

            if (questionList != null) {
                for (int i = 0; i < questionList.size(); i++) {
                    Question q = questionList.get(i);
                    builder.append(i + 1).append(". ").append(q.question).append("\n");
                    builder.append("Answer: ").append(q.options[q.answer]).append("\n");
                    builder.append("Explanation: ").append(q.explanation).append("\n\n");
                    builder.append("--------------------\n\n");
                }
            }
        } catch (Exception e) {
            builder.append("Error loading questions.");
        }

        display.setText(builder.toString());
    }
}
