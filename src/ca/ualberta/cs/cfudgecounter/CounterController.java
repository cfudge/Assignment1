/********
 * this class provides
 * an interface to a CounterListModel,
 * providing many functions
 * that allow us to organize,save and load the list 
 * 
 */


package ca.ualberta.cs.cfudgecounter;

import java.io.BufferedReader;
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
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


public class CounterController implements CounterControllerInterface
{
	//only attribute is a CounterListModel
	private CounterListModel counterList;

	//simple constructor
	public CounterController()
	{
		super();
		counterList = new CounterListModel();
	}
	
	//set the counter list:
	public ArrayList<CounterModel> getCounterList(){
		return counterList.getCounterList();
	}

	//clear the counter list:
	public void clearCounterList(){
		counterList.clear();
	}
	
	//reorganizes the counters in the CounterListModel
	//from highest value to lowest through
	//a simple bubble sort:
	public void sort(){
		CounterModel counter1;
		CounterModel counter2;
		//for every counter:
		for(int i = 0; i < counterList.size(); i++){
			//for every counter after it:
			for(int i2 = (i+1); i2 < counterList.size(); i2++){
				counter1 = counterList.get(i);
				counter2 = counterList.get(i2);
				//if the counter at position i is lower than the counter
				//at position i2, swap them. counter1 will still
				//be correct on the next iteration because the counter1
				//variable is set at the beginning of the inside loop, as
				//opposed to on the outside loop.
				if(counter1.getValue() < counter2.getValue()){
					counterList.set(i, counter2);
					counterList.set(i2,  counter1);
				}
			}
		}
	}

	//Creates a dialog in the provided activity to add a counter
	//to the array
	public void addCounter(Activity activity){

		//create an alertdialog builder instance:
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);

		//set the fields of the dialog:
		alert.setTitle("Add Counter");
		alert.setMessage("Type Counter Name");

		//add an edittext to the dialog for the user to enter
		//the name in:
		final EditText counterText = new EditText(activity);
		alert.setView(counterText);

		//set the positive button with its text and set up an on click listener
		//to add the counter with the text provided when it is pressed:
		alert.setPositiveButton("Add Counter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//get the text from the editable:
				Editable counterName = counterText.getText();
				String counterNameString = counterName.toString();
				
				//set a creation date in a datelist:
				ArrayList<Calendar> dateList = new ArrayList<Calendar>();
				dateList.add(Calendar.getInstance());
				
				//initialize a new counter to 0 with the provided name and current
				//time and add it to the list of counters:
				counterList.add(new CounterModel(0, counterNameString, dateList));
			}
		});

		//also set a cancel negative button that does nothing but close the 
		//dialog window:
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();

	}

	
	//simpler method that adds a counter with the given fields and does not
	//prompt the user:
	public void addCounter(int value, String name, ArrayList<Calendar> dateList)
	{
		ArrayList<CounterModel> list = counterList.getCounterList();
		list.add(new CounterModel(value, name, dateList));
	}
	
	//loads a list of counters from the provided file and returns an adapter with those counters:
	public CustomCounterAdapter loadFromFile(String fileName, Activity callingActvity, ListView countersView){
		//said adapter:
		CustomCounterAdapter adapter;
		
		//to hold the fields of the counter after the loaded line is split
		String[] counterFields;
		
		//variables for each counter's fields:
		String counterName;
		int counterValue;
		ArrayList<Calendar> counterDates;
		
		//array of strings to hold the dates from the file in string form
		String[] counterDateStrings;
		
		//intermediate variable to convert strings into for adding into the 
		//arraylist of calendars:
		Date addedDate;
		
		try {
			//open a file handle using the provided activity:
			FileInputStream CounterFileStream = callingActvity.openFileInput(fileName);
			BufferedReader CountersIn = new BufferedReader(new InputStreamReader(CounterFileStream));
			//read the first line:
			String line = CountersIn.readLine();
			//until the end of the file is reached:
			while (line != null) {
				//reinitialize counterDates to a new arraylist
				counterDates = new ArrayList<Calendar>();
				
				//split the line into 0:the name, 1:the vale and 2:a comma-seperated
				//list of dates to construct a counter with:
				counterFields = line.split("awildcounterhasappeared31245");
				counterName = counterFields[0];
				counterValue = Integer.parseInt(counterFields[1]);
				counterDateStrings = counterFields[2].split(",");
				
				//for each date obtained for the counter:
				for(int i=0; i < counterDateStrings.length; i++)
				{
					//The date string was produced by converting a calendar to a date
					//and then calling toString, so I re-convert it to a calendar by parsing
					//it using the SimpleDateformat I saved it in and setting the re-produced date
					//to be the time for the calendar
					addedDate = new SimpleDateFormat("EEE MMM DD kk:mm:ss zzz yyyy").parse(counterDateStrings[i]);
					
					//set up a calendar with the date set to the date obtained above:
					Calendar addedCalendar = Calendar.getInstance();
					addedCalendar.setTime(addedDate);
					//add that calendar to the list:
					counterDates.add(addedCalendar);
				}
				//and add the counter with the obtained fields:
				this.addCounter(counterValue, counterName, counterDates);
				//read the next line:
				line = CountersIn.readLine();
				}
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		adapter = new CustomCounterAdapter(callingActvity,
				R.layout.count_list_element, this.getCounterList());
		countersView.setAdapter(adapter);
		return adapter;
		}

	//Saves all of the counters in the adapter to the provided filename. This is called
	//whenever a counter is changed, added or removed so that the user does not have to worry
	//about losing their counters.
	public void saveInFile(String fileName, Activity callingActivity, CustomCounterAdapter adapter) {
		try {
			//use the provided activity to delete the old file:
			callingActivity.deleteFile(fileName);
			//Start a new file handle to write the counters to:
			FileOutputStream CountersOut = callingActivity.openFileOutput(fileName,
					Context.MODE_APPEND);
			String writable;//the string to write to file
			ArrayList<Calendar> counterDates;//arraylist to hold dates from each counter
			//for each counter in the provided adapter:
			for (int i = 0; i < adapter.getCount(); i++)
			{	
				//get the counter at index i
				CounterModel counterToPrint = adapter.getItem(i);
				//add the name and value to the string to write and insert a regular expression
				//to split the string around when it is loaded:
				writable = new String(counterToPrint.getName()+"awildcounterhasappeared31245"
						+Integer.toString(counterToPrint.getValue())+"awildcounterhasappeared31245");
				//Turn each calendar in the counter into a date and subsequently to a string,
				//and add it to the writable string, separated by commas:
				counterDates = counterToPrint.getDateList();
				for(int i2 = 0; i2 < (counterDates.size()-1); i2++)
					writable += counterDates.get(i2).getTime().toString()+",";
				if(counterDates.size() != 0)
					//don't add a comma after the last date
					writable += counterDates.get(counterDates.size()-1).getTime().toString();
				//write the string to memory along with a newline to separate it from other counters
				//and continue to the next counter(or break):
				CountersOut.write((writable+"\n")
					.getBytes());
			}
			//close the file handle:
			CountersOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


