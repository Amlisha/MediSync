package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ChatbotActivity extends AppCompatActivity {
    private LinearLayout chatContainer;
    private EditText etMessage;
    private ScrollView scrollChat;
    private TextToSpeech textToSpeech;
    private boolean ttsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {

                int result = textToSpeech.setLanguage(Locale.getDefault());

                // Try English if the default language is unavailable
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    result = textToSpeech.setLanguage(Locale.US);
                }

                ttsReady = result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED;

                if (ttsReady) {
                    Toast.makeText(
                            this,
                            "MediBot voice is ready",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            this,
                            "Voice language unavailable. Check TTS settings.",
                            Toast.LENGTH_LONG
                    ).show();
                }

            } else {
                Toast.makeText(
                        this,
                        "Text-to-speech could not initialize",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        chatContainer = findViewById(R.id.chatContainer);
        etMessage = findViewById(R.id.etMessage);
        scrollChat = findViewById(R.id.scrollChat);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnVoice = findViewById(R.id.btnVoice);

        btnVoice.setOnClickListener(v -> startVoiceInput());

        addMessage(
                "Hello! I'm MediBot, your MediSync health assistant. " +
                        "You can ask me about healthy habits, medicines, " +
                        "appointments, or using MediSync.",
                false
        );

        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        findViewById(R.id.btnHealthyHabits).setOnClickListener(v ->
                answerQuestion("healthy habits"));

        findViewById(R.id.btnMedicineHelp).setOnClickListener(v ->
                answerQuestion("missed medicine"));

        findViewById(R.id.btnAppointmentHelp).setOnClickListener(v ->
                answerQuestion("doctor appointment"));

        findViewById(R.id.btnAppHelp).setOnClickListener(v ->
                answerQuestion("how to use medisync"));
    }

    private void startVoiceInput() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your question to MediBot"
        );

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Speech recognition is not available on this device.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    private void speakAnswer(String message) {
        if (textToSpeech != null && ttsReady) {
            textToSpeech.speak(
                    message,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "MediBotReply"
            );
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK
                && data != null) {

            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
            );

            if (results != null && !results.isEmpty()) {
                etMessage.setText(results.get(0));
                etMessage.setSelection(etMessage.length());

                // Automatically send the spoken question
                sendMessage();
            }
        }
    }
    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            etMessage.setError("Please enter a question");
            return;
        }

        addMessage(message, true);
        etMessage.setText("");
        answerQuestion(message);
    }


    private void answerQuestion(String question) {
        String q = question.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9' ]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String answer;

        // Greetings
        if (containsAny(q, "hello", "hi", "hey",
                "good morning", "good afternoon", "good evening")) {
            answer = "Hello! I'm MediBot. How can I help you today? "
                    + "You can ask me about healthy habits, medicines, "
                    + "appointments, medical reports, or MediSync.";
        }

        // Emergency questions must be checked before general symptoms
        else if (containsAny(q, "emergency", "urgent",
                "can't breathe", "cannot breathe", "chest pain",
                "difficulty breathing", "severe bleeding",
                "unconscious")) {
            answer = "This may be a medical emergency. Contact local "
                    + "emergency services immediately. In India, you "
                    + "can call 112. Do not wait for a chatbot response.";
        }

        // Healthy habits
        else if (containsAny(q, "healthy habit", "healthy lifestyle",
                "stay healthy", "health tips", "fitness", "exercise",
                "workout", "diet", "food", "eat healthy")) {
            answer = "To support your health, eat balanced meals, "
                    + "drink enough water, stay physically active, "
                    + "sleep regularly, and take breaks to relax. "
                    + "Choose habits that suit your needs.";
        }

        // Water
        else if (containsAny(q, "water", "hydration", "dehydrated",
                "drink water")) {
            answer = "Drink water regularly throughout the day. "
                    + "Your needs can vary with activity, weather, "
                    + "and health conditions. Follow your healthcare "
                    + "professional's advice if you have fluid restrictions.";
        }

        // Sleep
        else if (containsAny(q, "sleep", "insomnia", "can't sleep",
                "cannot sleep", "sleeping", "tired all the time")) {
            answer = "Try keeping a regular sleep schedule, reducing "
                    + "screen time before bed, and keeping your room "
                    + "comfortable and quiet. If sleep problems continue, "
                    + "consider speaking with a healthcare professional.";
        }

        // Missed medicine
        else if (containsAny(q, "missed medicine", "miss medicine",
                "forgot medicine", "forgot my medicine",
                "forgot my tablet", "missed my tablet",
                "missed a dose", "forgot a dose", "medicine reminder",
                "medication reminder")) {
            answer = "If you miss a dose, check the instructions "
                    + "for your specific medicine or contact your "
                    + "pharmacist. Do not double the next dose unless "
                    + "your healthcare professional advises you to. "
                    + "You can manage medicine records and reminders "
                    + "in MediSync.";
        }

        // Medicines in general
        else if (containsAny(q, "medicine", "medication", "tablet",
                "capsule", "dosage", "side effect")) {
            answer = "You can use MediSync to organise your medicine "
                    + "details and reminders. Take medicines according "
                    + "to your prescription. Ask your doctor or pharmacist "
                    + "before changing a dose or stopping a medicine.";
        }

        // Doctor appointments
        else if (containsAny(q, "appointment", "doctor visit",
                "doctor appointment", "hospital visit",
                "book a doctor", "upcoming visit")) {
            answer = "Before a doctor's appointment, prepare your "
                    + "questions, note your symptoms, and bring your "
                    + "medicine list and relevant reports. You can use "
                    + "MediSync to record appointment details and reminders.";
        }

        // Medical reports
        else if (containsAny(q, "medical report", "lost report",
                "health report", "test report", "lab report",
                "blood report", "report missing")) {
            answer = "You can record medical report details in MediSync. "
                    + "Keep the original report safe. If a report is "
                    + "missing, contact the clinic or laboratory that "
                    + "issued it.";
        }

        // Allergies
        else if (containsAny(q, "allergy", "allergies",
                "allergic reaction")) {
            answer = "Keep a record of known allergies and tell your "
                    + "doctor or pharmacist about them. If you develop "
                    + "trouble breathing or swelling of your tongue "
                    + "or throat, seek emergency medical help immediately.";
        }

        // General symptoms
        else if (containsAny(q, "fever", "headache", "stomach pain",
                "stomach ache", "cough", "cold", "symptom",
                "sick", "illness", "pain", "vomiting", "dizziness")) {
            answer = "I can share general health information, but I "
                    + "cannot diagnose an illness. Rest and monitor "
                    + "your symptoms where appropriate. Contact a "
                    + "healthcare professional if symptoms are severe, "
                    + "persistent, or getting worse.";
        }

        // Login and account
        else if (containsAny(q, "password", "login", "log in",
                "register", "account", "sign in", "forgot password")) {
            answer = "Use the MediSync login screen to sign in, or "
                    + "choose Register to create an account. Keep your "
                    + "password private. If you cannot access your "
                    + "account, use the available account options.";
        }

        // MediSync features
        else if (containsAny(q, "how to use medisync",
                "what is medisync", "what can medisync do",
                "medisync app", "features", "what does this app do",
                "health profile", "emergency contact",
                "problem resolver")) {
            answer = "MediSync helps you organise your health profile, "
                    + "medicine records, medical reports, emergency "
                    + "contacts, and appointment reminders. You can also "
                    + "use the Problem Resolver for general guidance.";
        }

        // Thanks
        else if (containsAny(q, "thank you", "thanks",
                "thank u")) {
            answer = "You're welcome! I'm happy to help you use MediSync "
                    + "and learn about general healthy habits.";
        }

        // Fallback
        else {
            answer = "I'm still learning to recognise that question. "
                    + "Try asking me about healthy habits, water, sleep, "
                    + "medicines, missed doses, appointments, reports, "
                    + "allergies, symptoms, or MediSync features. "
                    + "For personal medical advice, consult a healthcare "
                    + "professional.";
        }

        addMessage(answer, false);
        speakAnswer(answer);
    }



    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }


    private void addMessage(String message, boolean isUser) {
        TextView bubble = new TextView(this);

        bubble.setText(message);
        bubble.setTextSize(15);
        bubble.setTextColor(
                android.graphics.Color.parseColor("#205C56")
        );
        bubble.setPadding(14, 12, 14, 12);
        bubble.setBackgroundColor(
                android.graphics.Color.parseColor(
                        isUser ? "#CDEDE7" : "#FFFFFF"
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        (int) (getResources().getDisplayMetrics()
                                .widthPixels * 0.78),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(4, 6, 4, 6);
        params.gravity = isUser ? Gravity.END : Gravity.START;

        bubble.setLayoutParams(params);
        chatContainer.addView(bubble);

        scrollChat.post(() ->
                scrollChat.fullScroll(View.FOCUS_DOWN)
        );
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
