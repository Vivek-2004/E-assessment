package com.example.e_assessment_hub;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button nextButton;

    private DatabaseReference databaseReference;
    private List<Question> questionsList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        // Initialize views
        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        nextButton = findViewById(R.id.nextButton);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Questions");

        // Fetch questions from Firebase
        fetchQuestions();

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            loadNextQuestion();
        });
    }

    private void fetchQuestions() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    questionsList.add(question);
                }
                loadNextQuestion();
            } else {
                Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            Question question = questionsList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestionText());
            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());
            optionsRadioGroup.clearCheck();
        } else {
            Toast.makeText(this, "Exam Finished! Your score: " + score, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedOption = findViewById(selectedId);
            Question currentQuestion = questionsList.get(currentQuestionIndex);
            if (selectedOption.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                score++;
            }
        } else {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
        }
        currentQuestionIndex++;
    }
}
