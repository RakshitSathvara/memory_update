package com.narmada.measure.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.narmada.measure.face_recognization.FaceClassifier;
import com.narmada.measure.face_recognization.Supervisor;
import com.narmada.measure.face_recognization.livefeed.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyFaces.db";

    public static final String SUPERVISOR_TABLE_NAME = "supervisor";
    public static final String SUPERVISOR_COLUMN_ID = "id";
    public static final String SUPERVISOR_COLUMN_CODE = "code";
    public static final String SUPERVISOR_COLUMN_NAME = "name";
    public static final String SUPERVISOR_COLUMN_EMBEDDING = "embedding";
    public static final String SUPERVISOR_COLUMN_IMAGE = "image";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE supervisor (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "code TEXT NOT NULL UNIQUE, " +
                        "name TEXT, " +
                        "embedding TEXT, " +
                        "image TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS supervisor");
        onCreate(db);
    }

    public boolean insertFace(String name, String code, Object embedding, String image) {
        float[][] floatList = (float[][]) embedding;
        String embeddingString = "";
        for (Float f : floatList[0]) {
            embeddingString += f.toString() + ",";
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUPERVISOR_COLUMN_NAME, name);
        contentValues.put(SUPERVISOR_COLUMN_CODE, code);
        contentValues.put(SUPERVISOR_COLUMN_EMBEDDING, embeddingString);
        contentValues.put(SUPERVISOR_COLUMN_IMAGE, image);
        long result = db.insert(SUPERVISOR_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean insertFaceFromApi(String name, String code, String embedding, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUPERVISOR_COLUMN_NAME, name);
        contentValues.put(SUPERVISOR_COLUMN_CODE, code);
        contentValues.put(SUPERVISOR_COLUMN_EMBEDDING, embedding);
        contentValues.put(SUPERVISOR_COLUMN_IMAGE, image);
        long result = db.insert(SUPERVISOR_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from supervisor where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SUPERVISOR_TABLE_NAME);
        return numRows;
    }

    public boolean updateFace(Integer id, String name, String embedding) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUPERVISOR_COLUMN_NAME, name);
        contentValues.put(SUPERVISOR_COLUMN_EMBEDDING, embedding);
        db.update(SUPERVISOR_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteSupervisor(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SUPERVISOR_TABLE_NAME, "code = ? ", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public HashMap<String, FaceClassifier.Recognition> getAllFaces() {
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id, name, code, embedding from supervisor", null);
        res.moveToFirst();

        HashMap<String, FaceClassifier.Recognition> registered = new HashMap<>();
        while (res.isAfterLast() == false) {
            String embeddingString = res
                    .getString(res.getColumnIndex(SUPERVISOR_COLUMN_EMBEDDING));
            String[] stringList = embeddingString.split(",");
            ArrayList<Float> embeddingFloat = new ArrayList<>();
            for (String s : stringList) {
                embeddingFloat.add(Float.parseFloat(s));
            }
            float[][] bigArray = new float[1][1];
            float[] floatArray = new float[embeddingFloat.size()];
            for (int i = 0; i < embeddingFloat.size(); i++) {
                floatArray[i] = embeddingFloat.get(i);
            }
            bigArray[0] = floatArray;
            String name = res.getString(res.getColumnIndex(SUPERVISOR_COLUMN_NAME));
            String code = res.getString(res.getColumnIndex(SUPERVISOR_COLUMN_CODE));
            FaceClassifier.Recognition recognition = new FaceClassifier.Recognition(name, code, bigArray);
            registered.putIfAbsent(recognition.getTitle(), recognition);
            res.moveToNext();
        }

        Log.d("tryRL", "rl=" + registered.size());
        return registered;
    }

    @SuppressLint("Range")
    public String getSingleFaceImage(String empCode) {
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select image from supervisor WHERE code = ? ";
        String[] bindArgs = new String[]{empCode};
        Cursor cursor = db.rawQuery(query, bindArgs);

        String result = null;
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(SUPERVISOR_COLUMN_IMAGE));
            break;
        }

        cursor.close();

        return result;
    }

    @SuppressLint("Range")
    public List<String> getExistingSupervisorIds() {

        List<String> idList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT code FROM supervisor";
        Cursor cursor = db.rawQuery(query, null);

        // Loop through the cursor to access data
        while (cursor.moveToNext()) {
            String code = cursor.getString(cursor.getColumnIndex(SUPERVISOR_COLUMN_CODE));
            idList.add(code);
        }

        cursor.close(); // Close the cursor

        return idList;
    }

    @SuppressLint("Range")
    public List<Supervisor> getAllSupervisor(String supervisorId) {

        List<Supervisor> idList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from supervisor WHERE code = ? ";
        String[] bindArgs = new String[]{supervisorId};
        Cursor cursor = db.rawQuery(query, bindArgs);

        // Loop through the cursor to access data
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(SUPERVISOR_COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(SUPERVISOR_COLUMN_NAME));
            String code = cursor.getString(cursor.getColumnIndex(SUPERVISOR_COLUMN_CODE));
            String image = cursor.getString(cursor.getColumnIndex(SUPERVISOR_COLUMN_IMAGE));
            idList.add(new Supervisor(id, name, code, image));
        }

        cursor.close(); // Close the cursor

        return idList;
    }

    public void deleteSupervisorTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.execSQL("DELETE FROM " + DBHelper.SUPERVISOR_TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}