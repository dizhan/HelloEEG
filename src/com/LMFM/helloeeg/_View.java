package com.LMFM.helloeeg;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

public class _View extends LinearLayout{
	private EditText display;
	
	public _View(Activity activity){
		super(activity, null);
		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.main, this);
		display = (EditText) findViewById(R.id.editText1);
	}

	public void setView(String s) {
		display.setText(s);
	}
}
