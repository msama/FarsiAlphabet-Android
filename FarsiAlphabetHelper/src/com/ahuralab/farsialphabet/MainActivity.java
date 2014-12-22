package com.ahuralab.farsialphabet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import com.ahuralab.farsialphabet.StatsProxy;

/**
 * @author michele.sama@gmail.com
 * @author panteha.s@gmail.com
 * 
 */
public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	// private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;

	private StatsProxy stats;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView list = (ListView) findViewById(R.id.letterList);
		list.setAdapter(new LetterListAdapter(this));

		if (stats == null) {
			stats = new StatsProxy(getApplicationContext());
		}

		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
		mDrawerList = (ListView) findViewById(R.id.main_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// mDrawerLayout.setDrawerListener(mDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerLayout
				.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerClosed(View view) {

					}

					@Override
					public void onDrawerOpened(View drawerView) {
						showDialog(
								"New words and usefull sentences will be added shortly!",
								getString(R.string.attention));
						getActionBar().setTitle(mDrawerTitle);
						invalidateOptionsMenu(); // creates call to
						// onPrepareOptionsMenu()

					}
				});

		mDrawerLayout.closeDrawer(mDrawerList);
		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	private void createDialog(final int position, final String[] tooltips) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.tooltips_title);
		builder.setIcon(R.drawable.ic_stat_tooltips);
		builder.setMessage(tooltips[position]);
		// Add the buttons
		builder.setNegativeButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						dialog.dismiss();

					}
				});
		builder.setPositiveButton(R.string.next,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
						dialog.dismiss();
						createDialog((position + 1) % tooltips.length, tooltips);
					}
				});

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/*
	 * private void showRateUsDialogIfNecessary() { AppPreferencesWrapper prefs
	 * = new AppPreferencesWrapper(this); int counter =
	 * prefs.incrementAppOpenedCounter();
	 * 
	 * if (counter % 5 == 0 && !prefs.getShouldAskToRate()) { // Show rate us
	 * dialog AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 * builder.setTitle(R.string.please_rate_use_title)
	 * .setMessage(R.string.please_rate_use_text)
	 * .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int id) { // Go and rate Uri
	 * uri = Uri.parse("market://details?id=" + MainActivity.this
	 * .getPackageName()); Intent goToMarket = new Intent( Intent.ACTION_VIEW,
	 * uri); try { MainActivity.this .startActivity(goToMarket);
	 * AppPreferencesWrapper prefs = new AppPreferencesWrapper(
	 * MainActivity.this); prefs.setShouldAskToRate(false); } catch
	 * (ActivityNotFoundException e) { Log.w("MainActivity",
	 * "Could not open marketplace application."); } } })
	 * .setNegativeButton(R.string.no_thanks, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int id) { // User cancelled the dialog AppPreferencesWrapper
	 * prefs = new AppPreferencesWrapper( MainActivity.this);
	 * prefs.setShouldAskToRate(false); } })
	 * .setNeutralButton(R.string.remind_me_later, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int id) { // User want to be reminded later // Nothing to do. }
	 * }); // Create the AlertDialog object and show it builder.show(); } }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			return true;
		}

		case R.id.action_numbers: {
			Intent intent = new Intent(this.getApplicationContext(),
					NumberPracticeActivity.class);
			startActivity(intent);
			return true;
		}

		case R.id.action_question: {
			String[] tooltips = getResources().getStringArray(R.array.tooltips);
			createDialog((int) Math.floor(Math.random() * tooltips.length),
					tooltips);
			return true;
		}
		case R.id.action_about: {
			Intent intent = new Intent(this.getApplicationContext(),
					AboutActivity.class);
			startActivity(intent);
			return true;
		}

		default: {
			return super.onOptionsItemSelected(item);
		}
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			selectItem(position);
		}

	}

	private void selectItem(int position) {
		// update the main content by replacing fragments

		Fragment fragment = new FarsiFragment();
		Bundle args = new Bundle();
		args.putInt(FarsiFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stats.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		stats.onResume();
	}

	protected void showDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(message);
		// Create the AlertDialog object and show it
		builder.show();
	}

}
