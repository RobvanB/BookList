package com.vanbran.booklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BookListMainAct extends Activity {
    /** Called when the activity is first created. */
        //setContentView(R.layout.main);
    	DBAdapter db = new DBAdapter(this);
    	EditText titleFld;
    	EditText authorFld;
    	EditText statusFld;
    	    		
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	try
        	{
        		super.onCreate(savedInstanceState);
        		setContentView(R.layout.main);
//        		titleFld = (EditText)findViewById(R.id.TitleVal);
//        		authorFld = (EditText)findViewById(R.id.AuthorVal);
        		
//        		db.open();
//        		rewardsTD.setText(String.valueOf(db.getAllRewards()));
//        		rewardsMonthly.setText(String.valueOf(db.getMonthlyRewards()));
//        		db.close();
        		
        		//Capture buttons from Layout
        		Button insertButton = (Button)findViewById(R.id.Insert);
        		Button searchButton = (Button)findViewById(R.id.Search);
        		//Register the onClick listener
        		insertButton.setOnClickListener(mAddListenerInsert);
        		searchButton.setOnClickListener(mAddListenerSearch);        		
        	}
        	catch (Exception ex)
        	{
        		Context context = getApplicationContext();
        		CharSequence text = ex.toString();
        		int duration = 5000 ; //Toast.LENGTH_LONG;
        		
        		Toast toast = Toast.makeText(context, text, duration);
        		toast.show();
        	}
        }
        
        //Create an anonymous implementation of OnClickListener for the insert
        private OnClickListener mAddListenerInsert = new OnClickListener() {
        	public void onClick(View v){
        		long id = 0;
        		//Do something when the button is clicked
        		db.open();
        		try{
        			titleFld = (EditText)findViewById(R.id.TitleVal);
        			authorFld = (EditText)findViewById(R.id.AuthorVal);
        			statusFld = (EditText)findViewById(R.id.StatusVal);
        			
        			id = db.insertBook(titleFld.getText().toString(),
        							   authorFld.getText().toString(),
        							   statusFld.getText().toString());
        			
        			//Clear the fields
        			titleFld.setText("");
        			authorFld.setText("");
        			statusFld.setText("");
        			titleFld.requestFocus();        			
        		}
        		catch(Exception ex)
        		{
        			Context context = getApplicationContext();
        			CharSequence text = ex.toString() + "ID: " + id;
        			int duration = Toast.LENGTH_LONG ;
        			
        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        		
        		db.close();
        	
        	}
        };

        //Create an anonymous implementation of OnClickListener for the Search
        private OnClickListener mAddListenerSearch = new OnClickListener() {
        	public void onClick(View v){

        		String titleStr;
        		String authorStr;
        		String statusStr;
        		
        		//Do something when the button is clicked
        		db.open();
        		try{
        			titleFld = (EditText)findViewById(R.id.TitleVal);
        			authorFld = (EditText)findViewById(R.id.AuthorVal);
        			statusFld = (EditText)findViewById(R.id.StatusVal);
        		
        			titleStr = titleFld.getText().toString() ;
        			authorStr = authorFld.getText().toString();
        			statusStr = statusFld.getText().toString();
        		
        			if (titleStr.length() == 0)
        			{
        				titleStr = "'%'";
        			}else
        			{
        				titleStr = "'" + titleStr + "'";
        			}
        			if (authorStr.length() == 0)
        			{
        				authorStr = "'%'";
        			}else
        			{
        				authorStr = "'" + titleStr + "'";
        			}
        			if(statusStr.length() == 0)
        			{
        				statusStr = "'%'";
        			}else
        			{
        				statusStr = "'" + titleStr + "'";
        			}
        				
        			Intent intent = new Intent(BookListMainAct.this, ShowList.class);
        			intent.putExtra("title", titleStr);
        			intent.putExtra("author", authorStr);
        			intent.putExtra("status", statusStr);
        			startActivity(intent);
        		}
        		catch(Exception ex)
        		{
        			Context context = getApplicationContext();
        			CharSequence text = ex.toString() ;
        			int duration = 10000; //Toast.LENGTH_LONG ;
        			
        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        		
        		db.close();
        	
        	}
        };
    }
