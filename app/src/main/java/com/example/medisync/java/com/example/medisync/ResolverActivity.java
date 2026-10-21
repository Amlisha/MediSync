
package com.example.medisync;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResolverActivity extends AppCompatActivity {

    Spinner spCategory, spProblem;
    Button btnGetAdvice;
    TextView tvSolution;

    Resolver resolver;

    String[] categories = {
            "Medicine and Medical Records",
            "Daily Health and Lifestyle",
            "Stress and Wellbeing",
            "Common Everyday Concerns",
            "Healthy Habits and Prevention",
            "Student Wellness",
            "Food and Hydration",
            "Older Adult and Family Care",
            "Emergency Awareness"
    };

    String[] medicineProblems = {
            "Missed Medicine Dose",
            "Medicine Unavailable",
            "Confusing Medicine Instructions",
            "Missed Appointment",
            "Reschedule Appointment",
            "Lost Medical Report",
            "Prepare for Doctor Visit"
    };

    String[] lifestyleProblems = {
            "Trouble Sleeping",
            "Feeling Tired",
            "Not Enough Water",
            "Sitting Too Long",
            "Irregular Sleep Schedule",
            "Forgetting Daily Routine",
            "Excessive Screen Time"
    };

    String[] stressProblems = {
            "Feeling Stressed",
            "Feeling Overwhelmed",
            "Difficulty Relaxing",
            "Exam Stress",
            "Difficulty Concentrating",
            "Feeling Socially Isolated"
    };

    String[] commonProblems = {
            "Mild Headache",
            "Mild Constipation",
            "Acidity or Indigestion",
            "Mild Muscle Soreness",
            "Dry Eyes",
            "Minor Cut or Scrape",
            "Seasonal Allergies"
    };

    String[] healthyProblems = {
            "Starting a Walking Routine",
            "Improving Eating Habits",
            "Better Sleep Habits",
            "Remembering Water",
            "Personal Hygiene",
            "Reducing Prolonged Sitting",
            "Weekly Wellness Checklist"
    };

    String[] studentProblems = {
            "Long Study Hours",
            "Study Routine"
    };

    String[] foodProblems = {
            "Unhealthy Snacking",
            "Skipping Meals"
    };

    String[] familyProblems = {
            "Family Appointment Help",
            "Family Medicine Help",
            "Safer Home"
    };

    String[] emergencyProblems = {
            "Emergency Advice"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver);

        spCategory = findViewById(R.id.spCategory);
        spProblem = findViewById(R.id.spProblem);
        btnGetAdvice = findViewById(R.id.btnGetAdvice);
        tvSolution = findViewById(R.id.tvSolution);

        resolver = new Resolver();

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(categoryAdapter);

        spCategory.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        updateProblems(position);
                        tvSolution.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        btnGetAdvice.setOnClickListener(v -> {
            String solution = getSelectedSolution();

            tvSolution.setText(solution);
            tvSolution.setVisibility(View.VISIBLE);
        });
    }

    private void updateProblems(int categoryPosition) {

        String[] problems;

        switch (categoryPosition) {
            case 0:
                problems = medicineProblems;
                break;
            case 1:
                problems = lifestyleProblems;
                break;
            case 2:
                problems = stressProblems;
                break;
            case 3:
                problems = commonProblems;
                break;
            case 4:
                problems = healthyProblems;
                break;
            case 5:
                problems = studentProblems;
                break;
            case 6:
                problems = foodProblems;
                break;
            case 7:
                problems = familyProblems;
                break;
            default:
                problems = emergencyProblems;
                break;
        }

        ArrayAdapter<String> problemAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        problems
                );

        problemAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spProblem.setAdapter(problemAdapter);
    }

    private String getSelectedSolution() {

        String problem = spProblem.getSelectedItem().toString();

        switch (problem) {

            case "Missed Medicine Dose":
                return resolver.missedMedicine();

            case "Medicine Unavailable":
                return resolver.medicineUnavailable();

            case "Confusing Medicine Instructions":
                return resolver.confusedMedicineInstructions();

            case "Missed Appointment":
                return resolver.missedAppointment();

            case "Reschedule Appointment":
                return resolver.rescheduleAppointment();

            case "Lost Medical Report":
                return resolver.lostReport();

            case "Prepare for Doctor Visit":
                return resolver.prepareForDoctorVisit();

            case "Trouble Sleeping":
                return resolver.troubleSleeping();

            case "Feeling Tired":
                return resolver.feelingTired();

            case "Not Enough Water":
                return resolver.notEnoughWater();

            case "Sitting Too Long":
                return resolver.sittingTooLong();

            case "Irregular Sleep Schedule":
                return resolver.irregularSleepSchedule();

            case "Forgetting Daily Routine":
                return resolver.forgettingRoutine();

            case "Excessive Screen Time":
                return resolver.excessiveScreenTime();

            case "Feeling Stressed":
                return resolver.feelingStressed();

            case "Feeling Overwhelmed":
                return resolver.feelingOverwhelmed();

            case "Difficulty Relaxing":
                return resolver.difficultyRelaxing();

            case "Exam Stress":
                return resolver.examStress();

            case "Difficulty Concentrating":
                return resolver.difficultyConcentrating();

            case "Feeling Socially Isolated":
                return resolver.feelingIsolated();

            case "Mild Headache":
                return resolver.mildHeadache();

            case "Mild Constipation":
                return resolver.mildConstipation();

            case "Acidity or Indigestion":
                return resolver.indigestion();

            case "Mild Muscle Soreness":
                return resolver.muscleSoreness();

            case "Dry Eyes":
                return resolver.dryEyes();

            case "Minor Cut or Scrape":
                return resolver.minorCut();

            case "Seasonal Allergies":
                return resolver.seasonalAllergies();

            case "Starting a Walking Routine":
                return resolver.walkingRoutine();

            case "Improving Eating Habits":
                return resolver.healthyEating();

            case "Better Sleep Habits":
                return resolver.betterSleepHabits();

            case "Remembering Water":
                return resolver.rememberWater();

            case "Personal Hygiene":
                return resolver.personalHygiene();

            case "Reducing Prolonged Sitting":
                return resolver.reduceSitting();

            case "Weekly Wellness Checklist":
                return resolver.wellnessChecklist();

            case "Long Study Hours":
                return resolver.longStudyHours();

            case "Study Routine":
                return resolver.studyRoutine();

            case "Unhealthy Snacking":
                return resolver.unhealthySnacking();

            case "Skipping Meals":
                return resolver.skippingMeals();

            case "Family Appointment Help":
                return resolver.familyAppointmentHelp();

            case "Family Medicine Help":
                return resolver.familyMedicineHelp();

            case "Safer Home":
                return resolver.saferHome();

            default:
                return resolver.emergencyAdvice();
        }
    }
}
