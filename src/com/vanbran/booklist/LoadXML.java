package com.vanbran.booklist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
		
		//Login to DropBox
		Intent intent = new Intent(LoadXML.this, DropboxMain.class);
		startActivityForResult(intent,1);
	}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
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
		final File newXml = BookListMainAct.newXml ;		
		final Activity thisActivity = activity ;
		int	progress = 1;
		ProgressBar	pBar ;
				
		setContentView(R.layout.load);
		
		pBar = (ProgressBar) findViewById(R.id.ProgressBar);
	
		String titleStr  = "" ;
		String authorStr = "" ;
		String statusStr = "" ;
		long   dcId      = 0  ;
		String tagName   = "" ;
		String xmlStr    = "" ;
		String line		 = "" ;
		String tmpStr    = "" ;
		
		//Parse the xml
		XmlPullParserFactory  xppf = XmlPullParserFactory.newInstance() ;
		XmlPullParser xpp = xppf.newPullParser();
		xpp.setInput(new FileReader(newXml));
		xpp = nextTag(xpp);
		/*
		 * event types:
		 * END_DOCUMENT = 1
		 * START_TAG = 2
		 * END_TAG = 3
		 * TEXT = 4
		 */
		int eventType = xpp.getEventType();
		try
		{
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				tagName = xpp.getName();
				if (eventType == XmlPullParser.START_TAG)
				{
					if (tagName.equals("author"))
					{
						xpp = nextTag(xpp);	
						eventType = xpp.getEventType(); //Debug
						tmpStr = xpp.getText(); //Debug
						if (xpp.getName().equals("name"))
						{
							tmpStr = xpp.getText();							
							xpp.next(); //Get to the content
							authorStr = xpp.getText();
						}
					}
					else if (tagName.equals("title"))
					{
						xpp = nextTag(xpp);
						titleStr = xpp.getText();
					}
					else if (tagName.equals("user-short-text-field-2"))
					{	
						xpp = nextTag(xpp);
						statusStr = xpp.getText();
					}
					else if (tagName.equals("id"))
					{
						xpp = nextTag(xpp);
						dcId = Long.parseLong(xpp.getText());
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
				progress = progress + 1;
				pBar.incrementProgressBy(progress);
				xpp = nextTag(xpp);
			}
		}
		catch(Exception ex)
		{
    		Context context = getApplicationContext();
    		CharSequence text = "XML Loop : " + ex.toString();
    		int duration = 5000 ; //Toast.LENGTH_LONG;
    		
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
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
	
	private XmlPullParser nextTag(XmlPullParser _xpp)
	{
		try
		{
			_xpp.nextTag();
			String tmpStr = _xpp.getName(); //Debug
			int eventType = _xpp.getEventType();
			while (eventType != XmlPullParser.START_TAG)
			{
				_xpp.next();
				tmpStr = _xpp.getName();	//Debug
				eventType = _xpp.getEventType();					
			}
		}
		catch(Exception ex)
		{
			Context context = getApplicationContext();
    		CharSequence text = "Next Tag : " + ex.toString();
    		int duration = 5000 ; //Toast.LENGTH_LONG;
    		
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
		}
		return _xpp ;
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
			
			cursor.close();
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
