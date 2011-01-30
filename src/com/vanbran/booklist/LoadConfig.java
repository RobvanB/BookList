package com.vanbran.booklist;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.widget.Toast;

public class LoadConfig
{
	private static final String CONSKEYTAG = "consumer_key";
	private static final String CONSSECTAG = "consumner_secret";
	private static String CONSKEY = "" ;
	private static String CONSSEC = "" ;
		
	public void init(Context _context)
	{
		XmlResourceParser parser = _context.getResources().getXml(R.xml.config);

		try 
		{
		    int eventType = parser.getEventType();
		
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		        String name = null;
		
		        switch (eventType){
		            case XmlPullParser.START_TAG:
		                name = parser.getName().toLowerCase();
		
		                if (name.equals(CONSKEYTAG)) 
		                {
		                	parser.next();
		                    CONSKEY = parser.getText();
		                }else if (name.equals(CONSSECTAG))
		        		{
		                	parser.next();
		        			CONSSEC = parser.getText();
		        		}
		                break;
		            case XmlPullParser.END_TAG:
		                name = parser.getName();
		                break;
		        }
		        eventType = parser.next();
		    }
		}
		catch (XmlPullParserException ex) 
		{
			CharSequence text = "LoadConfig XML Parser Exception: " + ex.toString() ;
			int duration = 500000; //Toast.LENGTH_LONG ;
		
			Toast toast = Toast.makeText(_context, text, duration);
			toast.show();
		}
		catch (IOException ex) {
			CharSequence text = "LoadConfig IO Exception: " + ex.toString() ;
			int duration = 500000; //Toast.LENGTH_LONG ;
			
				Toast toast = Toast.makeText(_context, text, duration);
				toast.show();
			}
			finally 
			{
			    parser.close();
			}
	}
	
	public String ConsKey()
	{
		return CONSKEY;
	}
	
	public String ConsSec()
	{
		return CONSSEC;
	}
}
