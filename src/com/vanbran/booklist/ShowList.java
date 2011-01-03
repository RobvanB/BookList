package com.vanbran.booklist;
	
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ShowList extends ListActivity {	
	
	private static final int DELETE_ID = Menu.FIRST;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showList();
	}
	
private void showList()
{
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
        	if (allBooks.getCount() == 0)
        	{
        		Context context = getApplicationContext();
    			CharSequence text = "No Data";
    			int duration = 10000; //Toast.LENGTH_LONG ;
    			
    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
        	}else
        	{
        		setListAdapter(allBooks);
        		registerForContextMenu(getListView());	//Needed for the 'delete' long press menu
        	}
	    }
		catch (Exception ex)
    	{
    		Context context = getApplicationContext();
    		CharSequence text = ex.toString();
    		int duration = 500000 ; //Toast.LENGTH_LONG;
    		
    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
    	}
	}//End ShowList

//Add delete to the long-press menu
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	super.onCreateContextMenu(menu, v, menuInfo);
	menu.add(0,DELETE_ID, 0, R.string.delete);
}	


@Override
public boolean onContextItemSelected(MenuItem item) {
	switch(item.getItemId()) {
	case DELETE_ID:
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	
		DBAdapter db = new DBAdapter(this);
		db.open();
		db.deleteBook(info.id);
		db.close();
		showList();
		return true;
	}
	return super.onContextItemSelected(item);
	}
}