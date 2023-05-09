package com.tomieiro.licensepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String mCurrentLicense;
    private TextView mLicenseView;
    private Boolean mIsHardware = false;
    private int mDiferential = 0;
    private Integer mLicenseType = 0;
    private Switch[] mMarks = new Switch[6];
    private static final String TAG = "LicensePicker";
    private static final HashMap<Integer, String> LICENSES = new HashMap<>();

    @SuppressLint("DiscouragedApi")
    protected void getViews() {
        this.mLicenseView = findViewById(R.id.licenseText);
        for(int i=0, id; i<4; i++){
            id = getResources().getIdentifier("switch" + i, "id", getPackageName());
            this.mMarks[i] = findViewById(id);
        }
    }

//    private int calculateIndex(Boolean[] qs){
//        Boolean[] ref = {true, false, false, false, true, true, false, true};
//        int aux = 0;
//        for(int i=0; i<qs.length; i++){
//            aux += qs[i] == ref[i] ? 1 : 0;
//        }
//        return aux;
//    }

    protected void fillLicenses() {
        //Copyleft licenses
        LICENSES.put(Integer.valueOf("1"  + "1" + "0"), "gpl-2.0");
        LICENSES.put(Integer.valueOf("1"  + "1" + "1"), "gpl-3.0");
        LICENSES.put(Integer.valueOf("1"  + "0" + "0"), "mpl-2.0");
        LICENSES.put(Integer.valueOf("1"  + "0" + "1"), "mpl-2.0");

        //Permissive licenses
        LICENSES.put(Integer.valueOf("2" + "0" + "0"), "apache-2.0");
        LICENSES.put(Integer.valueOf("2" + "2" + "0"), "apache-2.0");
        LICENSES.put(Integer.valueOf("2" + "2" + "1"), "apache-2.0");
        LICENSES.put(Integer.valueOf("2" + "1" + "0"), "mit");
        LICENSES.put(Integer.valueOf("2" + "0" + "1"), "mit");

        //Public domain licenses
        LICENSES.put(300, "unlicense");
        LICENSES.put(301, "cc0");
    }

    private String getLicense(String license) {
        AssetManager assMng = this.getAssets();
        int len = 0;

        try (AssetFileDescriptor fd = assMng.openFd(license + ".txt")) {
            len = (int) fd.getLength();
        } catch (IOException e) {
            Log.e(TAG, "Error in file openning: " + e.toString());
        }

        byte []aux = new byte[len];

        try (InputStream textBuffer = assMng.open(license + ".txt")) {
            textBuffer.read(aux, 0, aux.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(aux, StandardCharsets.UTF_8);
    }

    private void writeToLicenseField(String content) {
        this.mLicenseView.setText(this.mCurrentLicense.toUpperCase() + " LICENSE\n\n");
        this.mLicenseView.setText(this.mLicenseView.getText() + content);
    }

    private void chooseLicense() {
        this.mMarks[1].setOnClickListener(view -> {
            //this.mMarks[2].setEnabled(!((Switch)view).isChecked());
            this.mMarks[3].setEnabled(!((Switch)view).isChecked());
        });
        this.mMarks[3].setOnClickListener(view -> {
            this.mMarks[1].setEnabled(!((Switch)view).isChecked());
            this.mMarks[2].setEnabled(!((Switch)view).isChecked());
        });
        findViewById(R.id.recommend).setOnClickListener(v ->{
            this.mIsHardware = this.mMarks[0].isChecked();
            this.mLicenseType = this.mMarks[1].isChecked() ? 1 : this.mMarks[3].isChecked() ? 3 : 2;
            this.mDiferential = this.mMarks[2].isChecked() ? 1 : 0;
            int lIndex = Integer.parseInt(
                    String.valueOf(mLicenseType) + this.mDiferential + (mIsHardware ? 1 : 0));
            //Log.d(TAG, "chooseLicense: " + lIndex);
            this.mCurrentLicense = LICENSES.get(lIndex);
            writeToLicenseField(getLicense(this.mCurrentLicense));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillLicenses();
        getViews();
        chooseLicense();
    }
}