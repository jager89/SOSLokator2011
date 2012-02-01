package edu.feri.jager.soslokator;

import java.util.List;

import edu.feri.jager.soslokator.structure.MyLocation;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PathListActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener{
	MyApplication mainApp;
	Menu mMenu;
	View lastSelectedView = null;
	int lastSelectedItem;
	Drawable selectedBackgrund;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainApp = (MyApplication) getApplication();
//		mainApp.fillLocationsFromDB();			

//		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getList()));

		lastSelectedItem = -1;

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mainApp.fillLocationsFromDB();	
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getList()));

	}
	
	private String[] getList() {
		List<MyLocation> vec = mainApp.getListLocations();

		if(vec == null || vec.size() == 0)
			return new String[]{getString(R.string.empty_path_list)};

		String[] str = new String[vec.size()];
		for(int i = 0; i < vec.size(); i++) {

			String displayName = vec.get(i).getDisplayName();
			String phoneNumber = vec.get(i).getPhoneNumber();
			String timestamp = vec.get(i).getTimestamp();

			StringBuilder builder = new StringBuilder();

			if(displayName.length() > 0) {
				builder.append(displayName);
			}
			else {
				builder.append(phoneNumber);
			}
			builder.append(" [" + timestamp + "]");
			str[i] = builder.toString();
		}

		return str;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(mainApp != null && mainApp.getListLocations() != null && mainApp.getListLocations().size() > 0) {

			Intent intent = new Intent(PathListActivity.this, PathActivity.class);
			intent.putExtra("dbData", mainApp.getListLocations().get(position).getArray());
			startActivity(intent);
		}
	}

		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final int pos = position;
			new AlertDialog.Builder(PathListActivity.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Brisasnje sporočila")
			.setMessage("Ali ste prepričani, da želite odstraniti sporočilo?")
			.setPositiveButton("Da", new DialogInterface.OnClickListener() {
	
				public void onClick(DialogInterface dialog, int which) {
					long id = mainApp.getListLocations().get(pos).getId();

//					long id = mainApp.getListLocations().get(getSelectedItemPosition()).getId();
					mainApp.removeLocationFromDB(id);
					mainApp.getListLocations().remove(pos);

					//					mainApp.getListLocations().remove(getSelectedItemPosition());
					PathListActivity.this.setListAdapter(new ArrayAdapter<String>(PathListActivity.this, android.R.layout.simple_list_item_1, getList()));  
				}
	
			})
			.setNegativeButton("Ne", null)
			.show();
	
			return true;	
		}
}