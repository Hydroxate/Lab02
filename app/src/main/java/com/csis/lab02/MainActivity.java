package com.csis.lab02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//PURE DATA IMPORTS

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //For declaring and initialising XML items, Always of form OBJECT_TYPE VARIABLE_NAME = (OBJECT_TYPE) findViewById(R.id.ID_SPECIFIED_IN_XML);
        Button send = (Button) findViewById(R.id.sendButton); //findViewById uses the ids you specified in the xml!
        final TextView freqText = (TextView) findViewById(R.id.sendText);
        Switch initSynthSwitch = (Switch) findViewById(R.id.onOffSwitch);


        try {
            initPD(); //method is below to start PD
            loadPDPatch("synth.pd"); // This is the name of the patch in the zip
        } catch (IOException e) {
            e.printStackTrace(); // print error if init or load patch fails.
            finish();
        }


        initSynthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                float val = (isChecked) ?  1.0f : 0.0f; // value = (get value of isChecked, if true val = 1.0f, if false val = 0.0f)
                sendFloatPD("onOff", val);
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("freq", Float.parseFloat(freqText.getText().toString()));

            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        PdAudio.stopAudio();
    }

    public void sendFloatPD(String receiver, Float value)
    {
        PdBase.sendFloat(receiver, value);
    }

    public void sendBangPD(String receiver)
    {
        PdBase.sendBang(receiver);
    }


    private void loadPDPatch(String patchName) throws IOException
    {
        File dir = getFilesDir();
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.synth), dir, true);
            File pdPatch = new File(dir, patchName);
            PdBase.openPatch(pdPatch.getAbsolutePath());
        }catch (IOException e)
        {

        }
    }

    private void initPD() throws IOException
    {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate,0,2,8,true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

}
