package com.example.e_assessment_hub;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {
    private static final String TAG = "ExamActivity";

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button nextButton;
    private ProgressBar loadingProgressBar; // Declare the ProgressBar

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
        loadingProgressBar = findViewById(R.id.loadingProgressBar); // Initialize the ProgressBar

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        // Show loading indicator before fetching questions
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Fetch questions from Firebase
        fetchQuestions();

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (!checkAnswer()) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            currentQuestionIndex++;
            loadNextQuestion();
        });
    }

    private void fetchQuestions() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d(TAG, "Data fetched successfully!");
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    try {
                        Question question = snapshot.getValue(Question.class);
                        if (question != null) {
                            questionsList.add(question);
                            Log.d(TAG, "Question added: " + question.getQuestionText());
                        } else {
                            Log.e(TAG, "Null question object encountered");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question: " + e.getMessage());
                    }
                }
                if (questionsList.isEmpty()) {
                    Log.e(TAG, "No questions fetched. Check your Firebase data or mapping.");
                    Toast.makeText(this, "No questions found in the database.", Toast.LENGTH_SHORT).show();
                } else {
                    loadNextQuestion();
                }
            } else {
                Log.e(TAG, "Failed to fetch data: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Toast.makeText(this, "Failed to load questions. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }

            // Hide loading indicator after fetching questions
            loadingProgressBar.setVisibility(View.GONE);
        });
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            Question question = questionsList.get(currentQuestionIndex);
            Log.d(TAG, "Loading question: " + question.getQuestionText());
            questionTextView.setText(question.getQuestionText());
            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());
            optionsRadioGroup.clearCheck(); // Clear previous selection
        } else {
            Toast.makeText(this, "Exam Finished! Your score: " + score, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedOption = findViewById(selectedId);
            Question currentQuestion = questionsList.get(currentQuestionIndex);
            if (selectedOption.getText().toString().equals(currentQuestion.getCorrectAnswer())) {
                score++;
            }
            return true;
        }
        return false; // No answer selected
    }
}
