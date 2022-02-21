package com.nbin.termor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int letterIndex = 0;
    String inputedAnswer;
    List <String> arrayOfRightWords;
    GridLayout eachRowInputsGrid;
    GridLayout allColumnsAndRowsInputsGrid;
    GridLayout keyboardGrid;
    AlertDialog alert;
    int userRounds = 0;
    String guess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Create the grid
        allColumnsAndRowsInputsGrid = findViewById(R.id.all_columns_and_rows_inputs_grid);
        createGameInputs(allColumnsAndRowsInputsGrid);

        // Create all keyboard keys
        keyboardGrid = findViewById(R.id.keyboard_grid);
        createKeyboard(keyboardGrid);


        eachRowInputsGrid = (GridLayout) allColumnsAndRowsInputsGrid.getChildAt(userRounds);
        paintGridChild(eachRowInputsGrid, true);

        settingArrayOfWords();

        getaWordFromNewRightActivity();

        int rnd = new Random().nextInt(arrayOfRightWords.size());
        inputedAnswer = arrayOfRightWords.get(rnd);


        // Setting the game
        activateSpecialButtonsListeners();
    }

    private void settingArrayOfWords() {
        arrayOfRightWords = new ArrayList<String>();
        arrayOfRightWords.add("COSER");
        arrayOfRightWords.add("CANTO");
        arrayOfRightWords.add("NAVIO");
        arrayOfRightWords.add("MOUSE");
        arrayOfRightWords.add("CALDA");
    }

    protected void createGameInputs(GridLayout grid) {
        for (int i = 1; i <= 6; i++) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            GridLayout gridLayout = (GridLayout) inflater.inflate(R.layout.input, null);
            gridLayout.setId(i);
            grid.addView(gridLayout);
        }
    }

    protected void createKeyboard(GridLayout grid) {
        String[] alphabet = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "<-", "z", "x", "c", "v", "b", "n", "m"};
        int id = alphabet.length;

        for (String letter : alphabet) {
            createEachKeyButton(grid, id, letter.toUpperCase(Locale.ROOT));
            id--;
        }

    }

    protected void createEachKeyButton(GridLayout grid, int id, String letter) {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        Button button = (Button) inflater.inflate(R.layout.keyboard, null);


        grid.addView(button);

        button.setText(letter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button clickedButton = (Button) view;
                String clickedButtonText = (String) clickedButton.getText();


                displayOrEraseLetterOnGrid(clickedButtonText);

            }
        });
    }

    protected void displayOrEraseLetterOnGrid(String clickedLetter) {

        Button displayedButton = (Button) eachRowInputsGrid.getChildAt(this.letterIndex);
        CharSequence displayedButtonText = displayedButton.getText();

        if (clickedLetter == "<-") {//delete
            if (displayedButtonText == "") {
                decrementLetterIndex();
            }
            displayedButton = (Button) eachRowInputsGrid.getChildAt(this.letterIndex);
            displayedButton.setText("");
        } else {
            displayedButton.setText(clickedLetter);
            incrementLetterIndex();
        }
    }

    // This method bring the right word using Bundle
    public void getaWordFromNewRightActivity() {
        Bundle data = null;
        data = this.getIntent().getExtras();
        if (data != null) {
            arrayOfRightWords.add(data.getString("WORD"));
        }
    }

    protected String turnEachLetterInputIntoAStringAndReturnIt() {
        String guess = "";
        int numberOfButtonsInside = eachRowInputsGrid.getChildCount();
        for (int i = 0; i < numberOfButtonsInside; i++) {
            Button buttonAtIndex = (Button) eachRowInputsGrid.getChildAt(i);
            CharSequence buttonText = buttonAtIndex.getText();
            guess = guess + String.valueOf(buttonText);
        }
        return guess;
    }

    protected void activateSpecialButtonsListeners() {
        // Activate button that starts NewRightAnswerActivity
        ImageView newWordButton = findViewById(R.id.new_word_button);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewRightAnswerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Activate enter button from keyboard
        Button enterButton = findViewById(R.id.button_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guess = turnEachLetterInputIntoAStringAndReturnIt();
                if (guess.length() != 5) {
                    return;
                }
                winAndLoseController();
            }
        });

        // Activate more informations button
        ImageView helpButton = (ImageView) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpDialog();
            }
        });
    }

    public void winAndLoseController() {
        if (checkAnswer()) {
            setGridChildDisable(keyboardGrid);
            showAlertWhenUserWin();
            return;
        }

        if (userRounds == 5) {
            // Disable keyboard for user not to write any letter
            setGridChildDisable(keyboardGrid);
            showAlertWhenUserLose();
            return;
        }
        goToNextGridRow();
    }

    protected boolean checkAnswer() {

        if (guess.equals(inputedAnswer)) {
            paintInput(0, 5, R.color.green);
            return true;
        }

        for (int i = 0; i <= letterIndex; i++) {
            Character compChar = guess.charAt(i);//neede some castings to make this work
            CharSequence charToCharSeq = compChar.toString();//lol

            if (guess.charAt(i) == inputedAnswer.charAt(i)) {//right letter right place
                paintInput(i, i + 1, R.color.green);

            } else if (inputedAnswer.contains(charToCharSeq)) {//right letter wrong place
                paintInput(i, i + 1, R.color.yellow);

            } else if (guess.charAt(i) != inputedAnswer.charAt(i)) {
                paintInput(i, i + 1, R.color.dark_letter_color);
            }
        }
        return false; //there is no match
    }

    protected void goToNextGridRow() {
        incrementRound();
        paintGridChild(eachRowInputsGrid, false);
        eachRowInputsGrid = (GridLayout) allColumnsAndRowsInputsGrid.getChildAt(userRounds);
        //restart the letter index so the user doesn't write in the last button
        paintGridChild(eachRowInputsGrid, true);
        letterIndex = 0;
    }

    private void showAlertWhenUserWin() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = (View) inflater.inflate(R.layout.win_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alert = builder.create();
        WindowManager.LayoutParams alertaAttributes = alert.getWindow().getAttributes();

        alertaAttributes.gravity = Gravity.TOP | Gravity.CENTER;
        alertaAttributes.y = 178;
        alert.show();
        ;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(true);
    }

    // this method create a Alert with the right word, and restart the game
    private void showAlertWhenUserLose() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.game_over_dialog, null);
        TextView textView = (TextView) view.getChildAt(0);
        textView.setText("palavra certa: " + inputedAnswer);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alert = builder.create();
        WindowManager.LayoutParams alertaAttributes = alert.getWindow().getAttributes();

        alertaAttributes.gravity = Gravity.TOP | Gravity.CENTER;
        alertaAttributes.y = 178;
        alert.show();
        ;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(true);
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    // This method paints the button
    private void paintInput(int firstElement, int lastElement, int color) {
        for (int i = firstElement; i < lastElement; i++) {
            eachRowInputsGrid.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
        }
    }

    //This method loop into children of a grid, and disable them
    private void setGridChildDisable(GridLayout grid) {
        int numberOfChilds = grid.getChildCount() - 1;
        for (int i = 0; i < numberOfChilds; i++) {
            grid.getChildAt(i).setEnabled(false);
            grid.getChildAt(i).setClickable(false);
        }
    }

    private void paintGridChild(GridLayout grid, boolean param) {
        int backgroundDrawable = R.drawable.background_selected_inputs;
        if (param == false) {
            backgroundDrawable = R.drawable.background_inputs;
        }

        int numberOfChilds = grid.getChildCount() - 1;
        for (int i = 0; i <= numberOfChilds; i++) {
            grid.getChildAt(i).setBackgroundResource(backgroundDrawable);
        }
    }

    private void showHelpDialog() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.help_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alert = builder.create();
        WindowManager.LayoutParams alertaAttributes = alert.getWindow().getAttributes();

        alertaAttributes.gravity = Gravity.TOP | Gravity.CENTER;
        alertaAttributes.y = 200;
        alert.show();
        ;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View closeDialogView = alert.findViewById(R.id.close_help_dialog);
        closeDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    protected void incrementLetterIndex() {
        letterIndex = letterIndex == 4 ? 4 : letterIndex + 1;
    }

    protected void decrementLetterIndex() {
        letterIndex = letterIndex <= 0 ? 0 : letterIndex - 1;
    }

    protected void incrementRound() {
        userRounds = userRounds < 5 ? userRounds + 1 : userRounds;
    }


}