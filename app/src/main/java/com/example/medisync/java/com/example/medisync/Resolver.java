
package com.example.medisync;

public class Resolver {

    // 1. MEDICINE AND MEDICAL RECORDS

    // When a medicine dose is missed
    public String missedMedicine() {
        return "Check the medicine instructions or ask your "
                + "doctor or pharmacist what to do. Do not take "
                + "an extra dose unless advised.";
    }

    // When medicine is unavailable
    public String medicineUnavailable() {
        return "Contact your pharmacist or doctor to ask about "
                + "availability or a suitable alternative. "
                + "Do not substitute medicines on your own.";
    }

    // When medicine instructions are confusing
    public String confusedMedicineInstructions() {
        return "Read the prescription and medicine label carefully. "
                + "If anything is unclear, contact your pharmacist "
                + "or doctor before taking the medicine.";
    }

    // When an appointment is missed
    public String missedAppointment() {
        return "Contact your doctor or clinic to ask about "
                + "rescheduling your appointment.";
    }

    // When an appointment needs to be rescheduled
    public String rescheduleAppointment() {
        return "Contact the clinic as early as possible. "
                + "Confirm the new date, time, and any preparation "
                + "needed for your visit.";
    }

    // When a medical report is lost
    public String lostReport() {
        return "Contact the hospital or diagnostic centre "
                + "to ask whether a duplicate or digital copy "
                + "is available.";
    }

    // Preparing for a doctor visit
    public String prepareForDoctorVisit() {
        return "Write down your symptoms and questions. "
                + "Carry your previous reports, current medicine "
                + "list, allergies, and appointment details.";
    }

    // 2. DAILY HEALTH AND LIFESTYLE

    // Trouble sleeping
    public String troubleSleeping() {
        return "Try sleeping and waking at regular times. "
                + "Keep your room comfortable and reduce bright "
                + "screens before bedtime. Seek medical advice "
                + "if the problem continues.";
    }

    // Feeling tired
    public String feelingTired() {
        return "Try getting enough sleep, eating regular balanced "
                + "meals, and drinking water. If tiredness persists "
                + "or is unusual, consult a healthcare professional.";
    }

    // Not drinking enough water
    public String notEnoughWater() {
        return "Keep a water bottle nearby and drink water "
                + "regularly throughout the day. Follow any fluid "
                + "restrictions given by your healthcare professional.";
    }

    // Sitting for too long
    public String sittingTooLong() {
        return "Take short movement breaks during long periods "
                + "of sitting. Stand, stretch gently, or walk "
                + "around when possible.";
    }

    // Irregular sleep schedule
    public String irregularSleepSchedule() {
        return "Choose a regular wake-up time and gradually "
                + "adjust your bedtime. Try to maintain the same "
                + "routine on weekends.";
    }

    // Forgetting daily routines
    public String forgettingRoutine() {
        return "Use MediSync reminders, a daily checklist, "
                + "or a calendar to keep track of important "
                + "tasks and routines.";
    }

    // Excessive screen time
    public String excessiveScreenTime() {
        return "Take regular breaks from your screen. Look away "
                + "at a distant object periodically, blink often, "
                + "and adjust screen brightness for comfort.";
    }

    // 3. STRESS AND WELLBEING

    // Feeling stressed
    public String feelingStressed() {
        return "Pause for a moment, breathe slowly and comfortably, "
                + "and divide large tasks into smaller steps. "
                + "Talk to someone you trust if needed.";
    }

    // Feeling overwhelmed
    public String feelingOverwhelmed() {
        return "Write down what needs to be done and choose "
                + "one small task to begin with. Take breaks "
                + "and ask for help when necessary.";
    }

    // Difficulty relaxing
    public String difficultyRelaxing() {
        return "Try quiet music, gentle stretching, reading, "
                + "or a short walk. Choose an activity that "
                + "helps you feel comfortable.";
    }

    // Exam or work stress
    public String examStress() {
        return "Create a realistic study or work schedule. "
                + "Use short focused sessions with breaks, "
                + "get enough sleep, and avoid last-minute overload.";
    }

    // Difficulty concentrating
    public String difficultyConcentrating() {
        return "Reduce distractions, silence unnecessary "
                + "notifications, and work on one task at a time. "
                + "Take breaks and maintain a regular sleep routine.";
    }

    // Feeling socially isolated
    public String feelingIsolated() {
        return "Consider contacting a friend, family member, "
                + "or trusted person. Small conversations and "
                + "shared activities can help you feel connected.";
    }

    // 4. COMMON EVERYDAY CONCERNS

    // Mild headache
    public String mildHeadache() {
        return "Rest in a quiet place, drink water if appropriate, "
                + "and take a break from screens. Seek urgent care "
                + "for a sudden severe headache or headache with "
                + "weakness, confusion, or other serious symptoms.";
    }

    // Mild constipation
    public String mildConstipation() {
        return "Drink enough fluids if not restricted, gradually "
                + "increase fibre-rich foods, and stay physically "
                + "active as tolerated. Seek medical advice if "
                + "it persists or causes significant pain.";
    }

    // Occasional acidity or indigestion
    public String indigestion() {
        return "Try smaller meals and avoid lying down immediately "
                + "after eating. Notice whether particular foods "
                + "trigger symptoms. Seek medical advice if it "
                + "keeps happening.";
    }

    // Mild muscle soreness
    public String muscleSoreness() {
        return "Rest the affected muscles and try gentle movement "
                + "as comfortable. Avoid activities that worsen "
                + "the pain. Seek care for severe pain, major "
                + "swelling, or difficulty moving.";
    }

    // Dry eyes after screen use
    public String dryEyes() {
        return "Take screen breaks, blink regularly, and avoid "
                + "direct airflow toward your eyes. Seek eye-care "
                + "advice if symptoms continue or worsen.";
    }

    // Minor cuts and scrapes
    public String minorCut() {
        return "Wash your hands, gently rinse the wound with "
                + "clean running water, and cover it with a clean "
                + "dressing. Seek care for deep wounds, uncontrolled "
                + "bleeding, or signs of infection.";
    }

    // Seasonal allergy symptoms
    public String seasonalAllergies() {
        return "Notice and avoid triggers when possible. Keep "
                + "windows closed when pollen levels are high "
                + "and wash your hands and face after outdoor "
                + "activities. Ask a pharmacist about suitable options.";
    }

    // 5. HEALTHY HABITS AND PREVENTION

    // Starting a walking routine
    public String walkingRoutine() {
        return "Start with a comfortable amount of walking "
                + "and gradually increase it. Wear suitable footwear "
                + "and choose a safe route.";
    }

    // Improving eating habits
    public String healthyEating() {
        return "Aim for balanced meals with vegetables, fruits, "
                + "whole grains, and suitable protein sources. "
                + "Make gradual changes that fit your needs.";
    }

    // Improving sleep habits
    public String betterSleepHabits() {
        return "Keep a consistent sleep schedule, make your "
                + "bedroom comfortable, and avoid caffeine close "
                + "to bedtime if it affects your sleep.";
    }

    // Remembering water breaks
    public String rememberWater() {
        return "Keep water within reach and link drinking water "
                + "to regular activities, such as meal breaks. "
                + "Follow any fluid limits prescribed for you.";
    }

    // Personal hygiene
    public String personalHygiene() {
        return "Wash your hands regularly, maintain oral hygiene, "
                + "keep personal items clean, and follow basic "
                + "food-safety practices.";
    }

    // Reducing prolonged sitting
    public String reduceSitting() {
        return "Set a reminder to stand or move periodically "
                + "during long study or work sessions. Choose "
                + "movement that is comfortable and safe for you.";
    }

    // Weekly wellness checklist
    public String wellnessChecklist() {
        return "Plan your week with medicine and appointment "
                + "reminders, regular sleep, balanced meals, "
                + "movement breaks, and time to relax.";
    }

    // 6. STUDENT WELLNESS

    // Long study hours
    public String longStudyHours() {
        return "Divide study time into focused sessions. "
                + "Take short breaks, stretch gently, drink water, "
                + "and avoid sacrificing sleep to study.";
    }

    // Difficulty maintaining a study routine
    public String studyRoutine() {
        return "Choose a few realistic daily goals. Start with "
                + "the most important task, keep study materials "
                + "ready, and track completed tasks.";
    }

    // 7. FOOD AND HYDRATION

    // Unhealthy snacking habits
    public String unhealthySnacking() {
        return "Keep convenient options such as fruit, nuts "
                + "if suitable for you, or yoghurt available. "
                + "Choose snacks according to your dietary needs.";
    }

    // Skipping meals
    public String skippingMeals() {
        return "Plan simple meals or snacks ahead of busy days. "
                + "Choose regular meals that suit your schedule "
                + "and nutritional needs.";
    }

    // 8. OLDER ADULT AND FAMILY CARE

    // Helping someone remember appointments
    public String familyAppointmentHelp() {
        return "Add the appointment date, time, clinic location, "
                + "and contact details to a shared calendar "
                + "or reminder system, with their permission.";
    }

    // Organising a family member's medicines
    public String familyMedicineHelp() {
        return "Maintain an up-to-date medicine list with the "
                + "person's permission. Follow the prescription "
                + "and ask a pharmacist if instructions are unclear.";
    }

    // Reducing fall risks at home
    public String saferHome() {
        return "Keep walkways clear, secure loose rugs, improve "
                + "lighting, and use handrails where available. "
                + "Ask a healthcare professional about individual "
                + "fall risks if needed.";
    }

    // 9. EMERGENCY AWARENESS

    // General emergency guidance
    public String emergencyAdvice() {
        return "For severe or sudden symptoms, seek urgent "
                + "medical help. In India, call 112 for emergency "
                + "assistance. Do not rely on this app to diagnose "
                + "or manage a medical emergency.";
    }

}

