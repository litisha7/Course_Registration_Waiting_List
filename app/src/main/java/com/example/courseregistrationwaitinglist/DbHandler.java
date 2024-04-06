package com.example.courseregistrationwaitinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "registration_list_db";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE = "CREATE TABLE " + Student.TABLE_NAME + "("
                + Student.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Student.KEY_NAME + " TEXT,"
                + Student.KEY_PRIORITY + " TEXT" + ")";
        db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);
        onCreate(db);
    }

    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.KEY_NAME, student.getName());
        values.put(Student.KEY_PRIORITY, student.getPriority());

        long id = db.insert(Student.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public Student getStudent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.KEY_ID, Student.KEY_NAME, Student.KEY_PRIORITY},
                Student.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Student student = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                student = new Student();
                int idIndex = cursor.getColumnIndex(Student.KEY_ID);
                int nameIndex = cursor.getColumnIndex(Student.KEY_NAME);
                int priorityIndex = cursor.getColumnIndex(Student.KEY_PRIORITY);

                if (idIndex != -1) {
                    student.setId(cursor.getInt(idIndex));
                }
                if (nameIndex != -1) {
                    student.setName(cursor.getString(nameIndex));
                }
                if (priorityIndex != -1) {
                    student.setPriority(cursor.getString(priorityIndex));
                }
            }
            cursor.close();
        }
        return student;
    }


    public List<Student> getAllStudents() {
        List<Student> studentsList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Student.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Student student = new Student();
                    int idIndex = cursor.getColumnIndex(Student.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(Student.KEY_NAME);
                    int priorityIndex = cursor.getColumnIndex(Student.KEY_PRIORITY);

                    if (idIndex != -1) {
                        student.setId(cursor.getInt(idIndex));
                    }
                    if (nameIndex != -1) {
                        student.setName(cursor.getString(nameIndex));
                    }
                    if (priorityIndex != -1) {
                        student.setPriority(cursor.getString(priorityIndex));
                    }

                    studentsList.add(student);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return studentsList;
    }

    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.KEY_NAME, student.getName());
        values.put(Student.KEY_PRIORITY, student.getPriority());

        return db.update(Student.TABLE_NAME, values, Student.KEY_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
    }

    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, Student.KEY_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        db.close();
    }

    public static class Student {

        public static final String TABLE_NAME = "Students";

        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_PRIORITY = "priority";

        private int id;
        private String name;
        private String priority;

        public Student() {
            // Empty constructor
        }

        public Student(int id, String name, String priority) {
            this.id = id;
            this.name = name;
            this.priority = priority;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }
}
