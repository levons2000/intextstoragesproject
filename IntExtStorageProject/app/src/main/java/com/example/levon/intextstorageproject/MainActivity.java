package com.example.levon.intextstorageproject;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    //Views
    private EditText fileName;
    private EditText fileText;
    private TextView readedFileText;

    //Activity members
    private boolean isInternal = false, isExternal = false;

    //Files Directory Paths
    private File internalFilesPath;
    private final File externalFilePath = new File(Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + MY_DIR_NAME);

    //Constants for activity
    private static final int PERMISSION_REQUEST_CODE = 228;
    private static final String MY_DIR_NAME = "FilesDir";
    private static final String LOG_TAG = "mkDirLogTag";
    private static final String SUCCESS_RESULT = "Dir is created";
    private static final String ALREADY_CREATED_RESULT = "Dir is already created";
    private static final String FILE_NOT_FOUND = "File With This Name Not Found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addInternalStoragePath();
        checkPermission();
        findViews();

        setRadioButtonsCheck();
        setFileChangers();
    }

    //Adding internal storage path (getBaseContext will not working if onCreate not done)
    private void addInternalStoragePath() {
        internalFilesPath = new File(getBaseContext().getFilesDir() + File.separator + MY_DIR_NAME);
    }

    //Finding our activity member views by his id
    private void findViews() {
        fileName = findViewById(R.id.file_name);
        fileText = findViewById(R.id.file_text);
        readedFileText = findViewById(R.id.text_container);
    }

    //This method will check <<Write External Storage>> Permission
    //It necessary for creating files in External Storage
    private void checkPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                PERMISSION_REQUEST_CODE);
    }

    //Check Radio Buttons click and change Internal/External Boolean members of activity
    private void setRadioButtonsCheck() {
        final RadioButton internalButton = findViewById(R.id.internal_button);
        internalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternal = true;
                isExternal = false;
            }
        });

        final RadioButton externalButton = findViewById(R.id.external_button);
        externalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInternal = false;
                isExternal = true;
            }
        });
    }

    //This method will add listeners for file adding button and show text button
    private void setFileChangers() {
        final ImageButton buttonAdd = findViewById(R.id.add_file);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWhenAddFile();
            }
        });

        final ImageButton buttonShow = findViewById(R.id.show_file);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileText();
            }
        });
    }

    //Creating directory in internal storage
    private void createInternalDir() {
        if (internalFilesPath.mkdir()) {
            Log.d(LOG_TAG, SUCCESS_RESULT);
        } else {
            Log.d(LOG_TAG, ALREADY_CREATED_RESULT);
        }
    }

    //Creating directory in external storage
    private void createExternalDir() {
        if (externalFilePath.mkdir()) {
            Log.d(LOG_TAG, SUCCESS_RESULT);
        } else {
            Log.d(LOG_TAG, ALREADY_CREATED_RESULT);
        }
    }

    //Logic for adding files in our Internal or External storage directorys
    private void doWhenAddFile() {
        if (isInternal) {
            createInternalDir();
            createFileByPath(internalFilesPath);
        } else if (isExternal) {
            createExternalDir();
            createFileByPath(externalFilePath);
        }
    }

    //This method creating file by his argument path
    private void createFileByPath(File filePath) {
        FileWriter fw = null;
        try {
            final File file = new File(filePath, fileName.getText().toString());
            fw = new FileWriter(file);
            fw.append(fileText.getText().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //This method getting file text by his argument path
    private String getFileTextByPath(File filePath) {
        BufferedReader br = null;
        try {
            final File file = new File(filePath, fileName.getText().toString());
            br = new BufferedReader(new FileReader(file));
            String lane;
            final StringBuffer stringBuffer = new StringBuffer();
            while ((lane = br.readLine()) != null) {
                stringBuffer.append(lane);
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            Toast.makeText(MainActivity.this, FILE_NOT_FOUND, Toast.LENGTH_SHORT).
                    show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //Set file text in our text view
    private void showFileText() {
        if (isInternal) {
            readedFileText.setText(getFileTextByPath(internalFilesPath));
        } else if (isExternal) {
            readedFileText.setText(getFileTextByPath(externalFilePath));
        }
    }
}
