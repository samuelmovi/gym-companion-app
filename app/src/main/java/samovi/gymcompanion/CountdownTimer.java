package samovi.gymcompanion;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import java.io.IOException;


public class CountdownTimer extends AppCompatActivity {

    // this activity is a simple countdownButton timer
    // choose number of seconds [default: 30] and click button

    TextView seconds;
    Button countdown;
    MediaPlayer buzzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);
        getSupportActionBar().setTitle(R.string.countdown);

        seconds = findViewById(R.id.seconds);
        countdown = findViewById(R.id.countdown);
        buzzer = MediaPlayer.create(CountdownTimer.this, R.raw.buzzer);

        seconds.setText("30");

        countdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int choice = Integer.valueOf(String.valueOf(seconds.getText()));
                new CountDownTimer((Integer.valueOf(String.valueOf(seconds.getText()))*1000), 1000) {

                    public void onTick(long millisUntilFinished) {
                        seconds.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        seconds.setText(String.valueOf(choice));
                        buzzer.start();
                        //dialog.show();
                        new AlertDialog.Builder(CountdownTimer.this)
                                .setTitle("Bien Hecho")
                                .setMessage("TERMINADO!!!")

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

                                    }
                                })
                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }.start();

            }

        });
    }
}
