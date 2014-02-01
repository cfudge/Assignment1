package ca.ualberta.cs.cfudgecounter;

import java.io.BufferedReader;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;

/**************************************
 * 
 * @author Cole Fudge
 * The code in this class is for managing the activity.
 * The code directly involved with manipulating objects
 * is located within those objects themselves as much
 * as possible.
 */
public class CounterMainActivity extends Activity
{

	//ListView that lists the counters:
	private ListView countersView;
	
	//a custom adapter used to display the counters:
	private CustomCounterAdapter adapter;
	
	//string containing the name of the file to write to:
	private static final String COUNTERFILE = "counters.sav";
	
	//controller for manipulating counters:
	private static final CounterController counterController = new CounterController();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//countersView is a list view where the elements are horizontally
		//oriented layouts containing a TextView to display
		//the counter's name and a button that displays the current
		//value of the counter and increments the counter when pressed.
		countersView = (ListView) findViewById(R.id.CounterList);
		
		//The counter list will have a context menu which can
		//be brought up by long clicking any individual counter.
		//it is in this menu that the user has options to view statistics
		//or reset, rename or delete the counter.
		registerForContextMenu(countersView);
		
		//clearing the counter list so that I can 
		//initiate it with the latest information
		//in the files each time the activity is 
		//created:
		counterController.clearCounterList();
		
		//If the adapter already exists, I will clear
		//it so that load from file can initialize it with
		//the most recent information
		if(adapter != null)
			adapter.clearAdapter();
		
		//a function that initializes the list of counters
		//and adapter from the filename defined globally
		//as COUNTERFILE
		adapter = counterController.loadFromFile(COUNTERFILE, this, countersView);

	}
	
	//Besides the super function,
	//I do almost the same thing here as in
	//onCreate
	protected void onStart(){
		
		super.onStart(); 
		setContentView(R.layout.activity_main);
		countersView = (ListView) findViewById(R.id.CounterList);
		registerForContextMenu(countersView);
		counterController.clearCounterList();
		if(adapter != null)
			adapter.clearAdapter();
		adapter = counterController.loadFromFile(COUNTERFILE, this, countersView);

	}
	
	
	//update with most recent data
	//if another activity is invoked
	//that changes the data
	//also similar to onCreate and onStart
	protected void onResume(){
		super.onResume(); 
		setContentView(R.layout.activity_main);
		countersView = (ListView) findViewById(R.id.CounterList);
		registerForContextMenu(countersView);
		counterController.clearCounterList();
		if(adapter != null)
			adapter.clearAdapter();
		adapter = counterController.loadFromFile(COUNTERFILE, this, countersView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		//Inflating the main menu
		getMenuInflater().inflate(R.menu.main, menu);
		//countersView must be registered for the context
		//menu described above
		registerForContextMenu(countersView);
		return true;
	}
	
	//Inflating the context menu when required
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		//use info about the MenuItem to get the selected counter as an object
		//from its id(row in the adapter)
		AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		CounterModel selectedCounter = adapter.getItem((int) contextMenuInfo.id);
		
		//calling the appropriate function based on the id of the item selected:
		switch(item.getItemId())
		{
		case R.id.delete_menu_option:
			//remove the selected counter from the adapter:
			adapter.remove(selectedCounter);
			//update the view:
			adapter.notifyDataSetChanged();
			//use the controller to save changes:
			counterController.saveInFile(COUNTERFILE, this, adapter);
			return true;
		case R.id.rename_counter_option:
			//call rename function for the selected counter:
			selectedCounter.rename(this);
			//save changes:
			counterController.saveInFile(COUNTERFILE, this, adapter);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.view_stats_option:
			//Create an intent to pass to the counter's viewStatistics method
			Intent statView = new Intent(getApplicationContext(), StatisticsViewActivity.class);
			//use the CounterModel method for starting the view statistics activity:
			selectedCounter.viewStatistics(statView, this);
			return true;
		case R.id.reset_option:
			//call the selected counter's reset method:
			selectedCounter.reset();
			//save changes:
			counterController.saveInFile(COUNTERFILE, this, adapter);
			adapter.notifyDataSetChanged();
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.add_counter:
				//Use the controller to bring up an add counter dialog
				counterController.addCounter(this);
				//save changes:
				counterController.saveInFile(COUNTERFILE, this, adapter);
				adapter.notifyDataSetChanged();
				return true;
			case R.id.sort_counters:
				//call the controller method for sorting the counters from
				//largest count to lowest count:
				counterController.sort();
				//save:
				counterController.saveInFile(COUNTERFILE, this, adapter);
				adapter.notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	public void inc_counter(View incButtonView){
		//get the counter that was incremented by getting the tag
		//associated with the button pressed(this tag is set in the
		//custom counter adapter I created for specifically this purpose:
		CounterModel counterIncremented = (CounterModel)incButtonView.getTag();
		//call the counters increment method:
		counterIncremented.increment();
		//save, as usual:
		adapter.notifyDataSetChanged();
		counterController.saveInFile(COUNTERFILE, this, adapter);
	}
	

	

	
}