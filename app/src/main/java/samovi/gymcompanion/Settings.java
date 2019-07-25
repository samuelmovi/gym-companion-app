package samovi.gymcompanion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import com.github.tutorialsandroid.filepicker.controller.DialogSelectionListener;
import com.github.tutorialsandroid.filepicker.model.DialogConfigs;
import com.github.tutorialsandroid.filepicker.model.DialogProperties;
import com.github.tutorialsandroid.filepicker.view.FilePickerDialog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Settings extends AppCompatActivity {

    Context context;
    TextView exerciseTimeInput;
    TextView recoveryTimeInput;
    Button saveButton;
    Button importDataButton;
    Button exportDataButton;

    File baseDir;

    int exerciseTime;
    int recoveryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();
        baseDir = context.getExternalFilesDir(null);

        loadTimes();

        exerciseTimeInput = findViewById(R.id.exerciseTimeInput);
        exerciseTimeInput.setText(String.valueOf(exerciseTime));
        recoveryTimeInput = findViewById(R.id.recoveryTimeInput);
        recoveryTimeInput.setText(String.valueOf(recoveryTime));

        saveButton = findViewById(R.id.saveSettingsData);
        importDataButton = findViewById(R.id.importDataButton);
        exportDataButton = findViewById(R.id.exportDataButton);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveTimes();
                new AlertDialog.Builder(Settings.this)
                        .setTitle(R.string.dataSavedDialogTitle)
                        .setMessage(exerciseTime+"/"+recoveryTime)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        importDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File("/mnt/sdcard/");
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = new String[] {"zip"};
                FilePickerDialog dialog = new FilePickerDialog(Settings.this,properties);
                dialog.setTitle(R.string.importFilePickerTitle);
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //files is the array of the paths of files selected by the Application User.
                        if (files.length == 1){
                            System.out.println("[#] Got a file path: "+files[0]);
                            final ProgressDialog progressDialog;
                            progressDialog = new ProgressDialog(Settings.this);
                            // progressDialog.setMessage(R.string.exportDialogMessage);
                            progressDialog.setTitle(R.string.importDialogTitle); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                            final String name = files[0];
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        unZip(name);
                                        //Thread.sleep(10000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }
                            }).start();
                            // unZip(files[0]);
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

        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                final String newFileName = baseDir.getPath().replace("files", "") +"GymCompanionExport-"+ sdf.format(new Date())+".zip";
                System.out.println("[#] saving to: "+newFileName);

                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(Settings.this);
                // progressDialog.setMessage(R.string.exportDialogMessage);
                progressDialog.setTitle(R.string.exportDialogTitle); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            zip(new File(newFileName));
                            //Thread.sleep(10000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });
    }

    public void saveTimes(){
        try{
            exerciseTime = Integer.valueOf(exerciseTimeInput.getText().toString());
            recoveryTime = Integer.valueOf(recoveryTimeInput.getText().toString());

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("exerciseTime", exerciseTime); // Storing integer
            editor.putInt("resetTime", recoveryTime); // Storing integer

            editor.commit(); // commit changes
            // editor.apply();
            System.out.println("[#] Time values saves");
        }catch(Exception e){
            System.out.println("[!!] Error loading times");
        }
    }

    public void loadTimes(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        // SharedPreferences.Editor editor = pref.edit();
        exerciseTime = pref.getInt("exerciseTime", -1);
        recoveryTime = pref.getInt("resetTime", -1);
    }


    public void zip(File zipFile) {
        // we are zipping everything within /files folder
        List<File> fileList = getSubFiles(baseDir, true);
        ZipOutputStream zipOutputStream;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            int bufferSize = 1024;
            byte[] buf = new byte[bufferSize];
            ZipEntry zipEntry;
            for(int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                zipEntry = new ZipEntry(baseDir.toURI().relativize(file.toURI()).getPath());
                zipOutputStream.putNextEntry(zipEntry);
                if (!file.isDirectory()) {
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                    int readLength;
                    while ((readLength = inputStream.read(buf, 0, bufferSize)) != -1) {
                        zipOutputStream.write(buf, 0, readLength);
                    }
                    inputStream.close();
                }
            }
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[#] All files exported");
    }

    public static List<File> getSubFiles(File baseDir, boolean isContainFolder) {
        List<File> fileList = new ArrayList<>();
        File[] tmpList = baseDir.listFiles();
        for (File file : tmpList) {
            if (file.isFile()) {
                fileList.add(file);
            }
            if (file.isDirectory()) {
                if (isContainFolder) {
                    fileList.add(file); //key code
                }
                fileList.addAll(getSubFiles(file, true));
            }
        }
        return fileList;
    }

    public void unZip(String zipPath) {
        try  {
            FileInputStream fin = new FileInputStream(zipPath);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    File dir = new File(baseDir+"/"+ze.getName());
                    if (!dir.isDirectory()){
                        if (dir.mkdir()){
                            System.out.println("[#] New directory created");
                        }
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(baseDir +"/"+ ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }
                    bufout.close();
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
            System.out.println("[#] Unzipping complete. path :  " +baseDir );
        } catch(Exception e) {
            System.out.println("[!!!] Error unzipping: "+ e);
        }
    }
}
