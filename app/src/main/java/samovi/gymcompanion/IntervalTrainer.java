package samovi.gymcompanion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import java.io.IOException;


public class IntervalTrainer extends AppCompatActivity {

    String[] exerciseData = new String[4];
    String[][] extraData;

    TextView exerciseName;
    VideoView exerciseVideo;
    TextView exerciseCategory;
    TextView tip_1;
    TextView tip_2;
    TextView tip_3;
    Button countdownButton;
    MediaController mediaController;
    int counter = 0;

    MediaPlayer buzzer;
    MediaPlayer horn;

    int exerciseTime;
    int recoveryTime;

    CountDownTimer exerciseCount;
    CountDownTimer recoveryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_trainer);

        loadTimes();

        buzzer = MediaPlayer.create(IntervalTrainer.this, R.raw.buzzer);
        horn = MediaPlayer.create(IntervalTrainer.this, R.raw.horn);

        exerciseName =findViewById(R.id.exerciseName);
        exerciseVideo=findViewById(R.id.videoView);
        exerciseCategory = findViewById(R.id.exerciseCategory);
        tip_1=findViewById(R.id.tip1);
        tip_2=findViewById(R.id.tip2);
        tip_3=findViewById(R.id.tip3);
        // buttons
        countdownButton =findViewById(R.id.countdownButton);
        countdownButton.setText(String.valueOf(exerciseTime));
        //nextExercise=findViewById(R.id.nextExercise);

        mediaController=new MediaController(this);
        mediaController.setAnchorView(exerciseVideo);
        mediaController.setMediaPlayer(exerciseVideo);
        exerciseVideo.setMediaController(mediaController);

        recoveryCount = new CountDownTimer(recoveryTime*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownButton.setText(String.valueOf(millisUntilFinished / 1000));
            }
            // when the timer ends play sound, show alert, set exerciseTime and start countdown
            public void onFinish() {
                horn.start();
                new AlertDialog.Builder(IntervalTrainer.this)
                        .setTitle(R.string.recoveryDialogTitle)
                        .setMessage(R.string.recoveryDialogText)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                horn.stop();
                                try{
                                    horn.prepare();
                                }
                                catch(IOException ioe){
                                    ioe.printStackTrace();
                                }
                                countdownButton.setText(String.valueOf(exerciseTime));
                                countdownButton.setBackgroundResource(R.color.exerciseColor);
                                exerciseCount.start();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };

        exerciseCount =new CountDownTimer(exerciseTime*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownButton.setText(String.valueOf(millisUntilFinished / 1000));
            }
            // when timer ends play horn, show alert, go to next exercise
            public void onFinish() {
                buzzer.start();
                new AlertDialog.Builder(IntervalTrainer.this)
                        .setTitle(R.string.exerciseDialogTitle)
                        .setMessage(R.string.exerciseDialogText)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                buzzer.stop();
                                try{
                                    buzzer.prepare();
                                }
                                catch(IOException ioe){
                                    ioe.printStackTrace();
                                }
                                countdownButton.setText(String.valueOf(recoveryTime));
                                countdownButton.setBackgroundResource(R.color.recoveryColor);
                                nextContent();
                                recoveryCount.start();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };

        if (loadExtras()){
            // the activity starts with button set to recoveryTime, with countdown
            // upon finish, show alert dialog
            // upon ok, set button value to exerciseTime and start countdown
            // upon finish, show alert dialog
            // upon ok, go to next exercicse

            // set color and value for button
            countdownButton.setText(String.valueOf(recoveryTime));
            countdownButton.setBackgroundResource(R.color.recoveryColor);

            setContent();
            recoveryCount.start();
        }
        else{
            // no data passed so back to previous activity
            finish();
        }

    }

    public boolean loadExtras(){
        extraData = (String[][]) getIntent().getSerializableExtra("ExerciseData");
        if (extraData == null || extraData.length == 0){
            System.out.println("[!!] No extra information");
            return false;
        }
        return true;
    }

    public void loadTimes(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        // SharedPreferences.Editor editor = pref.edit();
        exerciseTime = pref.getInt("exerciseTime", -1);
        recoveryTime = pref.getInt("resetTime", -1);
    }

    public void loadExerciseData(){
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
        exerciseVideo.start();
        mediaController.hide();    //no parece que funcione

    }

    public void nextContent(){
        if(counter==extraData.length -1 ){
            counter=0;
        }else{
            counter++;
        }
        // loadTimes();
        countdownButton.setText(String.valueOf(exerciseTime));
        setContent();
    }

    @Override
    public void onBackPressed() {
        // your code.
        System.out.println("[#] pressed back");
        try{
            recoveryCount.cancel();
            exerciseCount.cancel();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finish();
    }
}
