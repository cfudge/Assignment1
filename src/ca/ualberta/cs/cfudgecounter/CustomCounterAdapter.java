/******
 * this class implements a custom adapter
 * that is able to hold the button
 * and textview necessary for a counter.
 * it can be used just as any other adapter
 * can only that i have added a method to clear
 * it, a nested class to hold the textview,
 * button and counter model, and overrides the getview method to
 * set the text of the textview and button
 * and set them as their container(the nested class
 * CounterTag) as a tag to the view so that
 * these values can be accessed later when the view needs
 * to be set again
 */


package ca.ualberta.cs.cfudgecounter;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomCounterAdapter extends ArrayAdapter<CounterModel> {

	private List <CounterModel> counters;
	private int resource;
	private Context context;

	public CustomCounterAdapter(Context context, int resource,
			List<CounterModel> objects) {
		super(context, resource, objects);	
		this.resource = resource;
		this.context = context;
		this.counters = objects;
	}

	@Override
	public View getView(int position, View counterView, ViewGroup parent){		
		//new instance of the CounterTag private class to hold 
		//the data needed by the view:
		CounterTag tag = new CounterTag();
		
		//getting the layout inflater for the main activity:
		LayoutInflater counterInflater = ((Activity) context).getLayoutInflater();
		//resource here should be a layout with a button(CounterButton) and
		//a TextView(CounterName), inflating the counter's view:
		counterView = counterInflater.inflate(resource, parent, false);
		
		//get the counter from the position provided:
		CounterModel currentCounter = counters.get(position);
		
		//set up the TextView in the tag and set its text to the name
		//of the counter:
		tag.name = (TextView)counterView.findViewById(R.id.CounterName);
		tag.name.setText(currentCounter.getName());
		
		//Setting up the button(incrementer) in the tag to show the
		//current value of the counter:
		tag.incrementer = (Button)counterView.findViewById(R.id.CounterButton);
		tag.incrementer.setText(Integer.toString(currentCounter.getValue()));
		
		//setting the counter in the tag and setting the tag of
		//the (button)incrementer to be the counter itself so
		//that the counter can be obtained later when the button is 
		//pressed by calling getTag on the button:
		tag.counter = currentCounter;
		tag.incrementer.setTag(tag.counter);

		//set the tag for this counter's view to be the instance
		//of counter tag I built above:
		counterView.setTag(tag);
		
		return counterView;
	}
	
	//custom method for clearing the adapter.
	//iterates through and removes each element:
	public void clearAdapter(){
		for (int i=0; i < this.getCount(); i++)
			this.remove(this.getItem(i));
	}
	//private nested class used as a tag to counter views:
	private static class CounterTag {
		TextView name;
		Button incrementer;
		CounterModel counter;
	}
	
	
}
