package edu.hzuapps.androidworks.homeworks.net1314080903237;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class Net1314080903237_ContactsProvider extends ContentProvider
{
	private static final String TAG= "Net1314080903237_ContactsProvider";

	private Net1314080903237_DBHelper dbHelper;
	private SQLiteDatabase contactsDB;
	
	public static final String AUTHORITY = "com.yarin.android.provider.Net1314080903237_ContactsProvider";
	public static final String CONTACTS_TABLE = "contacts";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+CONTACTS_TABLE);
	
	/*=====================================================*/
	//
	public static final int CONTACTS = 1;
	public static final int CONTACT_ID = 2;
	private static final UriMatcher uriMatcher;	
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY,"contacts",CONTACTS);
		//
		uriMatcher.addURI(AUTHORITY,"contacts/#",CONTACT_ID);
	}
	/*=====================================================*/
	
	@Override
	public boolean onCreate()
	{
		dbHelper = new Net1314080903237_DBHelper(getContext());
		//
		contactsDB = dbHelper.getWritableDatabase();
		return (contactsDB == null) ? false : true;
	}
	
	//删除指定数据列
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs)
	{
		int count;
		switch (uriMatcher.match(uri))
		{
			case CONTACTS:
				count = contactsDB.delete(CONTACTS_TABLE, where, selectionArgs);
				break;
			case CONTACT_ID:
				String contactID = uri.getPathSegments().get(1);
				count = contactsDB.delete(CONTACTS_TABLE,
						Net1314080903237_ContactColumn._ID
										  + "=" + contactID 
										  + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""),
										  selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	//URI类型转换
	public String getType(Uri uri)
	{
		switch (uriMatcher.match(uri))
		{
			case CONTACTS:
				return "vnd.android.cursor.dir/vnd.yarin.android.mycontacts";
			case CONTACT_ID:
				return "vnd.android.cursor.item/vnd.yarin.android.mycontacts";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	//插入数据
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		if (uriMatcher.match(uri) != CONTACTS)
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null)
		{
			values = new ContentValues(initialValues);
			Log.e(TAG + "insert", "initialValues is not null");
		}
		else
		{
			values = new ContentValues();
		}
		//设置默认值
		if (values.containsKey(Net1314080903237_ContactColumn.NAME) == false)
		{
			values.put(Net1314080903237_ContactColumn.NAME, "");
		}
		if (values.containsKey(Net1314080903237_ContactColumn.MOBILENUM) == false)
		{
			values.put(Net1314080903237_ContactColumn.MOBILENUM, "");
		}
		if (values.containsKey(Net1314080903237_ContactColumn.HOMENUM) == false)
		{
			values.put(Net1314080903237_ContactColumn.HOMENUM, "");
		}
		if (values.containsKey(Net1314080903237_ContactColumn.ADDRESS) == false)
		{
			values.put(Net1314080903237_ContactColumn.ADDRESS, "");
		}
		if (values.containsKey(Net1314080903237_ContactColumn.EMAIL) == false)
		{
			values.put(Net1314080903237_ContactColumn.EMAIL, "");
		}
		if (values.containsKey(Net1314080903237_ContactColumn.BLOG) == false)
		{
			values.put(Net1314080903237_ContactColumn.BLOG, "");
		}
		Log.e(TAG + "insert", values.toString());
		long rowId = contactsDB.insert(CONTACTS_TABLE, null, values);
		if (rowId > 0)
		{
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			Log.e(TAG + "insert", noteUri.toString());
			return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	//查询数据
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Log.e(TAG + ":query", " in Query");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(CONTACTS_TABLE);

		switch (uriMatcher.match(uri))
		{
			case CONTACT_ID:
				qb.appendWhere(Net1314080903237_ContactColumn._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = Net1314080903237_ContactColumn._ID;
		}
		else
		{
			orderBy = sortOrder;
		}
		Cursor c = qb.query(contactsDB, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	//更新数据库
	public int update(Uri uri, ContentValues values, String where, String[] selectionArgs)
	{
		int count;
		Log.e(TAG + "update", values.toString());
		Log.e(TAG + "update", uri.toString());
		Log.e(TAG + "update :match", "" + uriMatcher.match(uri));
		switch (uriMatcher.match(uri))
		{
			case CONTACTS:
				Log.e(TAG + "update", CONTACTS + "");
				count = contactsDB.update(CONTACTS_TABLE, values, where, selectionArgs);
				break;
			case CONTACT_ID:
				String contactID = uri.getPathSegments().get(1);
				Log.e(TAG + "update", contactID + "");
				count = contactsDB.update(CONTACTS_TABLE, values, Net1314080903237_ContactColumn._ID + "=" + contactID
						+ (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}

