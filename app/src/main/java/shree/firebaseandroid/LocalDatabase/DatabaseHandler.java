package shree.firebaseandroid.LocalDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import shree.firebaseandroid.fragments.FragmentProjects;

/**
 * Created by sada on 1/31/2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wotrLocaldb";

    // Contacts table name
    private static final String TABLE_CONTACTS = "projects";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PID = "pid";
    private static final String KEY_PNAME = "pname";
    private static final String KEY_PDESC = "pdesc";
    private static final String KEY_PCREATIONDATE = "pcreatedate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
       String CREATE_TABLE_CONTACTS="CREATE TABLE " + TABLE_CONTACTS + "("
               + KEY_ID +" INTEGER PRIMARY KEY,"
               + KEY_PNAME +" TEXT,"
               + KEY_PDESC +" TEXT,"
               + KEY_PCREATIONDATE  +" TEXT" + ")";
        db.execSQL(CREATE_TABLE_CONTACTS);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    //Insert values to the table contacts
    public void addContacts(FragmentProjects.ProjectDetails projectDetails){
      SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_PID,projectDetails.getProgramid());
        values.put(KEY_PNAME, projectDetails.getProgramName());
        values.put(KEY_PDESC, projectDetails.getShortDescription() );
        values.put(KEY_PCREATIONDATE, projectDetails.getCreationTime());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    /**
     *Getting All Contacts
     **/

    public List<FragmentProjects.ProjectDetails> getAllContacts() {
        List<FragmentProjects.ProjectDetails> projectList = new ArrayList<FragmentProjects.ProjectDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FragmentProjects.ProjectDetails projectDetails = new FragmentProjects.ProjectDetails();
                projectDetails.setProgramid(cursor.getString(0));
                projectDetails.setProgramName(cursor.getString(1));
                projectDetails.setShortDescription(cursor.getString(2));
                projectDetails.setCreationTime(cursor.getString(3));

                // Adding contact to list
                projectList.add(projectDetails);
            } while (cursor.moveToNext());
        }

        // return contact list
        return projectList;
    }



    /**
     *Updating single contact
     **//*

    public int updateContact(Contact contact, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FNAME, contact.getFName());
        values.put(KEY_SNAME, contact.getSName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    *//**
     *Deleting single contact
     **//*

    public void deleteContact(int Id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(Id) });
        db.close();
    }*/

}
