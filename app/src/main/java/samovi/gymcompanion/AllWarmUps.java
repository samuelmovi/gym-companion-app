package samovi.gymcompanion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;


public class AllWarmUps extends AppCompatActivity {

    Context context;

    Button startWarmUp;

    LinearLayout headNeckList;
    LinearLayout upperBodyList;
    LinearLayout coreList;
    LinearLayout lowerBodyList;

    File baseDir;

    ArrayList<String[]> loadedWarmUps = new ArrayList<>();
    ArrayList<String >  selectedWarmUps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_warm_ups);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.goToWarmUp);

        context = this.getApplicationContext();

        baseDir = context.getExternalFilesDir(null);

        startWarmUp = findViewById(R.id.startWarmUp);

        headNeckList = findViewById(R.id.headNeckList);
        upperBodyList = findViewById(R.id.upperBodyList);
        coreList = findViewById(R.id.coreList);
        lowerBodyList = findViewById(R.id.lowerBodyList);

        exerciseLoader();

        displayExercises();

        startWarmUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("[#] Starting warm-up");
                String[][] warmUpData ;

                if (selectedWarmUps.size() == 0){
                    // send all warm ups
                    System.out.println("[#] Sending all warm-ups...");
                    warmUpData = new String[loadedWarmUps.size()][4];
                    for(int i=0;i<loadedWarmUps.size();i++){
                        warmUpData[i] = loadedWarmUps.get(i);
                    }
                }
                else{
                    System.out.println("[#] Sending selected warm-ups...");
                    warmUpData = new String[selectedWarmUps.size()][4];
                    for(int i=0;i<warmUpData.length;i++){
                        String name = selectedWarmUps.get(i);
                        for(int j=0;j<loadedWarmUps.size();j++){
                            if(loadedWarmUps.get(j)[0].equals(name)){
                                System.out.println("["+i+"/"+warmUpData.length+"] "+name);
                                warmUpData[i] = loadedWarmUps.get(j);
                            }
                        }
                    }
                }

                for (String[] warmUp:warmUpData){
                    for (String s:warmUp){
                        System.out.println("\t-> "+s);
                    }
                }

                Intent intent = new Intent(AllWarmUps.this, CircuitTrainer.class);
                intent.putExtra("WarmUpData", warmUpData);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        exerciseLoader();
    }

    public void loadExercise(File folder, int i){
        System.out.println("[#] Extracting data from: "+folder.getPath());
        File[] dirContents;

        if (folder.exists() && folder.isDirectory()){
            File[] folderContents = folder.listFiles();

            // for each folder we take:
            // [1] folder name as exerciseData[0]
            // [2] category from int i as exerciseData[1]
            // [3] contents of info file as exerciseData[2]
            // [4] video path as exerciseData[3]
            for (File dir : folderContents){
                System.out.println("[#] Loading warm-ups from: "+dir.getPath());
                //4 strings = [0]Exercise name, [1]Category, [2]Tips, [3]Path to video file
                String[] singleExercise = new String[4];
                if (dir.isDirectory()){
                    dirContents = dir.listFiles();
                    if (dirContents.length < 3){
                        // [1] extract dir name as exercise name
                        singleExercise[0] = dir.getName();
                        // [2] set category
                        if (i==1){
                            singleExercise[1] = getString(R.string.categoryHeadNeck);
                        }
                        else if (i==2){
                            singleExercise[1] = getString(R.string.categoryUpperBody);
                        }
                        else if (i==3){
                            singleExercise[1] = getString(R.string.categoryCore);
                        }
                        else if (i==4){
                            singleExercise[1] = getString(R.string.categoryLowerBody);
                        }
                        else{
                            System.out.println("\n\t[!!!!] Problem loading exercise ");
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
                            else if (extension.equals("mkv") | extension.equals("mp4")| extension.equals("m4v")| extension.equals("mov")| extension.equals("webm")){
                                // [4] extract path of video file
                                singleExercise[3] = item.getAbsolutePath();
                            }
                        }
                        loadedWarmUps.add(singleExercise);
                        System.out.print("[#] Adding exercise button:\n\t> Nombre: "+singleExercise[0]+"\n\t> Categoría: "+singleExercise[1]+"\n\t> Consejos: "+singleExercise[2]+"\n\t> Video: "+singleExercise[3]+"\n");
                    }
                }
            }
        }
        else{
            System.out.println("\n!!!!!!!\n WTF baseDIr\n!!!!!\n");
        }
        System.out.println("\n[#] Loaded exercises: "+loadedWarmUps.size()+"\n");
    }

    public void exerciseLoader(){
        File exerciseDir = new File(baseDir.getPath() + "/warm-ups");
        System.out.println("\n[#] Loading warmups from: " + exerciseDir.getPath() + "\n");
        for (File folder : exerciseDir.listFiles()){
            String[] pathChunks = folder.getPath().split("/");
            String folderName = pathChunks[pathChunks.length-1];
            System.out.println("\n\t> Folder: "+folderName);
            if (folderName.equals("LowerBody")){
                loadExercise(folder, 4);
            }
            else if (folderName.equals("Core")){
                loadExercise(folder, 3);
            }
            else if (folderName.equals("UpperBody")){
                loadExercise(folder, 2);
            }
            else if (folderName.equals("HeadNeck")){
                loadExercise(folder, 1);
            }
            else{
                System.out.println("\n\t[!!!!] No match for folder: "+folderName);
            }
        }
    }

    public void displayExercises(){

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (final String[] exercise:loadedWarmUps){
            if (exercise.length == 4){
                final Button newButton = new Button(context);
                newButton.setBackgroundResource(R.color.buttonBackgroundColor);
                // newButton.setPadding(5,5,5,5);

                newButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String name = newButton.getText().toString();
                        if (!newButton.isSelected()){
                            selectedWarmUps.add(newButton.getText().toString());
                            System.out.println("[#] Adding selected warm-up: "+name);
                            newButton.setSelected(true);
                            newButton.setBackgroundResource(R.color.selectedButtonColor);
                        }
                        else{
                            newButton.setSelected(false);
                            newButton.setBackgroundResource(R.color.buttonBackgroundColor);
                            for(int i=0; i<selectedWarmUps.size(); i++){
                                if(selectedWarmUps.get(i).equals(newButton.getText().toString())){
                                    System.out.println("[#] Removing selected warm-up: "+name);
                                    selectedWarmUps.remove(i);
                                }
                            }
                        }
                    }
                });
                newButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        System.out.println("[#] should be launching exercise template...");
                        Intent intent=new Intent(AllWarmUps.this, ExerciseTemplate.class);
                        intent.putExtra("type", "warm-ups");
                        intent.putExtra("Data", exercise);
                        startActivity(intent);
                        return true;
                    }
                });

                System.out.println("[#] Adding button for:\n\t> Nombre: "+exercise[0]+" / "+exercise[1]);

                String warmUpType = exercise[1];
                // System.out.println("[#] Warm Up type: "+warmUpType);
                if(warmUpType != null){
                    if (warmUpType.equals(getString(R.string.categoryHeadNeck))){
                        newButton.setText(exercise[0]);
                        headNeckList.addView(newButton, lp);
                        System.out.println("\t> Added to Head/Neck");
                    }
                    else if (warmUpType.equals(getString(R.string.categoryUpperBody))){
                        newButton.setText(exercise[0]);
                        upperBodyList.addView(newButton, lp);
                        System.out.println("\t> Added to upper Body");
                    }
                    else if (warmUpType.equals(getString(R.string.categoryCore))){
                        newButton.setText(exercise[0]);
                        coreList.addView(newButton, lp);
                        System.out.println("\t> Added to Core");
                    }
                    else if (warmUpType.equals(getString(R.string.categoryLowerBody))){
                        newButton.setText(exercise[0]);
                        lowerBodyList.addView(newButton, lp);
                        System.out.println("\t> Added to Lower Body");
                    }
                    else{
                        System.out.println("[!!] Error adding button type: "+warmUpType);
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
        getMenuInflater().inflate(R.menu.all_warm_ups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        // int id = item.getItemId();

        Intent intent;

        switch (item.getItemId()) {
            case R.id.newWarmUp:
                //add the function to perform here
                intent = new Intent(AllWarmUps.this, ExerciseTemplate.class);
                intent.putExtra("type", "warm-ups");
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
