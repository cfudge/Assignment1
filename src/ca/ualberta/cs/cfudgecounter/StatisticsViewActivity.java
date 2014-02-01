/*********
 * This class is used to implement
 * a screen where the user can view
 * statistics for how many times the counter
 * has been incremented in each hour, day, week
 * or month it was incremented in, as well as 
 * some general info. It utilizes a linear layout 
 * of five scrollViews with textview children 
 * that share the screen evenly(i.e
 * each has a weight of 1 and a height of "0dp")
 */

package ca.ualberta.cs.cfudgecounter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;



public class StatisticsViewActivity extends Activity {

	//the five textviews that are used:
	private TextView general;
	private TextView hour;
	private TextView day;
	private TextView week;
	private TextView month; 

	//the intent provided by the calling activity:
	private Intent intent;

	//the extras, which needs to have
	//an arraylist of strings labelled "counterdates":
	private Bundle extras;

	//arraylists used in handling the dates:
	private ArrayList<String> dateStringList;
	private ArrayList<Calendar> dateList;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set up the layout from the xml file:
		setContentView(R.layout.activity_statistics_view);

		//the following was auto-generated, I believe it adds a 
		//button in the action bar that does the same as the
		//back button. Either way, it takes the user back to
		//the main activity with the list of counters:
		// Show the Up button in the action bar.
		setupActionBar();

		//get the intent provided by the calling activity
		intent = getIntent();
		//get the arraylist of strings from the counter, packaged by the calling
		//activity:
		extras = intent.getExtras();
		dateStringList = extras.getStringArrayList("counterDates");

		//converting the arraylist of strings into an arraylist of calendars
		//for easier use:
		dateList = new ArrayList<Calendar>();
		Calendar addedCalendar;

		//for every date string in the array:
		for(int i = 0; i < dateStringList.size(); i++){
			try {
				addedCalendar = Calendar.getInstance();

				//The date string was produced by converting a calendar to a date
				//and then calling toString, so I re-convert it to a calendar by parsing
				//it using the SimpleDateformat I saved it in and setting the re-produced date
				//to be the time for the calendar
				addedCalendar.setTime(new SimpleDateFormat("EEE MMM DD kk:mm:ss zzz yyyy").parse(dateStringList.get(i)));
				dateList.add(addedCalendar);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		general = (TextView) findViewById(R.id.Stats_general);
		hour = (TextView) findViewById(R.id.Stats_hour);
		day = (TextView) findViewById(R.id.Stats_day);
		week = (TextView) findViewById(R.id.Stats_week);
		month = (TextView) findViewById(R.id.Stats_month);

		//calling each of the methods to produce the statistics and set
		//them to their respective views:
		setGeneralInfo();
		setHourStats();
		setDayStats();
		setWeekStats();
		setMonthStats();

	}

	//Simply fills the top field with the counter name, vale, and date created:
	private void setGeneralInfo(){
		String generalInfoText = extras.getString("counterName")+":\n";
		generalInfoText += "Counter value: "+Integer.toString(extras.getInt("counterValue"))+"\n";
		generalInfoText += "Counter Created: " +"\n"+ dateStringList.get(0);
		general.setText(generalInfoText);
	}

	private void setHourStats(){
		//header:
		String hourStatsText = "Counter statistics by hour:\n";
		//This hashmap is used to avoid adding an hour's statistics twice:
		HashMap<String, Integer> hourNumberPairs = new HashMap<String, Integer>();


		String hourString;

		//ints used to compare two increments and see if they were in the same hour:
		int hourOfDay;
		int dayOfYear;
		int year;

		int hourOfDay2;
		int dayOfYear2;
		int year2;

		//Calendars to hold the times of both the increments for comparison:
		Calendar timeIncremented;
		Calendar timeIncremented2;

		//integer that will be incremented to get the total number of times
		//the counter was incremented in that hour:
		int incsForHour;
		//for every increment:
		for(int i = 1; i < dateList.size(); i++)
		{
			timeIncremented = dateList.get(i);
			//get a string representing the hour in which the increment occured:
			hourString = Integer.toString(timeIncremented.get(Calendar.YEAR))+" "
					+ timeIncremented.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) +" "+
					Integer.toString(timeIncremented.get(Calendar.DAY_OF_MONTH))+" " + Integer.toString(timeIncremented.get(Calendar.HOUR_OF_DAY))
					+":00";
			//if this hour has already been considered, move on to the next hour. the increments should be in order from 
			//earliest to latest since they were, of course added in that order, thus I needn't be concerned with
			//order if I simply iterated through them in order.
			if(hourNumberPairs.containsKey(hourString))
				continue;
			//get the necessary fields of the first increment using the Calendar.get() method:
			hourOfDay = timeIncremented.get(Calendar.HOUR_OF_DAY);
			dayOfYear = timeIncremented.get(Calendar.DAY_OF_YEAR);
			year = timeIncremented.get(Calendar.YEAR);
			//start at one increment for this hour(the outer increment):
			incsForHour = 1;
			//for every increment at a higher index than the outer increment:
			for(int i2 = (i+1); i2 < dateList.size(); i2++){
				timeIncremented2 = dateList.get(i2);
				//increment 2's fields:
				hourOfDay2 = timeIncremented2.get(Calendar.HOUR_OF_DAY);
				dayOfYear2 = timeIncremented2.get(Calendar.DAY_OF_YEAR);
				year2 = timeIncremented2.get(Calendar.YEAR);	
				//If they were incremented in the same hour of the same day of the same year,
				//we can induce that they were incremented within the hour and we increment
				//the number of incremnets for the hour under consideration:
				if((hourOfDay==hourOfDay2) && (year == year2) && (dayOfYear == dayOfYear2))
					incsForHour++;
			}
			//adding the produced hourString and the number of increments in that hour to the list
			//of statistics:
			hourStatsText += hourString+ ": "+Integer.toString(incsForHour)+"\n";
			//add this hour to the hashmap:
			hourNumberPairs.put(hourString, incsForHour);
		}
		//Finally set the text for the hour field to the list of hours
		//obtained:
		hour.setText(hourStatsText);

	}


	//This method is extremely similar to the setHourStats() method and thus needs
	//no further commenting since the algorithm is the same:
	private void setDayStats(){
		String dayStatsText = "Counter statistics by day:\n";
		HashMap<String, Integer> dayNumberPairs = new HashMap<String, Integer>();
		String dayString;
		int dayOfYear;
		int year;

		int dayOfYear2;
		int year2;

		Calendar timeIncremented;
		Calendar timeIncremented2;

		int incsForDay;
		for(int i = 1; i < dateList.size(); i++)
		{
			timeIncremented = dateList.get(i);
			dayString = Integer.toString(timeIncremented.get(Calendar.YEAR))+" "
					+ timeIncremented.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) +" "+
					Integer.toString(timeIncremented.get(Calendar.DAY_OF_MONTH));
			if(dayNumberPairs.containsKey(dayString))
				continue;
			dayOfYear = timeIncremented.get(Calendar.DAY_OF_YEAR);
			year = timeIncremented.get(Calendar.YEAR);
			incsForDay = 1;
			for(int i2 = (i+1); i2 < dateList.size(); i2++){
				timeIncremented2 = dateList.get(i2);

				dayOfYear2 = timeIncremented2.get(Calendar.DAY_OF_YEAR);
				year2 = timeIncremented2.get(Calendar.YEAR);			
				if((year == year2) && (dayOfYear == dayOfYear2))
					incsForDay++;
			}

			dayStatsText += dayString+ ": "+Integer.toString(incsForDay)+"\n";
			dayNumberPairs.put(dayString, incsForDay);
		}
		day.setText(dayStatsText);

	}

	//weekstats is also similar, but requires some messing
	//around to find the appropriate date for the beginning of the week.
	private void setWeekStats(){
		String weekStatsText = "Counter statistics by week:\n";
		HashMap<String, Integer> weekNumberPairs = new HashMap<String, Integer>();
		String weekString;

		int weekOfYear;
		int year;

		int weekOfYear2;
		int year2;

		String yearPrinted;
		String monthPrinted;
		String dayPrinted;

		Calendar timeIncremented;
		Calendar timeIncremented2;

		int incsForWeek;
		for(int i = 1; i < dateList.size(); i++)
		{
			timeIncremented = dateList.get(i);
			//if the incrementation occured near the beginning of a month, there is some work required
			//to find the date that needs to be printed, other than this the algorithm is essentially as
			//described in setHourStats:
			if((timeIncremented.get(Calendar.DAY_OF_MONTH) - (timeIncremented.get(Calendar.DAY_OF_WEEK) -1)) < 1)
			{
				yearPrinted = Integer.toString(timeIncremented.get(Calendar.YEAR));
				switch(timeIncremented.get(Calendar.MONTH))
				{
				case(1):
					monthPrinted = "Dec";
				yearPrinted = Integer.toString(Integer.parseInt(yearPrinted) - 1);
				//in December's case we need to also go one year back.
				break;
				case(2):
					monthPrinted = "Jan";
				break;	
				case(3):
					monthPrinted = "Feb";
				break;	

				case(4):
					monthPrinted = "Mar";
				break;	

				case(5):
					monthPrinted = "Apr";
				break;	

				case(6):
					monthPrinted = "May";
				break;	

				case(7):
					monthPrinted = "Jun";
				break;	

				case(8):
					monthPrinted = "Jul";
				break;	

				case(9):
					monthPrinted = "Aug";
				break;	

				case(10):
					monthPrinted = "Sep";
				break;	

				case(11):
					monthPrinted = "Oct";
				break;	

				default:
					monthPrinted = "Nov"; //12 is the only remaining possibility and without a default case
					//the compiler may complain that monthPrinted may not have a value.
				}
				//need to get the day of the month for the Sunday before the date the counter
				//was incremented. If this Sunday is in the month before, then we need to 
				//go back the number of days remaining in the month to the 31st, but then still back
				//some days to the beginning of the week because the day of the month was less than
				//the day of the week. Thus, the number of days back into the previous month we must
				//go to find the most recent Sunday is given by the difference between the nth day 
				//of the week we are on and the mth day of the month we are on.
				dayPrinted = Integer.toString(31 + (timeIncremented.get(Calendar.DAY_OF_MONTH) - (timeIncremented.get(Calendar.DAY_OF_WEEK) - 1)));
			}
			else
			{
				yearPrinted = Integer.toString(timeIncremented.get(Calendar.YEAR));
				monthPrinted = timeIncremented.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
				dayPrinted = Integer.toString(timeIncremented.get(Calendar.DAY_OF_MONTH) - (timeIncremented.get(Calendar.DAY_OF_WEEK) -1));
			}
			weekString = "Week of " + yearPrinted +" "
					+ monthPrinted +" "+
					dayPrinted;
			if(weekNumberPairs.containsKey(weekString))
				continue;
			weekOfYear = timeIncremented.get(Calendar.WEEK_OF_YEAR);
			year = timeIncremented.get(Calendar.YEAR);
			incsForWeek = 1;
			for(int i2 = (i+1); i2 < dateList.size(); i2++){
				timeIncremented2 = dateList.get(i2);

				weekOfYear2 = timeIncremented2.get(Calendar.WEEK_OF_YEAR);
				year2 = timeIncremented2.get(Calendar.YEAR);			
				if((year == year2) && (weekOfYear == weekOfYear2))
					incsForWeek++;
			}

			weekStatsText += weekString+ ": "+Integer.toString(incsForWeek)+"\n";
			weekNumberPairs.put(weekString, incsForWeek);
		}
		week.setText(weekStatsText);

	}

	
	//See setHourstats. The algorithm is the same.
	private void setMonthStats(){
		String monthStatsText = "Counter statistics by month:\n";
		HashMap<String, Integer> monthNumberPairs = new HashMap<String, Integer>();
		String monthString;
		int monthOfYear;
		int year;

		int monthOfYear2;
		int year2;

		Calendar timeIncremented;
		Calendar timeIncremented2;

		int incsForMonth;
		for(int i = 1; i < dateList.size(); i++)
		{
			timeIncremented = dateList.get(i);
			monthString = Integer.toString(timeIncremented.get(Calendar.YEAR))+" "
					+ timeIncremented.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
			if(monthNumberPairs.containsKey(monthString))
				continue;
			monthOfYear = timeIncremented.get(Calendar.MONTH);
			year = timeIncremented.get(Calendar.YEAR);
			incsForMonth = 1;
			for(int i2 = (i+1); i2 < dateList.size(); i2++){
				timeIncremented2 = dateList.get(i2);

				monthOfYear2 = timeIncremented2.get(Calendar.MONTH);
				year2 = timeIncremented2.get(Calendar.YEAR);			
				if((year == year2) && (monthOfYear == monthOfYear2))
					incsForMonth++;
			}

			monthStatsText += monthString+ ": "+Integer.toString(incsForMonth)+"\n";
			monthNumberPairs.put(monthString, incsForMonth);
		}
		month.setText(monthStatsText);
	}


	//all auto-generated stuff from this point on:
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
