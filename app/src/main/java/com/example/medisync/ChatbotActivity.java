package com.example.medisync;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
        String[] suggestions;

        // Emergency guidance must be checked first.
        if (containsAny(q, "can't breathe", "cannot breathe",
                "difficulty breathing", "chest pain", "severe bleeding",
                "unconscious", "stroke symptoms", "overdose",
                "medical emergency", "urgent medical help",
                "this is an emergency")) {

            answer = "This may be a medical emergency.\n\n"
                    + "What to do:\n"
                    + "1. Call your local emergency number immediately. In India, call 112.\n"
                    + "2. Follow the emergency operator's instructions.\n"
                    + "3. Do not delay urgent care to use MediBot.";

            suggestions = new String[]{
                    "How to use emergency contacts"
            };
        }

        // Greetings: exact matching prevents 'hi' matching inside 'this'.
        else if (q.equals("hi") || q.equals("hello")
                || q.equals("hey") || q.equals("good morning")
                || q.equals("good afternoon") || q.equals("good evening")) {

            answer = "Hello! I'm MediBot, your offline health information assistant.\n\n"
                    + "I can help with general wellness information and explain "
                    + "how to use MediSync. Choose a suggestion below or ask me "
                    + "a question in your own words.";

            suggestions = new String[]{
                    "Healthy habits",
                    "Manage stress",
                    "Medicine safety",
                    "How to use MediSync"
            };
        }
        // Healthy habits
        else if (containsAny(q, "healthy habits", "stay healthy",
                "health tips", "healthy lifestyle", "daily health tips")) {

            answer = formatAnswer(
                    "Small, consistent habits can support your general health "
                            + "and wellbeing.",
                    "Healthy habits to follow:",
                    new String[]{
                            "Eat a varied and balanced diet.",
                            "Drink water regularly, according to your needs.",
                            "Get enough sleep and keep a regular sleep schedule.",
                            "Include suitable physical activity in your routine.",
                            "Maintain personal hygiene and make time to relax."
                    },
                    "Start with small changes that suit your daily routine. "
                            + "Seek professional advice for personal health concerns."
            );

            suggestions = new String[]{
                    "What is a balanced diet?",
                    "How can I improve my sleep?",
                    "Manage stress"
            };
        }
        // Balanced diet
        else if (containsAny(q, "balanced diet", "healthy diet",
                "nutrition", "eat healthy", "healthy food", "diet plan")) {

            answer = formatAnswer(
                    "A balanced diet provides nutrients your body needs for energy, "
                            + "growth and general health.",
                    "Healthy eating habits:",
                    new String[]{
                            "Include vegetables and fruits in your meals.",
                            "Choose whole grains such as oats, whole-wheat roti or brown rice.",
                            "Include protein foods such as dal, beans, eggs or other suitable options.",
                            "Drink water regularly and limit excess salt, sugar and highly processed foods."
                    },
                    "Your nutritional needs can vary with age, activity and health. "
                            + "Ask a qualified professional for advice tailored to you."
            );

            suggestions = new String[]{
                    "Foods rich in iron",
                    "Sources of protein",
                    "How much water should I drink?"
            };
        }

        // Iron-rich foods
        else if (containsAny(q, "iron rich", "foods rich in iron",
                "iron deficiency foods", "iron foods")) {

            answer = formatAnswer(
                    "Iron helps your body make haemoglobin, which carries oxygen in the blood.",
                    "Foods that contain iron:",
                    new String[]{
                            "Lentils, beans and chickpeas.",
                            "Leafy green vegetables.",
                            "Meat, where included in your diet.",
                            "Iron-fortified cereals and other fortified foods."
                    },
                    "Vitamin C foods, such as citrus fruits, can help your body absorb "
                            + "iron from plant foods. If you suspect iron deficiency, "
                            + "seek medical advice rather than starting supplements yourself."
            );

            suggestions = new String[]{
                    "Sources of protein",
                    "What is a balanced diet?",
                    "When should I see a doctor?"
            };
        }

        // Protein
        else if (containsAny(q, "protein", "protein rich foods",
                "sources of protein")) {

            answer = formatAnswer(
                    "Protein helps the body maintain and repair tissues.",
                    "Common protein sources:",
                    new String[]{
                            "Dal, beans, chickpeas and soy foods.",
                            "Milk, curd and paneer.",
                            "Eggs, fish and chicken, if included in your diet.",
                            "Nuts and seeds."
                    },
                    "Choose foods that suit your dietary needs and any advice "
                            + "you have received from your healthcare professional."
            );

            suggestions = new String[]{
                    "What is a balanced diet?",
                    "Foods rich in iron",
                    "Healthy habits"
            };
        }

        // Hydration

        else if (containsAny(q, "drink water", "how much water",
                "hydration", "dehydrated", "water intake",
                "signs of dehydration", "am i dehydrated")) {


            answer = formatAnswer(
                    "Water helps regulate body temperature and supports normal body functions.",
                    "Helpful habits:",
                    new String[]{
                            "Drink water regularly throughout the day.",
                            "Drink more when heat or physical activity increases your needs.",
                            "Remember that foods and other drinks also contribute to fluid intake."
                    },
                    "Fluid needs vary. If you have been told to restrict fluids because "
                            + "of a medical condition, follow your clinician's advice."
            );

            suggestions = new String[]{
                    "Healthy habits",
                    "Foods rich in iron",
                    "How can I improve my sleep?"
            };
        }

        // Sleep
        else if (containsAny(q, "sleep", "insomnia", "can't sleep",
                "cannot sleep", "sleeping problem", "sleep schedule",
                "sleep better")) {

            answer = formatAnswer(
                    "Good sleep supports concentration, mood and overall health.",
                    "Habits that may help:",
                    new String[]{
                            "Keep a consistent bedtime and wake-up time.",
                            "Reduce screen use before bedtime.",
                            "Avoid caffeine close to bedtime.",
                            "Keep your bedroom quiet, dark and comfortable."
                    },
                    "If sleep problems persist or affect daily activities, "
                            + "consider consulting a healthcare professional."
            );

            suggestions = new String[]{
                    "Manage stress",
                    "Healthy habits",
                    "How much water should I drink?"
            };
        }

        // Mental wellness
        else if (containsAny(q, "stress", "anxiety", "feeling anxious",
                "feeling overwhelmed", "relax", "mental health",
                "feeling sad", "sad all the time")) {

            answer = formatAnswer(
                    "Stress can affect how you feel and manage everyday activities. "
                            + "Small steps may help you cope.",
                    "Things you can try:",
                    new String[]{
                            "Take a short break and breathe slowly.",
                            "Break difficult tasks into smaller steps.",
                            "Make time for rest and suitable physical activity.",
                            "Talk with someone you trust about how you feel."
                    },
                    "If distress continues or interferes with daily life, seek "
                            + "professional support. If you feel in immediate danger, "
                            + "contact emergency services."
            );

            suggestions = new String[]{
                    "How can I improve my sleep?",
                    "Healthy habits",
                    "When should I see a doctor?"
            };
        }

        // Exercise
        else if (containsAny(q, "exercise", "workout",
                "physical activity", "fitness", "stay active",
                "healthy weight", "weight management", "lose weight",
                "weight loss", "gain weight")) {


            answer = formatAnswer(
                    "Regular physical activity can support heart health, strength and mood.",
                    "Getting started:",
                    new String[]{
                            "Choose an activity you enjoy, such as walking or cycling.",
                            "Start at a comfortable level and increase gradually.",
                            "Include suitable movement breaks during long periods of sitting.",
                            "Allow time for rest and recovery."
                    },
                    "Choose activity appropriate for your abilities. Stop and seek "
                            + "medical help if you experience chest pain, fainting "
                            + "or severe breathlessness."
            );

            suggestions = new String[]{
                    "What is a balanced diet?",
                    "How can I improve my sleep?",
                    "Manage stress"
            };
        }

        // Hygiene

        else if (containsAny(q, "prevent infection", "avoid infection",
                "hand washing", "handwashing", "wash hands",
                "how to wash hands", "personal hygiene",
                "prevent germs", "how to prevent infections")) {


            answer = formatAnswer(
                    "Good hygiene can reduce the spread of many infections.",
                    "Everyday prevention:",
                    new String[]{
                            "Wash your hands with soap and water when appropriate.",
                            "Handle food safely and drink safe water.",
                            "Follow recommended vaccination advice.",
                            "Cover coughs and sneezes and avoid spreading illness to others."
                    },
                    "Seek medical advice if you have concerns about an infection "
                            + "or develop symptoms that persist or worsen."
            );

            suggestions = new String[]{
                    "Healthy habits",
                    "When should I see a doctor?",
                    "First aid for a minor burn"
            };
        }

        // Women's health
        else if (containsAny(q, "menstrual hygiene", "period hygiene",
                "period care", "menstrual health")) {

            answer = formatAnswer(
                    "Menstrual hygiene helps keep you comfortable and supports "
                            + "your health during your period.",
                    "Helpful practices:",
                    new String[]{
                            "Use clean menstrual products suitable for you.",
                            "Change products regularly according to their instructions.",
                            "Wash your hands before and after changing products.",
                            "Clean the external genital area gently."
                    },
                    "Seek medical advice for unusually heavy bleeding, severe pain "
                            + "or other concerning changes."
            );

            suggestions = new String[]{
                    "Healthy habits",
                    "When should I see a doctor?",
                    "Manage stress"
            };
        }

        // Minor thermal burns
        else if (containsAny(q, "minor burn", "small burn",
                "burn first aid", "burned my hand", "burnt my hand")) {

            answer = formatAnswer(
                    "For a minor thermal burn, prompt cooling can help limit injury.",
                    "Immediate first aid:",
                    new String[]{
                            "Cool the burn under cool running water for 20 minutes.",
                            "Remove nearby jewellery or tight items if they are not stuck to the skin.",
                            "Do not apply ice, toothpaste, butter or other home remedies."
                    },
                    "Seek urgent medical care for large, deep or serious burns, "
                            + "chemical or electrical burns, or burns affecting "
                            + "the face or breathing passages. Do not delay care "
                            + "for a serious burn."
            );

            suggestions = new String[]{
                    "When should I see a doctor?",
                    "How to use emergency contacts"
            };
        }

        // Missed dose
        else if (containsAny(q, "missed medicine", "miss medicine",
                "forgot medicine", "forgot my medicine",
                "forgot my tablet", "missed my tablet",
                "missed a dose", "miss a dose", "missed dose",
                "forgot a dose", "medicine reminder",
                "medication reminder")) {

            answer = formatAnswer(
                    "What to do after a missed dose depends on the specific medicine "
                            + "and how late the dose is.",
                    "Safer next steps:",
                    new String[]{
                            "Check the patient leaflet or instructions for your medicine.",
                            "Ask your pharmacist or prescriber if you are unsure.",
                            "Do not take a double dose unless your healthcare professional "
                                    + "specifically instructs you to.",
                            "Use MediSync reminders to help organise future doses."
                    },
                    "MediBot cannot determine the correct missed-dose plan for your medicine."
            );

            suggestions = new String[]{
                    "Medicine safety",
                    "How do I manage reminders?",
                    "How to use MediSync"
            };
        }

        // Medicine safety
        else if (containsAny(q, "medicine", "medication", "tablet",
                "capsule", "dosage", "side effect", "drug interaction")) {

            answer = formatAnswer(
                    "Medicines can have benefits and risks. Use them carefully "
                            + "and follow reliable instructions.",
                    "Medicine safety tips:",
                    new String[]{
                            "Follow the instructions from your prescriber or pharmacist.",
                            "Check the label before taking a medicine.",
                            "Tell your healthcare professional about other medicines "
                                    + "and supplements you use.",
                            "Ask a pharmacist about possible side effects or interactions."
                    },
                    "Do not start, stop or change a prescribed dose based only on "
                            + "MediBot's response."
            );

            suggestions = new String[]{
                    "What if I miss a dose?",
                    "How do I manage reminders?",
                    "When should I see a doctor?"
            };
        }

        // Appointments
        else if (containsAny(q, "appointment", "appointments",
                "manage appointments", "manage my appointments",
                "how do i manage appointments",
                "doctor visit", "doctor appointment",
                "hospital visit", "upcoming visit",
                "appointment reminder", "schedule appointment")) {

            answer = formatAnswer(
                    "MediSync can help you record appointments and remember "
                            + "scheduled visits.",
                    "Managing an appointment:",
                    new String[]{
                            "Open the appointment reminder section.",
                            "Enter the requested appointment details.",
                            "Check the date and time carefully before saving.",
                            "Review the saved entry and ensure notifications are permitted."
                    },
                    "MediSync reminders do not replace confirmation from your clinic "
                            + "or hospital."
            );

            suggestions = new String[]{
                    "How do I save a report?",
                    "How do I manage appointments?",
                    "How do I manage reminders?",
                    "How do I update my health profile?",
                    "How to use MediSync"
            };
        }

        // Medical reports
        else if (containsAny(q, "medical report", "lost report",
                "health report", "test report", "lab report",
                "blood report", "report missing", "save report", "save reports",
                "search report", "search reports")) {

            answer = formatAnswer(
                    "MediSync can help you organise medical report details.",
                    "Managing report records:",
                    new String[]{
                            "Open the medical report section.",
                            "Add the report title and the requested details.",
                            "Save the entry and use the search field to find it later.",
                            "Keep the original report or digital file securely."
                    },
                    "A saved record may contain details only, rather than the "
                            + "actual report document. Check what your app has stored."
            );

            suggestions = new String[]{
                    "How do I search reports?",
                    "How do I manage appointments?",
                    "How do I update my health profile?"
            };
        }

        // Allergies
        else if (containsAny(q, "allergy", "allergies",
                "allergic reaction")) {

            answer = formatAnswer(
                    "Allergies can cause different reactions, and some can be serious.",
                    "Helpful precautions:",
                    new String[]{
                            "Avoid known allergens when possible.",
                            "Tell healthcare professionals about your allergies.",
                            "Ask a qualified professional how to manage your specific allergy."
                    },
                    "Swelling of the tongue or throat, difficulty breathing or collapse "
                            + "may indicate a life-threatening reaction. Call emergency "
                            + "services immediately."
            );

            suggestions = new String[]{
                    "When should I see a doctor?",
                    "Healthy habits",
                    "How to use emergency contacts"
            };
        }

        // Fever
        else if (containsAny(q, "fever", "high temperature",
                "body temperature")) {

            answer = formatAnswer(
                    "A fever can occur for many reasons. MediBot cannot determine its cause.",
                    "What you can do:",
                    new String[]{
                            "Rest and drink fluids if you can safely do so.",
                            "Monitor your temperature and how you feel.",
                            "Seek medical advice if the fever persists or worsens.",
                            "Seek urgent care for severe symptoms or difficulty breathing."
                    },
                    "For an infant under 3 months with a temperature of 38°C or higher, "
                            + "seek urgent medical assessment."
            );

            suggestions = new String[]{
                    "How can I manage a headache?",
                    "Healthy habits",
                    "When should I see a doctor?"
            };
        }

        // Headache
        else if (containsAny(q, "headache", "head pain")) {

            answer = formatAnswer(
                    "Headaches can have different causes, and MediBot cannot diagnose them.",
                    "Helpful steps for a mild headache:",
                    new String[]{
                            "Rest somewhere quiet if possible.",
                            "Drink water if you may be dehydrated.",
                            "Take a break from screens and bright lights.",
                            "Seek medical advice if headaches persist or keep returning."
                    },
                    "A sudden, extremely severe headache or headache with weakness, "
                            + "confusion or loss of consciousness needs urgent medical care."
            );

            suggestions = new String[]{
                    "What should I do for a fever?",
                    "How can I improve my sleep?",
                    "When should I see a doctor?"
            };
        }

        // Cough and cold
        else if (containsAny(q, "cough", "common cold",
                "runny nose", "blocked nose", "sore throat")) {

            answer = formatAnswer(
                    "Coughs and cold symptoms can have several causes.",
                    "General self-care:",
                    new String[]{
                            "Rest and drink fluids if appropriate.",
                            "Avoid smoke and other irritating fumes.",
                            "Wash your hands regularly.",
                            "Seek medical advice if symptoms worsen or persist."
                    },
                    "Difficulty breathing, severe chest pain or blue/grey lips "
                            + "require emergency medical attention."
            );

            suggestions = new String[]{
                    "What should I do for a fever?",
                    "Healthy habits",
                    "When should I see a doctor?"
            };
        }

        // Vomiting and diarrhoea
        else if (containsAny(q, "vomiting", "vomit", "throwing up",
                "diarrhoea", "diarrhea", "loose motions",
                "loose stools")) {

            answer = formatAnswer(
                    "Vomiting and diarrhoea can cause dehydration.",
                    "General care:",
                    new String[]{
                            "Take small, frequent sips of fluids if you can keep them down.",
                            "An oral rehydration solution may help replace lost fluids.",
                            "Rest and eat foods you can tolerate.",
                            "Seek medical advice if symptoms persist or worsen."
                    },
                    "Seek urgent care for blood in vomit or stools, severe abdominal pain, "
                            + "confusion, fainting or signs of severe dehydration."
            );

            suggestions = new String[]{
                    "What should I do for a fever?",
                    "Healthy habits",
                    "When should I see a doctor?"
            };
        }

        // Nosebleed first aid
        else if (containsAny(q, "nosebleed", "nose bleed",
                "bleeding from nose", "my nose is bleeding")) {

            answer = formatAnswer(
                    "A nosebleed can often be managed with simple first aid.",
                    "What to do:",
                    new String[]{
                            "Sit upright and lean slightly forward.",
                            "Pinch the soft part of your nose firmly.",
                            "Hold the pressure continuously for 10 to 15 minutes.",
                            "Breathe through your mouth and avoid tilting your head backward."
                    },
                    "Seek urgent medical care if bleeding is heavy, follows a serious injury, "
                            + "causes weakness or dizziness, or does not stop with sustained pressure."
            );

            suggestions = new String[]{
                    "First aid for a minor burn",
                    "When should I see a doctor?",
                    "How to use emergency contacts"
            };
        }

        // Minor cuts
        else if (containsAny(q, "minor cut", "small cut",
                "cut my finger", "small wound", "minor wound")) {

            answer = formatAnswer(
                    "Small cuts may be manageable with basic first aid.",
                    "What to do:",
                    new String[]{
                            "Wash your hands before caring for the wound.",
                            "Apply gentle, steady pressure with clean gauze to stop bleeding.",
                            "Once bleeding stops, rinse the wound with clean running water.",
                            "Cover it with a clean dressing and keep it clean."
                    },
                    "Seek medical care for deep wounds, uncontrolled bleeding, animal bites, "
                            + "or possible infection. Ask a healthcare professional about tetanus protection if needed."
            );

            suggestions = new String[]{
                    "First aid for a minor burn",
                    "When should I see a doctor?",
                    "How to use emergency contacts"
            };
        }

        // Dehydration
        else if (containsAny(q, "dehydration", "dehydrated",
                "signs of dehydration", "am i dehydrated")) {

            answer = formatAnswer(
                    "Dehydration happens when the body loses more fluid than it takes in.",
                    "Possible signs include:",
                    new String[]{
                            "Feeling thirsty or having a dry mouth.",
                            "Passing urine less often or having dark-coloured urine.",
                            "Feeling tired or dizzy."
                    },
                    "Drink fluids if you can safely do so. Confusion, fainting or severe "
                            + "weakness needs urgent medical assessment."
            );

            suggestions = new String[]{
                    "How much water should I drink?",
                    "What should I do for vomiting?",
                    "When should I see a doctor?"
            };
        }

        // Healthy weight habits
        else if (containsAny(q, "lose weight", "weight loss",
                "gain weight", "healthy weight", "weight management")) {

            answer = formatAnswer(
                    "Healthy weight management is about supporting wellbeing, "
                            + "not following extreme diets.",
                    "Helpful habits:",
                    new String[]{
                            "Eat regular, balanced meals.",
                            "Include a variety of vegetables, fruits and protein foods.",
                            "Choose physical activities that suit your abilities.",
                            "Get enough sleep and make changes gradually."
                    },
                    "Avoid crash diets and unverified weight-loss products. A healthcare "
                            + "professional can help with individual nutritional needs."
            );

            suggestions = new String[]{
                    "What is a balanced diet?",
                    "Healthy habits",
                    "Sources of protein"
            };
        }

        // Handwashing
        else if (containsAny(q, "how to wash hands", "wash my hands",
                "proper handwashing", "steps of handwashing",
                "hand washing steps")) {

            answer = formatAnswer(
                    "Washing your hands properly helps reduce the spread of germs.",
                    "Steps:",
                    new String[]{
                            "Wet your hands with clean running water.",
                            "Apply soap and rub palms, backs of hands and between fingers.",
                            "Scrub for at least 20 seconds.",
                            "Rinse well and dry with a clean towel."
                    },
                    "Wash your hands before preparing or eating food and after using the toilet."
            );

            suggestions = new String[]{
                    "Healthy habits",
                    "How to prevent infections",
                    "When should I see a doctor?"
            };
        }

        // Common symptoms
        else if (containsAny(q, "fever", "headache", "stomach pain",
                "stomach ache", "cough", "cold", "vomiting", "dizziness",
                "symptom", "feeling sick", "illness", "pain")) {

            answer = formatAnswer(
                    "Symptoms can have many causes, and MediBot cannot diagnose "
                            + "the cause from a message.",
                    "What you can do:",
                    new String[]{
                            "Notice when the symptoms started and whether they change.",
                            "Rest if appropriate and drink fluids if you can safely do so.",
                            "Contact a healthcare professional if symptoms persist, "
                                    + "worsen or concern you."
                    },
                    "Seek urgent help for severe symptoms, difficulty breathing, "
                            + "fainting, confusion or other signs of an emergency."
            );

            suggestions = new String[]{
                    "When should I see a doctor?",
                    "Healthy habits",
                    "How to use emergency contacts"
            };
        }

        // Account and login
        else if (containsAny(q, "password", "login", "log in",
                "register", "account", "sign in", "forgot password")) {

            answer = formatAnswer(
                    "MediSync provides account options to help you access your records.",
                    "Account help:",
                    new String[]{
                            "Use your registered username and password to log in.",
                            "Use the registration option if you have not created an account.",
                            "Open the Account section to view the options available in your app.",
                            "Keep your password private."
                    },
                    "If you cannot access your account, use the recovery options available "
                            + "in your app, if provided."
            );

            suggestions = new String[]{
                    "How to use MediSync",
                    "How do I update my health profile?",
                    "How do I save a report?"
            };
        }
        // Managing reminders
        else if (containsAny(q, "manage reminders", "set reminders",
                "manage my reminders", "how do reminders work",
                "medicine reminder settings", "set a medicine reminder")) {

            answer = formatAnswer(
                    "MediSync can help you keep track of medicine and "
                            + "appointment reminders.",
                    "Managing reminders:",
                    new String[]{
                            "Open the relevant medicine or appointment section.",
                            "Enter the required details, including the date and time.",
                            "Save the reminder and check that it appears correctly.",
                            "Allow notifications for MediSync in your phone settings."
                    },
                    "Check your saved details carefully. A reminder is a helpful "
                            + "prompt, not confirmation that medicine has been taken "
                            + "or an appointment has been confirmed."
            );

            suggestions = new String[]{
                    "What if I miss a dose?",
                    "How do I manage appointments?",
                    "Medicine safety"
            };
        }

        // MediSync features
        else if (containsAny(q, "how to use medisync", "what is medisync",
                "what can medisync do", "medisync app", "app features",
                "what does this app do", "health profile",
                "emergency contact", "problem resolver")) {

            answer = formatAnswer(
                    "MediSync helps you organise selected health information "
                            + "and reminders in one app.",
                    "Available features include:",
                    new String[]{
                            "Health profile and personal health details.",
                            "Medicine records and reminders.",
                            "Medical report details and search.",
                            "Appointment reminders and emergency contacts.",
                            "Problem Resolver and MediBot for general guidance."
                    },
                    "Available actions depend on the section and version of your app. "
                            + "Keep important medical documents securely backed up."
            );

            suggestions = new String[]{
                    "How do I save a report?",
                    "How do I manage appointments?",
                    "What if I miss a dose?",
                    "How do I update my health profile?"
            };
        }
        // When to seek medical advice
        else if (containsAny(q, "see a doctor", "when should i see a doctor",
                "consult a doctor", "medical advice", "need a doctor",
                "should i visit a doctor")) {

            answer = formatAnswer(
                    "A healthcare professional can assess symptoms and help "
                            + "you decide what care is needed.",
                    "Consider seeking medical advice when:",
                    new String[]{
                            "Symptoms persist, worsen or keep returning.",
                            "Symptoms interfere with sleep or everyday activities.",
                            "You are concerned about a medicine's effects.",
                            "You notice a new or unexplained health problem."
                    },
                    "For severe symptoms such as chest pain, difficulty breathing "
                            + "or loss of consciousness, seek emergency help immediately."
            );

            suggestions = new String[]{
                    "Healthy habits",
                    "Medicine safety",
                    "How to use MediSync"
            };
        }
        // Thanks
        else if (containsAny(q, "thank you", "thanks", "thank u")) {

            answer = "You're welcome! You can ask another question or choose "
                    + "one of the suggestions below.";

            suggestions = new String[]{
                    "Healthy habits",
                    "Medicine safety",
                    "How to use MediSync"
            };
        }

        // Fallback
        else {

            answer = "I don't have a prepared offline answer for that question yet.\n\n"
                    + "Try asking about nutrition, sleep, stress, exercise, hygiene, "
                    + "first aid, medicines, missed doses, appointments, reports or "
                    + "MediSync features.\n\n"
                    + "For personal medical advice, consult a healthcare professional.";

            suggestions = new String[]{
                    "Healthy habits",
                    "Medicine safety",
                    "How do I manage appointments?",
                    "How to use MediSync"
            };
        }

        addMessage(answer, false);
        speakAnswer(answer);
        addSuggestionChips(suggestions);
    }



    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            String pattern = "(?<![a-z0-9])"
                    + java.util.regex.Pattern.quote(keyword.toLowerCase(Locale.ROOT))
                    + "(?![a-z0-9])";

            if (java.util.regex.Pattern.compile(pattern)
                    .matcher(text)
                    .find()) {
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

    private void addSuggestionChips(String... suggestions) {

        TextView label = new TextView(this);
        label.setText("You could also ask:");
        label.setTextSize(13);
        label.setTextColor(Color.parseColor("#426D68"));
        label.setPadding(6, 4, 6, 2);
        chatContainer.addView(label);

        HorizontalScrollView horizontalScrollView =
                new HorizontalScrollView(this);

        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        LinearLayout chipContainer = new LinearLayout(this);
        chipContainer.setOrientation(LinearLayout.HORIZONTAL);
        chipContainer.setGravity(Gravity.START);

        for (String suggestion : suggestions) {

            Button chip = new Button(this);
            chip.setText(suggestion);
            chip.setTextSize(12);
            chip.setAllCaps(false);
            chip.setTextColor(Color.parseColor("#205C56"));
            chip.setTypeface(null, Typeface.NORMAL);
            chip.setMinHeight(0);
            chip.setMinimumHeight(0);
            chip.setMinWidth(0);
            chip.setMinimumWidth(0);
            chip.setPadding(14, 4, 14, 4);

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.parseColor("#D8F3EC"));
            background.setCornerRadius(40);
            background.setStroke(1, Color.parseColor("#A8DCD0"));
            chip.setBackground(background);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(4, 2, 4, 2);
            chipContainer.addView(chip, params);

            chip.setOnClickListener(v -> {
                etMessage.setText(suggestion);
                sendMessage();
            });
        }

        horizontalScrollView.addView(chipContainer);

        chatContainer.addView(horizontalScrollView);

        scrollChat.post(() ->
                scrollChat.fullScroll(View.FOCUS_DOWN));
    }

    private String formatAnswer(
            String introduction,
            String heading,
            String[] steps,
            String closing) {

        StringBuilder result = new StringBuilder();

        result.append(introduction)
                .append("\n\n")
                .append(heading)
                .append("\n");

        for (int i = 0; i < steps.length; i++) {
            result.append(i + 1)
                    .append(". ")
                    .append(steps[i])
                    .append("\n");
        }

        if (closing != null && !closing.trim().isEmpty()) {
            result.append("\n").append(closing);
        }

        return result.toString().trim();
    }
}
