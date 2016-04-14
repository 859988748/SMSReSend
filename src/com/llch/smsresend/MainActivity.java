package com.llch.smsresend;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	Button button;
	EditText text;
	TextView currenNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button1);
		text = (EditText) findViewById(R.id.editText1);
		currenNum = (TextView) findViewById(R.id.textView2);
        button.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
                // Perform action on click
                String phoneNum = text.getText().toString();
                SharedPreferences settings = getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor = settings.edit();
                if (phoneNum.equals("") || phoneNum == null ){
                	phoneNum = "-1";
                }
                editor.putString("phnum",phoneNum);
                editor.commit();
                updateCurrentNumView();
            }  
        });  
        updateCurrentNumView();
	}

	private void updateCurrentNumView(){
		SharedPreferences settings = getSharedPreferences("setting", 0);
		String phone = settings.getString("phnum","无号码");
		currenNum.setText(phone);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
