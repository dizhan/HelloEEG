package com.LMFM.helloeeg;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import android.media.MediaPlayer;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import android.view.Menu;


import com.neurosky.thinkgear.*;
import com.LMFM.helloeeg.R;

import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
//import android.bluethooth.BluetoothAdapter;
//import android.bluethooth.BluetoothDevice;



@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
public class HelloEEGActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;

	BluetoothAdapter bluetoothAdapter;

	TextView tv;
	TextView AttentionMes;
	TextView MediationMes;
	Button b;
	EditText fileName;

	TGDevice tgDevice;
	final boolean rawEnabled = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView)findViewById(R.id.textView1);
        tv.setText("");
        tv.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        
        fileName = (EditText)findViewById(R.id.editText1);
        //fileName.getText();
        
        AttentionMes = (TextView)findViewById(R.id.textView3);
        AttentionMes.setText("");
        
        MediationMes = (TextView)findViewById(R.id.textView5);
        MediationMes.setText("");   
        
        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		MediaPlayer mediaPlayer = MediaPlayer.create(HelloEEGActivity.this, R.raw.seagull);
        		mediaPlayer.start();
        		
        	}
        });
        

        
        
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //String bluetoothname = bluetoothAdapter.getName();
        

        
        if(bluetoothAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {
        	/*if (bluetoothAdapter.isEnabled()) {
        	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	}*/
        	/* create the TGDevice */
        	/*ArrayAdapter mArrayAdapter = null;
        	Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        	if (pairedDevices.size()>0){
        		for (BluetoothDevice device : pairedDevices){
        			mArrayAdapter.add(device.getName() +"\n"+device.getAddress());
        		}
        	}*/
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        	/*if (tgDevice == null){
        		tv.append("there ");
        	}*/
        	tgDevice.connect(true);
        	tgDevice.start();
        	
   
        }  
    }
    
    @Override
    public void onDestroy() {
    	
    	tgDevice.close();
        super.onDestroy();
    }
    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:
            	tv.append("get message");
                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	tv.append("Connecting...\n");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
	                	tv.append("Connected.\n");
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	tv.append("Can't find\n");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	tv.append("not paired\n");
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	tv.append("Disconnected mang\n");
	                	save(AttentionMes.getText().toString(), MediationMes.getText().toString(),fileName.getText().toString());


	                    System.out.println(getFilesDir());
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
            		//tv.append("PoorSignal: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_RAW_DATA:	  
            		//raw1 = msg.arg1;
            		//tv.append("Got raw: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_HEART_RATE:
        		//tv.append("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:
            		int att = msg.arg1;
            		AttentionMes.append(msg.arg1+ " ");
            		//Log.v("HelloA", "Attention: " + att + "\n");
            	break;
            case TGDevice.MSG_MEDITATION:
            	int Med = msg.arg1;
            	MediationMes.append(msg.arg1+ " ");
            	break;
            case TGDevice.MSG_BLINK:
            		//tv.append("Blink: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_RAW_COUNT:
            		//tv.append("Raw Count: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            default:
            	break;
        }
        }
    };
    
    public void doStuff(View view) {
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(rawEnabled);   
    	//tgDevice.ena
    }
    public void save(String A, String B, String x)
    {
        try {
            
			FileOutputStream outStream=openFileOutput(x,Activity.MODE_WORLD_WRITEABLE+Activity.MODE_WORLD_READABLE);
            outStream.write(A.getBytes());

            outStream.write(B.getBytes());
            
            //outStream.write(B.toString().getBytes());
            outStream.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            return;
        }
        catch (IOException e){
            return ;
        }
    }
    
 

}