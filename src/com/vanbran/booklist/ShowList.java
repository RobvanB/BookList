package com.vanbran.booklist;
	
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ShowList extends ListActivity {	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Cursor cursor;
		String title;
		String author;
		String status;

		try{
			DBAdapter db = new DBAdapter(this);
			
			Bundle extras = getIntent().getExtras();
			title = extras.getString("title");
			author = extras.getString("author");
			status = extras.getString("status");
					
			db.open();
			cursor = db.searchBooks(author, title, status);
			//Get all rows from Db and create the item list
			startManagingCursor(cursor);
	        	
        	//Create an array to display the fields in the list (these should be the actual fieldnames in the table)
        	String[] from = new String[]{"Title", "Author", "Status"};
        	
        	//Another array used for the fields we want to bind the list fields to
        	int[] to = new int[]{R.id.TitleVal, R.id.AuthorVal, R.id.StatusVal};
        	
        	//Create cursor adapter and set it do display
        	SimpleCursorAdapter allBooks = new SimpleCursorAdapter(this, R.layout.booklist, cursor, from, to);
        	setListAdapter(allBooks);
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
}