package cs175.myapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joshua on 12/9/16.
 */

public class ScoreDatabase extends SQLiteOpenHelper {

    //set database values
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Scores";
    public static final String TABLE_SCORES = "scores";
    public static final String ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_SCORE = "points";
    private static final String CREATE_TABLE_SCORES = "create table " + TABLE_SCORES
            + " (" + ID + " integer primary key autoincrement, " + COL_NAME
            + " text not null, " + COL_SCORE + " integer not null);";
    private String me = "Joshua";

    private static final String DB_SCHEMA = CREATE_TABLE_SCORES;

    //constructor
    public ScoreDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_SCHEMA);
        sqLiteDatabase.execSQL("INSERT INTO scores(name, points) VALUES(" + me + ", 50);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(sqLiteDatabase);
    }
}
