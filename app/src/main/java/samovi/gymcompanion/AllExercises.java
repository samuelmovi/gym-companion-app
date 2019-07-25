package samovi.gymcompanion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class AllExercises extends AppCompatActivity {

    // this activity contains:
    //  - list view of all current exercises found in app data folder
    //  - sorted by categories
    //  - buttons at bottom to add new exercise
    //  - clicking exercise button takes you to populated ExerciseTemplate
    Button startCircuitTrainerButton;
    Button startIntervalTrainerButton;

    LinearLayout backList;
    LinearLayout upperBodyList;
    LinearLayout coreList;
    LinearLayout lowerBodyList;

    File baseDir;

    Context context;
    // we load all exercises data here.
    // each internal array list has 4 strings
    //  - exercise name
    //  - category
    //  - exercise tips [up to 3 separated by '/']
    //  - path to exercise's video

    ArrayList<String[]> loadedExercises = new ArrayList<>();
    ArrayList<String >  selectedExercises = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exercises);
        ActionBar actionBar = getSupportActionBar();
        // TODO: set application icon
        actionBar.setTitle(R.string.goToExercises);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar
        context = this.getApplicationContext();

        baseDir = context.getExternalFilesDir(null);

        backList = findViewById(R.id.backList);
        upperBodyList = findViewById(R.id.upperBodyList);
        coreList = findViewById(R.id.coreList);
        lowerBodyList = findViewById(R.id.lowerBodyList);

        startCircuitTrainerButton = findViewById(R.id.buttonStartCircuitTrainer);
        startIntervalTrainerButton = findViewById(R.id.buttonStartIntervalTrainer);


        // loadExercises();
        exerciseLoader();

        System.out.println("[#] All exercises loaded from disk:");
        for (String[] s : loadedExercises){
            System.out.print("\n\t> Nombre: "+s[0]+"\n\t> Categoría: "+s[1]+"\n\t> Consejos: "+s[2]+"\n\t> Video: "+s[3]+"\n");
        }
        displayExercises();

        startCircuitTrainerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String[][] exerciseData = new String[selectedExercises.size()][4];

                if (selectedExercises.size() == 0){
                    // send all warm ups
                    exerciseData = new String[loadedExercises.size()][4];
                    for(int i=0;i<loadedExercises.size();i++){
                        exerciseData[i] = loadedExercises.get(i);
                    }
                }
                else{

                    int counter = 0;

                    for(int i=0;i<selectedExercises.size();i++){
                        String name = selectedExercises.get(i);
                        for(int j=0;j<loadedExercises.size();j++){
                            if(loadedExercises.get(j)[0].equals(name)){
                                exerciseData[counter] = loadedExercises.get(j);
                                counter += 1;
                            }
                        }
                    }
                }

                Intent intent=new Intent(AllExercises.this, CircuitTrainer.class);
                intent.putExtra("ExerciseData", exerciseData);
                startActivity(intent);
            }
        });
        startIntervalTrainerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String[][] exerciseData = new String[selectedExercises.size()][4];

                int counter = 0;

                for(int i=0;i<selectedExercises.size();i++){
                    String name = selectedExercises.get(i);
                    for(int j=0;j<loadedExercises.size();j++){
                        if(loadedExercises.get(j)[0].equals(name)){
                            exerciseData[counter] = loadedExercises.get(j);
                            counter += 1;
                        }
                    }
                }
                Intent intent=new Intent(AllExercises.this, IntervalTrainer.class);
                intent.putExtra("ExerciseData", exerciseData);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        exerciseLoader();
    }

    public void loadExercises(File folder, int i){
        System.out.println("[#] Extracting data from: "+folder.getPath());
        File[] dirContents;

        if (folder.exists() && folder.isDirectory()){
            File[] folderContents = folder.listFiles();

            // for each folder we take:
            // [1] folder name as exerciseData[0]
            // [2] category from int i exerciseData[1]
            // [3] contents of info file as exerciseData[2]
            // [4] video path as exerciseData[3]
            for (File dir : folderContents){
                System.out.println("\n\t> "+dir.getPath());
                //4 strings = [0]Exercise name, [1]Category, [2]Tips, [3]Path to video file
                String[] singleExercise = new String[4];
                if (dir.isDirectory()){
                    dirContents = dir.listFiles();
                    if (dirContents.length < 3){
                        // [1] extract dir name as exercise name
                        singleExercise[0] = dir.getName();
                        // [2] set category
                        if (i==1){
                            singleExercise[1] = getString(R.string.categoryLowerBody);
                        }
                        else if (i==2){
                            singleExercise[1] = getString(R.string.categoryCore);
                        }
                        else if (i==3){
                            singleExercise[1] = getString(R.string.categoryUpperBody);
                        }
                        else if (i==4){
                            singleExercise[1] = getString(R.string.categoryBack);
                        }
                        for (File item: dirContents){
                            String fileName = item.getName();
                            System.out.println("\n[#] Processing: "+fileName+"\n");
                            String[] chunks = fileName.split("\\.");
                            String extension = chunks[chunks.length-1];
                            // check for info file
                            if (extension.equals("info")){
                                // read exercise tips from file
                                if (item.canRead()){
                                    try{
                                        FileReader fr = new FileReader(item);
                                        BufferedReader br = new BufferedReader(fr);
                                        // [3] read tips
                                        singleExercise[2] = br.readLine();
                                        // add reading of the video file's path to singleExercise[3]
                                        br.close();
                                    }catch(Exception e){
                                        System.out.print("[!] Something went wrong reading file: ");
                                        e.printStackTrace();
                                    }
                                }
                            }
                            // check for file video extension
                            else if (extension.equals("mkv") | extension.equals("mp4")| extension.equals("m4v")| extension.equals("mov")){
                                // [4] extract path of video file
                                singleExercise[3] = item.toString();
                            }
                        }
                        loadedExercises.add(singleExercise);
                        System.out.print("[#] Adding exercise button:\n\t> Nombre: "+singleExercise[0]+"\n\t> Categoría: "+singleExercise[1]+"\n\t> Consejos: "+singleExercise[2]+"\n\t> Video: "+singleExercise[3]+"\n");
                    }
                }
            }
        }
        else{
            System.out.println("\n!!!!!!!\n WTF baseDIr\n!!!!!\n");
        }
        System.out.println("\n[#] Loaded exercises: "+loadedExercises.size()+"\n");
    }

    public void exerciseLoader(){
        File exerciseDir = new File(baseDir.getPath() + "/exercises");
        System.out.println("\n[#] Working out of: " + exerciseDir.getPath() + "\n");
        for (File folder : exerciseDir.listFiles()){
            String[] pathChunks = folder.getPath().split("/");
            String lastFolder = pathChunks[pathChunks.length-1];
            System.out.println("\n\t> lastFolder: "+lastFolder);
            if (lastFolder.equals("LowerBody")){
                loadExercises(folder, 1);
            }
            else if (lastFolder.equals("Core")){
                loadExercises(folder, 2);
            }
            else if (lastFolder.equals("UpperBody")){
                loadExercises(folder, 3);
            }
            else if (lastFolder.equals("Back")){
                loadExercises(folder, 4);
            }
        }
    }

    public void displayExercises(){

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (final String[] exercise:loadedExercises){
            if (exercise.length == 4){
                final Button newButton = new Button(context);
                newButton.setBackgroundResource(R.color.buttonBackgroundColor);
                newButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if (!newButton.isSelected()){
                            String name = newButton.getText().toString();
                            selectedExercises.add(newButton.getText().toString());
                            System.out.println("[#] Adding selected exercise: "+name);
                            newButton.setSelected(true);
                            newButton.setBackgroundResource(R.color.selectedButtonColor);
                        }
                        else{
                            newButton.setSelected(false);
                            newButton.setBackgroundResource(R.color.buttonBackgroundColor);
                            for(int i=0; i<selectedExercises.size(); i++){
                                if(selectedExercises.get(i).equals(newButton.getText().toString())){
                                    selectedExercises.remove(i);
                                }
                            }
                        }
                    }
                });
                newButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        System.out.println("[#] Long press: launching exercise template...");
                        Intent intent=new Intent(AllExercises.this, ExerciseTemplate.class);
                        exercise[3] = "";
                        intent.putExtra("ExerciseData", exercise);
                        startActivity(intent);
                        return true;
                    }
                });

                // System.out.print("[#] Adding exercise button:\n\t> Nombre: "+exercise[0]+"\n\t> Categoría: "+exercise[1]+"\n\t> Consejos: "+exercise[2]+"\n\t> Video: "+exercise[3]+"\n");
                if(exercise[1] != null){
                    if (exercise[1].equals(getString(R.string.categoryBack))){
                        newButton.setText(exercise[0]);
                        backList.addView(newButton, lp);
                    }
                    else if (exercise[1].equals(getString(R.string.categoryUpperBody))){
                        newButton.setText(exercise[0]);
                        upperBodyList.addView(newButton, lp);
                    }
                    else if (exercise[1].equals(getString(R.string.categoryCore))){
                        newButton.setText(exercise[0]);
                        coreList.addView(newButton, lp);
                    }
                    else if (exercise[1].equals(getString(R.string.categoryLowerBody))){
                        newButton.setText(exercise[0]);
                        lowerBodyList.addView(newButton, lp);
                    }
                }
            }
            else{
                System.out.println("[!!!] Exercise data is missing fields");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_exercises, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        Intent intent;

        if (item.getItemId() == R.id.newExercise){
            //add the function to perform here
            intent=new Intent(AllExercises.this, ExerciseTemplate.class);
            intent.putExtra("new", "exercise");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
