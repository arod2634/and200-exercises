package com.example1.arod.android200example1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SurveyActivity extends Activity {

    // Declare class variables and objects
    EditText name;
    ToggleButton isMarried;
    CheckBox hasKids;
    CheckBox hasPets;
    TextView spouseLabel;
    TextView petType;
    TextView petLabel;
    EditText spouseName;
    TextView numberOfKidsLabel;
    RadioGroup numberOfKids;
    TextView previousSurveyResultsLabel;
    TextView previousSurveyResults;
    String numberOfChildren;
    String summary;

    public final static String EXTRA_MESSAGE = "com.example1.arod.android200example1.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        loadViewObjects();
        prefillForm();
        showWelcomeDialog();

    }

    private void loadViewObjects() {

        // Once the view in rendered, create instances of all the view objects that will we interacted with
        name = (EditText) findViewById(R.id.editName);

        isMarried = (ToggleButton) findViewById(R.id.marriageStatusToggle);
        spouseLabel = (TextView) findViewById(R.id.spouseName);
        spouseName = (EditText) findViewById(R.id.editSpouseName);

        hasKids = (CheckBox) findViewById(R.id.childrenCheckBox);
        numberOfKidsLabel = (TextView) findViewById(R.id.howManyChildren);
        numberOfKids = (RadioGroup) findViewById(R.id.numberOfChildren);

        hasPets = (CheckBox) findViewById(R.id.pets);
        petType = (TextView) findViewById(R.id.petType);
        petLabel = (TextView) findViewById(R.id.petLabel);

        previousSurveyResultsLabel = (TextView) findViewById(R.id.previousResultsLabel);
        previousSurveyResults = (TextView) findViewById(R.id.previousSurveyResults);

    }

    private void prefillForm() {

        // Prefill form with previous users data saved to a serialized file
        User user = new User();

        try {
            FileInputStream fis = openFileInput("User.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            user = (User)ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        name.setText(user.getName());

        if (!user.getPetType().equals("")) {
            hasPets.setChecked(true);
            petType.setText(user.getPetType());
            petLabel.setVisibility(View.VISIBLE);
            petType.setVisibility(View.VISIBLE);
        }

        if (!user.getSpouseName().equals("")) {
            isMarried.setChecked(true);
            spouseName.setText(user.getSpouseName());
            spouseLabel.setVisibility(View.VISIBLE);
            spouseName.setVisibility(View.VISIBLE);
        }

        Log.i("Output", "user.getNumberOfKids() = " + user.getNumberOfKids());

        if (!(user.getNumberOfKids() == null)) {
            numberOfChildren = user.getNumberOfKids();
            hasKids.setChecked(true);
            numberOfKidsLabel.setVisibility(View.VISIBLE);
            numberOfKids.setVisibility(View.VISIBLE);
            switch (user.getNumberOfKids()) {
                case "one":
                    numberOfKids.check(R.id.radio_one);
                    break;
                case "two":
                    numberOfKids.check(R.id.radio_two);
                    break;
                case "three":
                    numberOfKids.check(R.id.radio_three);
                    break;
                case "three or more":
                    numberOfKids.check(R.id.radio_four);
                    break;
                default:
                    numberOfKids.clearCheck();
                    hasKids.setChecked(false);
                    numberOfKidsLabel.setVisibility(View.GONE);
                    numberOfKids.setVisibility(View.GONE);
            }
        }

    }

    private void showWelcomeDialog() {

        AlertDialog.Builder welcomeMessage  = new AlertDialog.Builder(this);
        welcomeMessage.setTitle("Welcome!");

        // Pull the previous users summary string from the applications shared preferences
        SharedPreferences savedAppData = getSharedPreferences("App Data", MODE_PRIVATE);

        String previousSurveryResults = savedAppData.getString("summary", "");

        if (!previousSurveryResults.equals("")) {
            welcomeMessage.setMessage("Previous Survey Results:\n\n" + previousSurveryResults);
        } else {
            welcomeMessage.setMessage("Please tell me about yourself");
        }

        AlertDialog dlg = welcomeMessage.create();
        dlg.show();

    }

    public void toggleMarriageStatus(View view) {

        // Dynamically hide and show spouse UI elements if marriage status is checked
        if (isMarried.isChecked()) {
            spouseLabel.setVisibility(View.VISIBLE);
            spouseName.setVisibility(View.VISIBLE);
        } else {
            spouseLabel.setVisibility(View.GONE);
            spouseName.setVisibility(View.GONE);
            spouseName.setText("");
        }

    }

    public void checkedChildren(View view) {

        // Dynamically hide and show children UI elements if "Have Children" is checked
        if (hasKids.isChecked()) {
            numberOfKidsLabel.setVisibility(View.VISIBLE);
            numberOfKids.setVisibility(View.VISIBLE);
        } else {
            numberOfKidsLabel.setVisibility(View.GONE);
            numberOfKids.setVisibility(View.GONE);
            numberOfKids.clearCheck();
            numberOfChildren = "";
        }

    }

    public void radioClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_one:
                if (checked)
                    numberOfChildren = "one";
                break;
            case R.id.radio_two:
                if (checked)
                    numberOfChildren = "two";
                break;
            case R.id.radio_three:
                if (checked)
                    numberOfChildren = "three";
                break;
            case R.id.radio_four:
                if (checked)
                    numberOfChildren = "three or more";
                break;
        }
    }

    public void checkedHasPets(View view) {

        // Show custom pets dialog
        if (hasPets.isChecked()) {

            final String[] pets = { "Cat", "Dog", "Rabbit", "Turtle", "Fish", "Other..." };
            final ArrayList<String> selectedPets = new ArrayList<>(6);

            AlertDialog.Builder choosePet = new AlertDialog.Builder(this);
            choosePet.setTitle("What kind of pets do you have?");

            DialogInterface.OnMultiChoiceClickListener clickedMultiChoiceOption = new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    if (isChecked)
                        selectedPets.add(pets[which]);
                    else
                        selectedPets.remove(pets[which]);

                }
            };

            DialogInterface.OnClickListener clickedDialogButton = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE) {
                        petType.setText(String.valueOf(selectedPets).subSequence(1, String.valueOf(selectedPets).length()-1));
                        petLabel.setVisibility(View.VISIBLE);
                        petType.setVisibility(View.VISIBLE);
                    } else {
                        hasPets.setChecked(false);
                        petType.setText("");
                    }

                }
            };

            choosePet.setMultiChoiceItems(pets, null, clickedMultiChoiceOption);

            choosePet.setPositiveButton("OK", clickedDialogButton);
            choosePet.setNegativeButton("Cancel", clickedDialogButton);

            AlertDialog dlg = choosePet.create();
            dlg.show();

        } else {
            petLabel.setVisibility(View.GONE);
            petType.setVisibility(View.GONE);
            petType.setText("");
        }

    }

    public void summarizeInfo(View view) {

        // Validate user input and display error messages to use via Toast
        if (name.getText().toString().matches(""))
            Toast.makeText(getApplicationContext(), "Name can not be empty", Toast.LENGTH_SHORT).show();
        else if (isMarried.isChecked() && spouseName.getText().toString().matches(""))
            Toast.makeText(getApplicationContext(), "Spouse name can not be empty", Toast.LENGTH_SHORT).show();
        else if (hasKids.isChecked() && numberOfChildren == null)
            Toast.makeText(getApplicationContext(), "Please specify how many children you have", Toast.LENGTH_SHORT).show();
        else {

            summary =  name.getText() + " is single with ";

            // Input is valid - build readable output string to show on next screen
            if (!isMarried.isChecked()) {

                // Single w/ no kids
                if(!hasKids.isChecked())
                    summary += "no children";
                else  {
                    // Single w/ 1 kid
                    if (numberOfChildren.equals("one"))
                        summary += numberOfChildren + " child";
                    // Single w/ 2 or more kids
                    else
                        summary += numberOfChildren + " children";
                }

            } else {

                summary = name.getText() + " is married to " + spouseName.getText();

                // Married w/ no kids
                if (!hasKids.isChecked())
                    summary += " with no children";
                else {

                    // Married w/ 1 child
                    if (numberOfChildren.equals("one"))
                        summary += " with " + numberOfChildren + " child";
                    // Married w/ 2 or more children
                    else
                        summary += " with " + numberOfChildren + " children";

                }

            }

            if (hasPets.isChecked())
                summary += " and has a " + petType.getText();
            else
                summary += " and no pets";

            showAreYouSureConfirmation();

        }

    }

    private void showAreYouSureConfirmation() {

        // Create an alert and set properties
        AlertDialog.Builder areYouSureConfirmation = new AlertDialog.Builder(this);
        areYouSureConfirmation.setTitle("Are you sure?");
        areYouSureConfirmation.setMessage("Do you really want to see your survey results?");

        // Setup a custom event listener to handle when a user selects an alert option
        DialogInterface.OnClickListener chooseOption = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButtonClicked) {

                // If the user selects "Yes" or "Maybe", show a custom toast
                if ((whichButtonClicked == AlertDialog.BUTTON_POSITIVE) || (whichButtonClicked == AlertDialog.BUTTON_NEUTRAL)) {
                    saveUserData();
                    startIntentToResultsActivity();
                }

            }

        };

        // Attach custom listener to alert dialog clicks
        areYouSureConfirmation.setPositiveButton("Yes", chooseOption);
        areYouSureConfirmation.setNegativeButton("No", chooseOption);
        areYouSureConfirmation.setNeutralButton("Maybe", chooseOption);

        // Create and show custom dialog
        AlertDialog customDialog = areYouSureConfirmation.create();
        customDialog.show();

    }

    private void saveUserData() {

        // Save summary to user prefs
        SharedPreferences sp = getSharedPreferences("App Data", MODE_PRIVATE);
        SharedPreferences.Editor editPreferences = sp.edit();

        editPreferences.putString("summary", summary);

        editPreferences.apply();

        // Save user model to serialized file
        User user = new User();
        user.setName(String.valueOf(name.getText()));
        user.setPetType(String.valueOf(petType.getText()));
        user.setSpouseName(String.valueOf(spouseName.getText()));
        user.setNumberOfKids(numberOfChildren);

        Log.i("Output", "user.setNumberOfKids(numberOfChildren) = " + numberOfChildren);

        try {
            FileOutputStream fos = openFileOutput("User.bin", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearFields();

    }

    private void startIntentToResultsActivity() {

        // Setup intent and pass summary to new activity
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, summary);
        startActivity(intent);

    }

    private void clearFields() {

        name.setText("");
        isMarried.setChecked(false);
        spouseName.setText("");
        hasKids.setChecked(false);
        numberOfKids.clearCheck();
        numberOfChildren = "";
        hasPets.setChecked(false);
        petType.setText("");
        spouseLabel.setVisibility(View.GONE);
        spouseName.setVisibility(View.GONE);
        numberOfKids.setVisibility(View.GONE);
        numberOfKidsLabel.setVisibility(View.GONE);
        petLabel.setVisibility(View.GONE);
        petType.setVisibility(View.GONE);

    }

}