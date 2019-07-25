package samovi.gymcompanion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    TextView homeTitle;
    Button goToWarmUp;
    Button goToExercises;
    Button goToTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        try{
            actionBar.setTitle(R.string.app_name);
        }
        catch (NullPointerException npe){
            System.out.println("[!!!] Error setting action bar title");
        }

        checkFolderStructure();

        homeTitle = findViewById(R.id.homeTitle);

        goToWarmUp = findViewById(R.id.goToWarmUp);
        goToExercises = findViewById(R.id.goToExercises);
        goToTimer = findViewById(R.id.goToTimer);

        goToWarmUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,AllWarmUps.class);
                startActivity(intent);
            }
        });

        goToExercises.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this, AllExercises.class);
                startActivity(intent);
            }
        });

        goToTimer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this, CountdownTimer.class);
                startActivity(intent);
            }
        });


        // set interval time values, just in case
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("exerciseTime", 30); // Storing integer
        editor.putInt("resetTime", 20); // Storing integer

        editor.commit(); // commit changes
        // editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.

        Intent intent;

        if (item.getItemId() == R.id.settings){
            //add the function to perform here
            intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkFolderStructure(){
        Context context = getApplicationContext();
        File baseDir = context.getExternalFilesDir(null);

        try{
            new File(baseDir.getPath() + "/warm-ups").mkdir();
            new File(baseDir.getPath() + "/warm-ups/HeadNeck").mkdir();
            new File(baseDir.getPath() + "/warm-ups/UpperBody").mkdir();
            new File(baseDir.getPath() + "/warm-ups/LowerBody").mkdir();
            new File(baseDir.getPath() + "/warm-ups/Core").mkdir();

            new File(baseDir.getPath() + "/exercises").mkdir();
            new File(baseDir.getPath() + "/exercises/LowerBody").mkdir();
            new File(baseDir.getPath() + "/exercises/Core").mkdir();
            new File(baseDir.getPath() + "/exercises/UpperBody").mkdir();
            new File(baseDir.getPath() + "/exercises/Back").mkdir();
        }
        catch(Exception e){
            System.out.println("[#] Error creating folder");
            e.printStackTrace();
        }
    }
}
