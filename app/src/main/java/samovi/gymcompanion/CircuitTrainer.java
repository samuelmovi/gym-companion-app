package samovi.gymcompanion;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.net.Uri;

public class CircuitTrainer extends AppCompatActivity {

    String[] exerciseData = new String[4];
    String[][] extraData;

    TextView exerciseName;
    VideoView exerciseVideo;
    TextView exerciseCategory;
    TextView tip_1;
    TextView tip_2;
    TextView tip_3;
    Button previousExercise;
    Button nextExercise;
    MediaController mediaController;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_trainer);

        exerciseName =findViewById(R.id.exerciseName);
        exerciseVideo=findViewById(R.id.circuitVideoView);
        exerciseCategory = findViewById(R.id.exerciseCategory);
        tip_1=findViewById(R.id.tip1);
        tip_2=findViewById(R.id.tip2);
        tip_3=findViewById(R.id.tip3);
        previousExercise=findViewById(R.id.previousExercise);
        nextExercise=findViewById(R.id.nextExercise);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(exerciseVideo);
        mediaController.setMediaPlayer(exerciseVideo);
        exerciseVideo.setMediaController(mediaController);

        previousExercise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                previousContent();
            }
        });
        nextExercise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                nextContent();
            }
        });

        if (loadExtras()){
            setContent();
        }
        else{
            // no data passed so back to previous activity
            finish();
        }
    }

    public boolean loadExtras(){
        extraData = (String[][]) getIntent().getSerializableExtra("ExerciseData");
        if (extraData == null || extraData.length == 0){
            extraData = (String[][]) getIntent().getSerializableExtra("WarmUpData");
        }

        if (extraData == null || extraData.length == 0){
            System.out.println("[!!] No extra information");
            return false;
        }
        else{
            System.out.println("[#] Loaded extras:");
            for (int i=0; i<extraData.length; i++){
                for (String s : extraData[i]){
                    System.out.println("\t> "+s);
                }
            }
            return true;
        }
    }

    public void loadExerciseData(){
        System.out.println("[#] Loading exercise data...");
        exerciseData = extraData[counter];
        // set name
        exerciseName.setText(exerciseData[0]);
        // set category
        exerciseCategory.setText(exerciseData[1]);
        //set tips
        String[] allTips = exerciseData[2].split(" // ");
        if (allTips.length == 3){
            tip_1.setText(allTips[0]);
            tip_2.setText(allTips[1]);
            tip_3.setText(allTips[2]);
        }
        //set video
        String uri = "file://"+exerciseData[3];
        System.out.println("[#] Loading video file: "+ uri);
        exerciseVideo.setVideoURI(Uri.parse("file://"+uri));
    }

    public void setContent(){
        loadExerciseData();
        System.out.println("[#] Starting video...");
        exerciseVideo.start();
        System.out.println("[#] Hiding controllers...");
        mediaController.hide();    //no parece que funcione

    }

    public void previousContent(){
        if(counter==extraData.length -1 ){
            counter=0;
        }else{
            counter--;
        }
        setContent();
    }

    public void nextContent(){
        if(counter==extraData.length -1 ){
            counter=0;
        }else{
            counter++;
        }
        setContent();
        System.out.println("[#] Next content has been set");
    }
}
