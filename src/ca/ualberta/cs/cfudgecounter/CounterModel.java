/***********
 * this class holds information
 * about a counter and implements
 * a variety of getter and setter
 * methods. Since these counters
 * are meant to be used in a list,
 * it is suggested that 
 * a CounterListModel be used
 * to contain them and a CounterController
 * is used to interface them.
 */

package ca.ualberta.cs.cfudgecounter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.widget.EditText;


public class CounterModel
{
	//attributes:
	private int value;
	private String name;
	private ArrayList<Calendar> dateList;
	
	//constructor:
	public CounterModel(int value, String name,ArrayList<Calendar> dateList){
		super();
		this.name = name;
		this.value = value;
		this.dateList = dateList;
		
	}
	
	//getters and setters:
	public ArrayList<Calendar> getDateList(){
		return this.dateList;
		
	}
	
	public void addDate(){
		dateList.add(Calendar.getInstance());
	}
	
	public void setDateList(ArrayList<Calendar> dateList) {
		this.dateList = dateList;
	}

	public int getValue()
	{
	
		return value;
	}
	
	
	public void setValue(int value)
	{
	
		this.value = value;
	}
	
	public String getName()
	{
	
		return name;
	}
	
	public void setName(String name)
	{
	
		this.name = name;
	}
	
	//just increments the counter:
	public void increment() {
		this.value++;
	}
	
	//reset the value to zero and keep only the
	//first calendar(when the counter was created)
	public void reset(){
		this.setValue(0);
		ArrayList<Calendar> newDateList = new ArrayList<Calendar>();
		newDateList.add(this.getDateList().get(0));
		this.setDateList(newDateList);
	}
	
	//calls a dialog on the activity provided
	//that prompts for a new counter name:
	public void rename(Activity activity){
		//create an instance of the dialog builder
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		
		//set the title and the prompt
		alert.setTitle("Rename Counter");
		alert.setMessage("Type Counter Name");
		//new edittext tied to the calling activity:
		final EditText counterText = new EditText(activity);
		//set the view of the alert to be an EditText(string prompt):
		alert.setView(counterText);
		
		//need a final CounterModel variable for the onclick function of the positive button
		final CounterModel counter = this;
		
		//set the button to press to confirm the counter's new name:
		alert.setPositiveButton("Rename Counter", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			//get the text from the editable and use it to set the counter's name
			Editable counterName = counterText.getText();
			String counterNameString = counterName.toString();
			counter.setName(counterNameString);//can't use "this", variable must be final
			 }
			});
			//set a negative button that does nothing when pressed, says cancel on it:
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
			});

			 alert.show();
		
	}
	
	//method called to start a statistics viewing activity from
	//a specified parent activity. puts the necessary attributes from the
	//counter into the intent and calls the activity:
	public void viewStatistics(Intent statView, Activity parentActivity){
		//add name and value to the intent:
		statView.putExtra("counterName", this.getName());
		statView.putExtra("counterValue", this.getValue());
		
		//generate an arrayList of strings for the intent since the
		//intent cannot hold Calendars:
		ArrayList<String> dateStringList = new ArrayList<String>();
		ArrayList<Calendar> counterDates = this.getDateList();
		//for each date in the counter, convert it into a string and add it to the list:
		for(int i=0; i < counterDates.size(); i++)
			dateStringList.add(counterDates.get(i).getTime().toString());
		//pack the list into the intent:
		statView.putStringArrayListExtra("counterDates", dateStringList);
		//call the intent from the specified parent activity:
		parentActivity.startActivity(statView);
	}

}
