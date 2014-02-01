/****************
 * This is a class
 * that describes the interface that 
 * must be implemented to control a 
 * list of counters stored in a CounterListModel
 */



package ca.ualberta.cs.cfudgecounter;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.widget.ListView;


public interface CounterControllerInterface
{
	public ArrayList<CounterModel> getCounterList();
	public void addCounter(Activity activity);
	public void sort();
	public void addCounter(int value, String name, ArrayList<Calendar> dateList);
	public void clearCounterList();
	public CustomCounterAdapter loadFromFile(String fileName, Activity callingActvity, ListView countersView);
	public void saveInFile(String fileName, Activity callingActivity, CustomCounterAdapter adapter);

}
