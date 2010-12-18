package com.vanbran.booklist;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadXML extends Activity
{
	
	long id 			= 0;
	int  rowsInserted 	= 0;
	int  rowsUpdated  	= 0;
	int  rowsSkipped	= 0;
	DBAdapter db = new DBAdapter(this);
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load);
		db.open();	
		
		try
		{
			parseXML(this);
		}
		catch(XmlPullParserException ex)
		{
			Context context = getApplicationContext();
			CharSequence text = "XML Parser Exception: " + ex.toString() ;
			int duration = 10000; //Toast.LENGTH_LONG ;
		
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		catch(IOException ex)
		{
			Context context = getApplicationContext();
			CharSequence text = "IO Exception: " + ex.toString() ;
			int duration = 500000; //Toast.LENGTH_LONG ;
		
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			db.close();
		}
		catch(Exception ex)
		{
			Context context = getApplicationContext();
			CharSequence text = "Exception: " + ex.toString() ;
			int duration = 5000000; //Toast.LENGTH_LONG ;
		
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			db.close();
		}
		db.close();
	}
	
	private void parseXML(Activity activity)
	throws XmlPullParserException, IOException
	{
		final Activity thisActivity = activity ;
		int	progress = 1;
		ProgressBar	pBar ;
				
		setContentView(R.layout.load);
		
		pBar = (ProgressBar) findViewById(R.id.ProgressBar);
	
		String titleStr = "";
		String authorStr = "";
		String statusStr = "";
		long dcId = 0;
		String tagName = "";
		Resources res = activity.getResources();
		XmlResourceParser  xrp = res.getXml(R.xml.dcandroidexport);
	
		xrp.next();
		int eventType = xrp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			tagName = xrp.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				if (tagName.equals("author"))
				{
					eventType = xrp.next() ;
					if (xrp.getName().equals("name"))
					{
						xrp.next(); //Get to the content
						authorStr = xrp.getText();
					}
				}
				else if (tagName.equals("title"))
				{
					xrp.next();
					titleStr = xrp.getText();
				}
				else if (tagName.equals("user-short-text-field-2"))
				{	
					xrp.next();
					statusStr = xrp.getText();
				}
				else if (tagName.equals("id"))
				{
					xrp.next();
					dcId = Long.parseLong(xrp.getText());
				}
			}	
			if (eventType == XmlPullParser.END_TAG && tagName.equals("book"))
			{
				//Write the record
				this.insert(titleStr, authorStr, statusStr, dcId);
				titleStr = "";
				authorStr = "";
				statusStr = "";
			}
			xrp.next();
			eventType = xrp.getEventType();
			progress = progress + 1;
			pBar.incrementProgressBy(progress);
		}
		db.close();
		//Show a nice dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Upload complete. \n" + rowsSkipped + " Rows Skipped,\n" + rowsInserted 
											+ " Rows Inserted, \n" + rowsUpdated + " Rows Updated.")
	       .setCancelable(false)
	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() 
	       {
	           public void onClick(DialogInterface dialog, int id) 
	           {
	        	   thisActivity.finish();
	           }
	       });
	
		AlertDialog alert = builder.create();		
		alert.show();
	}
	
	private void insert(String titleFld, String authorFld, String statusFld, Long dcId)
	{
		try
		{
			//If the record exists, update it
			Cursor cursor = db.findByDcId(dcId);
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				int colId = cursor.getColumnIndexOrThrow("_id");
				id = cursor.getInt(colId);
				colId = cursor.getColumnIndexOrThrow("Author");
				String curAuthor = cursor.getString(colId);
				colId = cursor.getColumnIndexOrThrow("Title");
				String curTitle = cursor.getString(colId);
				colId = cursor.getColumnIndexOrThrow("Status");
				String curStatus = cursor.getString(colId);
				
				if (curAuthor.equals(authorFld) &&
					curTitle.equals(titleFld)   &&
					curStatus.equals(statusFld)    )
				{
					rowsSkipped++;
				}else
				{
					db.updateBook(id, authorFld, titleFld, statusFld, dcId);
					rowsUpdated++ ;
				}
			}
			else
			{
				id = db.insertBook(authorFld, titleFld, statusFld, dcId);
				rowsInserted++;
			}
			/*
			Context context = getApplicationContext();
    		CharSequence text = "Record with id " + id + " loaded to DB.";
    		int duration = Toast.LENGTH_SHORT;
    		
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    		*/			
		}
		catch(Exception ex)
		{
    		Context context = getApplicationContext();
    		CharSequence text = ex.toString();
    		int duration = 5000 ; //Toast.LENGTH_LONG;
    		
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	}
	}
}	
