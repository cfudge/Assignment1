/**********
 * This class contains
 * a private ArrayList of CounterModels
 * that must be accessed either through
 * the public getter and setter methods laid out here
 * or, preferably, through a controller,
 * which implements a great deal more methods
 * for dealing with this list.
 */


package ca.ualberta.cs.cfudgecounter;

import java.util.ArrayList;

public class CounterListModel
{
	//only attribute is an arraylist of counter models
	private ArrayList<CounterModel> counterList;
	
	//constructor:
	public CounterListModel()
	{

		super();
		counterList = new ArrayList<CounterModel>();
	}

	
	//A bunch of getters and setters:
	
	//add a counter
	public void add(CounterModel counter){
		counterList.add(counter);
	}
	
	//supply the caller with the arraylist
	public ArrayList<CounterModel> getCounterList()
	{
	
		return counterList;
	}

	//Let the caller set the arraylist
	public void setCounterList(ArrayList<CounterModel> counterList)
	{
	
		this.counterList = counterList;
	}
	
	//clear the arraylist
	public void clear(){
		counterList.clear();
	}

	//return the size of the arraylist
	public int size() {
		return counterList.size();
	}

	//return element i of the arraylist
	public CounterModel get(int i) {
		return counterList.get(i);
	}

	//let the caller set element i of the arraylist to
	//a new counter:
	public void set(int i, CounterModel counter) {
		counterList.set(i, counter);
	}

}
