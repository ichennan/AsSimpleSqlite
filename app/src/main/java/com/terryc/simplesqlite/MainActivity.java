package com.terryc.simplesqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

	Button btn_create, btn_insert, btn_close;
	EditText et_01;
	SQLiteDatabase db;
	int rowId;
	static final int PageSize = 10;
	private static final String TABLE_NAME = "table_test";
	private static final String COLUMN1 = "student_id";
	private static final String COLUMN2 = "student_name";
	SimpleAdapter sa;
	ArrayList<HashMap<String, String>> list_page_id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn_create = (Button) findViewById(R.id.btn_create);
		btn_create.setOnClickListener(new PrivateClickListener());
		btn_insert = (Button) findViewById(R.id.btn_insert);
		btn_insert.setOnClickListener(new PrivateClickListener());
		btn_close = (Button) findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new PrivateClickListener());
		et_01 = (EditText) findViewById(R.id.et_01);

		GridView gv_01 = (GridView) findViewById(R.id.gv_01);
		list_page_id = new ArrayList<HashMap<String, String>>();
		sa = new SimpleAdapter(
				MainActivity.this,
				list_page_id,
				R.layout.gridview_item,
				new String[]{"Item Text"},
				new int[]{R.id.item}
		);
		gv_01.setAdapter(sa);
		gv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				loadPage(position);
			}
		});
	}

	class PrivateClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btn_create) {
				createDatabase();
			} else if (v == btn_insert) {
				insertRecord();
				refreshPage();
			} else if (v == btn_close) {
				db.close();
			}
		}
	}

	void createDatabase() {
		db = SQLiteDatabase.create(null);
		Log.e("Database Path", db.getPath());
		Integer size = databaseList().length;
		Log.e("Database size", size.toString());
		String sql = "create table " + TABLE_NAME + " (" + COLUMN1 + " text not null, " +
				COLUMN2 + " text not null " + " );";
		try {
			db.execSQL("drop table if exists " + TABLE_NAME);
			db.execSQL(sql);
		} catch (Exception e) {
		}
	}

	void insertRecord() {
		rowId++;
		String sql = "insert into " + TABLE_NAME + " values('" + String.valueOf(rowId) +
				"', 'teacher')";
		try {
			db.execSQL(sql);
		} catch (Exception e) {

		}
	}

	void loadPage(int pageNumber) {
		String sql = "select * from " + TABLE_NAME + " Limit " + String.valueOf(PageSize) +
				" Offset " + String.valueOf(pageNumber * PageSize);
		Cursor rs = db.rawQuery(sql, null);
		for (int i = 0; i < rs.getCount(); i++) {
			rs.moveToPosition(i);
			et_01.setText(et_01.getText() + "/r/n" + rs.getString(0) + " ..." + rs.getString(1));
		}
		rs.close();
	}

	void refreshPage() {
		String sql = "select count(*) from " + TABLE_NAME;
		Cursor rs = db.rawQuery(sql, null);
		rs.moveToLast();
		long size = rs.getLong(0);
		Log.e("refresh size", String.valueOf(size));
		int totalPage = (int) size / PageSize + 1;
		list_page_id.clear();
		for (int i = 0; i < totalPage; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Item Text", "Tage " + String.valueOf(i));
			list_page_id.add(map);
		}
		sa.notifyDataSetChanged();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
