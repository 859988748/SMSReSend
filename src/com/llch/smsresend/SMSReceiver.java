package com.llch.smsresend;

import java.text.SimpleDateFormat;  
import java.util.ArrayList;
import java.util.Date;  
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;  
import android.content.ContentResolver;
import android.content.Context;  
import android.content.Intent;  
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;  
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;  
import android.telephony.SmsMessage;  

public class SMSReceiver extends BroadcastReceiver {
	private Context context = null;
	private String phoneNum = null;
	@Override  
    public void onReceive(Context context, Intent intent) {  
        // TODO Auto-generated method stub  
        // 监听短信广播
		this.context = context;
        if (intent.getAction()  
                .equals("android.provider.Telephony.SMS_RECEIVED") || intent.getAction()  
                .equals("android.provider.Telephony.GSM_SMS_RECEIVED") || intent.getAction()  
                .equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
        	SharedPreferences settings = context.getSharedPreferences("setting", 0);
    		String phone = settings.getString("phnum",null);
    		if (phone == null || phone.equals("-1")){
    			return;
    		}else{
    			phoneNum = phone;
    		}
        	Map<String, String> msg = null; 
            SmsMessage[] msgs;
            Bundle bundle = intent.getExtras();
            
            if (bundle != null && bundle.containsKey("pdus")) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (pdus != null) {
                    int nbrOfpdus = pdus.length;
                    msg = new HashMap<String, String>(nbrOfpdus);
                    msgs = new SmsMessage[nbrOfpdus];
                    
                    // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                    // However, send long SMS of same sender in one message
                    for (int i = 0; i < nbrOfpdus; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        
                        String originatinAddress = msgs[i].getOriginatingAddress();
                        
                        // Check if index with number exists                    
                        if (!msg.containsKey(originatinAddress)) { 
                            // Index with number doesn't exist                                               
                            // Save string into associative array with sender number as index
                            msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody()); 
                            
                        } else {    
                            // Number has been there, add content but consider that
                            // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS, 
                            // so just add the part of the current PDU
                            String previousparts = msg.get(originatinAddress);
                            String msgString = previousparts + msgs[i].getMessageBody();
                            msg.put(originatinAddress, msgString);
                        }
                    }
                    for (String sender : msg.keySet()){
                    	SmsManager manager = SmsManager.getDefault();
                    	String SmsContent = msg.get(sender);
                    	String name = getContactDisplayNameByNumber(sender);
                    	if (name != null){ 
                    		sender = name;
                    	}
                    	ArrayList<String> messages = manager.divideMessage("发送者:"+sender + "\n" + SmsContent);
                    	manager.sendMultipartTextMessage(phoneNum, null, messages, null, null);
                    }
                }
            }
        }  
    }
	
	public String getContactDisplayNameByNumber(String number) {
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String name = null;

	    ContentResolver contentResolver = context.getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
	            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    try {
	        if (contactLookup != null && contactLookup.getCount() > 0) {
	            contactLookup.moveToNext();
	            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
	        }
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }

	    return name;
	}
}
