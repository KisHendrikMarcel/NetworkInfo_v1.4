package de.KIS.networkinfo;

import java.util.ArrayList;
import java.util.List;
import de.KIS.networkinfo.MySQLiteHelper;
import de.KIS.networkinfo.Entry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class HistoryDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { "ID", "CellID", "LAC", "NetworkType", "Network", "Time"};	

	public HistoryDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Entry createEntry(int cid, int lac, String nType, String network, String time) {
		ContentValues values = new ContentValues();
		values.put("CellID", cid);
		values.put("LAC", lac);
		values.put("NetworkType", nType);
		values.put("Network", network);
		values.put("Time", time);

		long insertId = database.insert("HISTORY", null, values);

		Cursor cursor = database.query("HISTORY", allColumns, "ID = " + insertId, null, null, null, null);
		cursor.moveToFirst();

		return cursorToEntry(cursor);
	}

	protected List<Entry> getAllEntries() {
		List<Entry> EntriesList = new ArrayList<Entry>();
		EntriesList = new ArrayList<Entry>();

		Cursor cursor = database.query("HISTORY", allColumns, null, null, null, null, null);
		cursor.moveToFirst();

		if(cursor.getCount() == 0) return EntriesList;


		while (cursor.isAfterLast() == false) {
			Entry entry = cursorToEntry(cursor);
			EntriesList.add(entry);
			cursor.moveToNext();
		} 	

		cursor.close();

		return EntriesList;
	}


	private Entry cursorToEntry(Cursor cursor) {
		Entry entry = new Entry();
		entry.setId(cursor.getLong(0));
		entry.setCellID(cursor.getInt(1));
		entry.setLAC(cursor.getInt(2));
		entry.setnType(cursor.getString(3));
		entry.setNetwork(cursor.getString(4));
		entry.setTime(cursor.getString(5));

		return entry;
	}	
}