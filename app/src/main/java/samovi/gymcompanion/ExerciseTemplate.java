package samovi.gymcompanion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import com.github.tutorialsandroid.filepicker.controller.DialogSelectionListener;
import com.github.tutorialsandroid.filepicker.model.DialogConfigs;
import com.github.tutorialsandroid.filepicker.model.DialogProperties;
import com.github.tutorialsandroid.filepicker.view.FilePickerDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;


public class ExerciseTemplate extends AppCompatActivity {
    // form with required fields for exercises:
    //  - Requires: video, name, 3 tips, category
    //  - fields populated when editing
    //  - fields empty when adding new

    ActionBar actionBar;

    Context context;
    Button chooseVideo;
    Button saveExerciseButton;
    Button modifyExerciseButton;
    Button deleteExerciseButton;
    EditText exerciseName;
    EditText exerciseTip_1;
    EditText exerciseTip_2;
    EditText exerciseTip_3;
    TextView feedbackText;
    Spinner categoryDropdown;
    ArrayAdapter<CharSequence> spinnerAdapter;
    // string array to hold: name, category, tips, video source path
    String[] exerciseData = new String[4];
    String[] extraData = new String[4];
    String[] newExercise = new String[4];

    boolean is_it_new = false;

    File baseDir;
    File infoFile;
    File exerciseVideoSource;
    File exerciseVideoTarget;

    String type = ""; // hold content of extra "new", should be "exercise" or "warm-up"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_template);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);

        context  = this.getApplicationContext();

        //setting the buttons
        chooseVideo = findViewById(R.id.inputVideoButton);
        saveExerciseButton = findViewById(R.id.saveExerciseButton);
        modifyExerciseButton = findViewById(R.id.modifyExerciseButton);
        deleteExerciseButton = findViewById(R.id.deleteExerciseButton);
        //setting the text fields
        exerciseName = findViewById(R.id.inputNameText);
        exerciseTip_1 = findViewById(R.id.inputTip_1);
        exerciseTip_2 = findViewById(R.id.inputTip_2);
        exerciseTip_3 = findViewById(R.id.inputTip_3);
        feedbackText = findViewById(R.id.feedback);
        categoryDropdown = findViewById(R.id.categorySpinner);

        checkExtras();

        // populate dropdown menus
        if (type.equals("warm-ups")){
            System.out.println("[#] setting spinner content for warm-ups");
            // set spinner content
            spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.warm_up_categories, android.R.layout.simple_spinner_item);
            // Apply the adapter to the spinner
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryDropdown.setAdapter(spinnerAdapter);
        }
        else if (type.equals("exercises")){
            System.out.println("[#]Setting spinner content for exercises");
            // set spinner content
            spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.exercise_categories, android.R.layout.simple_spinner_item);
            // Apply the adapter to the spinner
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryDropdown.setAdapter(spinnerAdapter);
        }

        // select corresponding category from spinner
        if (!is_it_new) {
            categoryDropdown.setSelection(spinnerAdapter.getPosition(extraData[1]));
        }

        // set exercise category
        if (exerciseData[1] != null){
            System.out.println("[#] New exercise category: "+newExercise[1]);

            if (exerciseData[1].equals("Lower Body") | exerciseData[1].equals("Parte Inferior")){
                exerciseData[1] = "LowerBody";
            }
            else if (exerciseData[1].equals("Core") | exerciseData[1].equals("Tronco")){
                exerciseData[1] = "Core";
            }
            else if (exerciseData[1].equals("Upper Body") | exerciseData[1].equals("Parte Superior")){
                exerciseData[1] = "UpperBody";
            }
            else if (exerciseData[1].equals("Back") | exerciseData[1].equals("Espalda")){
                exerciseData[1] = "Back";
            }
            else if (exerciseData[1].equals("Head/Neck") | exerciseData[1].equals("Cabeza/Cuello")){
                exerciseData[1] = "HeadNeck";
            }
        }
        else{
            System.out.println("[!!!] Should be a new "+type);
        }

        if (!is_it_new){
            // set exercise name
            exerciseName.setText(extraData[0]);
            // populate tip fields
            String[] tips = extraData[2].split("//");
            exerciseTip_1.setText(tips[0]);
            exerciseTip_2.setText(tips[1]);
            exerciseTip_3.setText(tips[2]);
            // set baseDir
            baseDir = new File(context.getExternalFilesDir(null)+"/"+type+"/"+exerciseData[1]);
            System.out.println("[#] Setting baseDir: "+baseDir.getPath());
        }
        else{

        }

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveExerciseButton.setEnabled(false);
            System.out.println("[!!] Can't access external Storage\n");
        }
        else{
            System.out.println("[#] External Storage Accessible");
        }

        // set buttons' functionalities
        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = new String[]{"mkv", "avi", "mp4", "m4v", "webm"};
                FilePickerDialog dialog = new FilePickerDialog(ExerciseTemplate.this,properties);
                dialog.setTitle(R.string.templateFilePickerTitle);
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //files is the array of the paths of files selected by the Application User.
                        if (files.length == 1){
                            System.out.println("[#] Got a file path: "+files[0]);
                            newExercise[3] = files[0];
                        }
                        else{
                            System.out.println("[!!] Got a bunch of stuff: ");
                            for (String s :files){
                                System.out.println("\t> "+s);
                            }
                        }
                    }
                });
                dialog.show();
            }
        });

        saveExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExercise();
            }
        });

        modifyExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyExercise();
            }
        });

        deleteExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

    }

    private void checkExtras(){
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            checkForType(extras);
            checkForData(extras);
        }
        else{
            modifyExerciseButton.setEnabled(false);
            System.out.println("\n[!!] I didn't get any extras :-(\n");
        }
    }

    private void checkForType(Bundle extra){
        type = extra.getString("type");
        System.out.println("[#] Type: "+ type);
    }

    private void checkForData(Bundle extra){
        extraData = extra.getStringArray("Data");
        if (extraData != null && extraData.length==4){
            System.out.println("[#] Received "+type+" data: ");
            exerciseData = extraData;
            for (String s : exerciseData){
                System.out.println("\t>> "+s);
            }
            // disable button
            saveExerciseButton.setEnabled(false);
        }
        else{
            System.out.println("[#] Creating new "+type);
            is_it_new = true;
            // disable buttons
            modifyExerciseButton.setEnabled(false);
            deleteExerciseButton.setEnabled(false);
        }
    }

    private void saveExercise(){
        // write data for new exercise into disk
        File exerciseDir;
        try {
            // set name
            newExercise[0] = exerciseName.getText().toString().replace(' ', '-');
            // set category
            String category = categoryDropdown.getSelectedItem().toString();
            if (category.equals("Lower Body") | category.equals("Parte Inferior")){
                newExercise[1] = "LowerBody";
            }
            else if (category.equals("Core") | category.equals("Tronco")){
                newExercise[1] = "Core";
            }
            else if (category.equals("Upper Body") | category.equals("Parte Superior")){
                newExercise[1] = "UpperBody";
            }
            else if (category.equals("Back") | category.equals("Espalda")){
                newExercise[1] = "Back";
            }
            else if (category.equals("Head/Neck") | category.equals("Cabeza/Cuello")){
                newExercise[1] = "HeadNeck";
            }
            System.out.println("[#] New exercise category: "+newExercise[1]);
            // set exerciseDir
            exerciseDir = new File(context.getExternalFilesDir(null)+"/"+type+"/"+newExercise[1] + "/" + newExercise[0]);
            exerciseDir.mkdir();
            System.out.println("[#] New exercise folder created: "+exerciseDir.getPath());
            // set tips
            newExercise[2] = exerciseTip_1.getText().toString() + " // " +exerciseTip_2.getText().toString() + " // " +exerciseTip_3.getText().toString();
            // new info file
            infoFile = new File(exerciseDir.getPath()+"/"+newExercise[0]+".info");
            infoFile.createNewFile();
            System.out.println("[#] Creating info file: "+infoFile.getPath());
            FileWriter fw = new FileWriter(infoFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(newExercise[2]);
            bw.close();
            fw.close();
            System.out.println("\n[#] "+infoFile.getPath()+" file closed\n");

            // STEP 4: CHECK FOR VIDEO FILE
            if (newExercise[3] != null && !newExercise[3].isEmpty()){
                String[] chunks = newExercise[3].split("\\.");
                String extension = chunks[chunks.length-1];
                exerciseVideoTarget = new File(exerciseDir.getPath()+"/"+newExercise[0]+"."+extension);
                exerciseVideoSource = new File(newExercise[3]);
                newExercise[3] = exerciseVideoTarget.getAbsolutePath();
                System.out.println("\n[#] Saving new video file to: "+newExercise[3]);
                // copy video file from videoPath to exerciseVideo
                InputStream in = new FileInputStream(exerciseVideoSource);
                OutputStream out = new FileOutputStream(exerciseVideoTarget);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            else{
                System.out.println("\n[!!!] No video file\n");
            }

            // source video path saved to exerciseData[3] in selection listener
            for (String s : newExercise){
                System.out.println("\t>> "+s);
            }

            System.out.println("[#] Saving exercise to: "+ exerciseDir.getPath());
            System.out.println("\n\t> Name: "+newExercise[0]+"\n\t> Category: "+newExercise[1]+"\n\t> Tips: "+newExercise[2]);
            if (newExercise[3] != null){
                System.out.println("\n\t> Video: "+newExercise[3]);
            }
            feedbackText.setText(R.string.dataSaveFeedback);
        }
        catch (Exception e) {
            e.printStackTrace();
            feedbackText.setText(R.string.dataSaveProblem);
        }

    }

    private void modifyExercise(){
        try{
            System.out.println("[#] Modifying exercise");

            File exerciseDir;
            // populate exerciseData with input data
            newExercise[0] = exerciseName.getText().toString().replace(' ', '-');
            newExercise[1] = categoryDropdown.getSelectedItem().toString();
            newExercise[2] = exerciseTip_1.getText().toString() + " // " +exerciseTip_2.getText().toString() + " // " +exerciseTip_3.getText().toString();
            if (newExercise[3] == null){
                newExercise[3] = exerciseData[3];
            }
            // source video path saved to exerciseData[3] in selection listener
            System.out.println("[#] Exercise values:");
            for (String s : newExercise){
                System.out.println("\t>> "+s);
            }
            // CATEGORY
            System.out.println("[#] New exercise category: "+newExercise[1]);
            if (newExercise[1].equals("Lower Body") | newExercise[1].equals("Parte Inferior")){
                newExercise[1] = "LowerBody";
            }
            else if (newExercise[1].equals("Core") | newExercise[1].equals("Tronco")){
                newExercise[1] = "Core";
            }
            else if (newExercise[1].equals("Upper Body") | newExercise[1].equals("Parte Superior")){
                newExercise[1] = "UpperBody";
            }
            else if (newExercise[1].equals("Back") | newExercise[1].equals("Espalda")){
                newExercise[1] = "Back";
            }
            else if (newExercise[1].equals("Head/Neck") | newExercise[1].equals("Cabeza/Cuello")){
                newExercise[1] = "HeadNeck";
            }
            exerciseDir = new File(context.getExternalFilesDir(null)+"/"+type+"/"+newExercise[1]+"/"+newExercise[0]);

            if (!newExercise[0].equals(exerciseData[0]) | !newExercise[1].equals(exerciseData[1])){
                // create new folder
                System.out.println("[#] Creating new exercise folder: "+exerciseDir.getPath());
                exerciseDir.mkdir();
            }

            // MODIFY INFO FILE
            infoFile = new File(exerciseDir.getPath()+"/"+newExercise[0]+".info");
            System.out.println("[#] Modifying info file: "+infoFile.getPath());
            if (!infoFile.exists()){
                infoFile.createNewFile();
            }
            FileWriter fw = new FileWriter(infoFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(newExercise[2]);
            bw.close();
            fw.close();
            System.out.println("\n[#] "+infoFile.getPath()+" file closed\n");

            // CHECK FOR NEW VIDEO FILE
            String[] chunks = newExercise[3].split("\\.");
            String extension = chunks[chunks.length-1];
            exerciseVideoTarget = new File(exerciseDir.getPath()+"/"+newExercise[0]+"."+extension);
            exerciseVideoSource = new File(newExercise[3]);
            // changing the value from source to target
            newExercise[3] = exerciseVideoTarget.getAbsolutePath();
            System.out.println("\n[#] Saving new video file to: "+newExercise[3]);
            // copy video file from videoPath to exerciseVideo
            InputStream in = new FileInputStream(exerciseVideoSource);
            OutputStream out = new FileOutputStream(exerciseVideoTarget);
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            feedbackText.setText(R.string.dataSaveFeedback);
        }
        catch (Exception e) {
            e.printStackTrace();
            feedbackText.setText(R.string.dataSaveProblem);
        }
    }

    private void deleteExercise(){
        System.out.println("[#] Deleting exercise: "+exerciseData[0]);
        try{

            File dir = new File(baseDir.getPath()+"/"+exerciseData[0]);
            File file;
            System.out.println("[!] Folder: "+dir);
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i=0; i<children.length; i++)
                {
                    file = new File(dir, children[i]);
                    if (file.delete()){
                        System.out.println("[#] File deleted");
                    }
                    else{
                        System.out.println("[!!!] Problem deleting file");
                    }
                }
                dir.delete();
            }
            feedbackText.setText(R.string.dataSaveFeedback);
        }
        catch (Exception e) {
            e.printStackTrace();
            feedbackText.setText(R.string.dataSaveProblem);
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    private AlertDialog AskOption(){
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(R.string.delete)
                .setMessage(R.string.confirmDeletion)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deleteExercise();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        // return myQuittingDialogBox;
    }
}
