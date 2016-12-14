package cs175.myapp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 * Created by joshua on 12/9/16.
 */

public class ScoresProvider extends ContentProvider {

    private SQLiteDatabase db;
    static final String PROVIDER = "cs175.myapp";
    static final String PATH_SCORES = "scores";
    static final String URL = "content://" + PROVIDER + "/" + PATH_SCORES;
    static final Uri URI = Uri.parse(URL);

    static final String ID = "_id";
    static final String SCORE = "score";

    Context mContext;

    static final int SCORES = 1;
    static final int SCORE_ID = 2;
    private static HashMap<String, String> SCORES_PROJECTION_MAP;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "scores", SCORES );
        uriMatcher.addURI(PROVIDER, "scores/#", SCORE_ID);
    }

    static final int DB_VERSION = 1;
    static final String DB_NAME = "Scores";
    static final String TABLE_SCORES = "scores";
    static final String COL_ID = "_id";
    static final String COL_NAME = "name";
    static final String COL_SCORE = "points";
    private static final String CREATE_TABLE_SCORES = "create table " + TABLE_SCORES
            + " (" + COL_ID + " integer primary key autoincrement, " + COL_NAME
            + " text, " + COL_SCORE + " integer not null);";

    private static final String DB_SCHEMA = CREATE_TABLE_SCORES;

    private static class ScoreDatabase extends SQLiteOpenHelper {


        public ScoreDatabase(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        private String me = "Joshua";

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DB_SCHEMA);
            db.execSQL("INSERT INTO scores(name, points) VALUES('Joshua',50");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
            onCreate(db);
        }
    }

    private void notifyChange(Uri uri){
        ContentResolver resolver = mContext.getContentResolver();
        if(resolver != null) resolver.notifyChange(uri, null);
    }

    private int getMatchedID(Uri uri){
        int matchedID = uriMatcher.match(uri);
        if(!(matchedID == SCORES || matchedID == SCORE_ID)){
            throw new IllegalArgumentException("Unsupported URI");
        }
        return matchedID;
    }

    private String getIdString(Uri uri){
        return (ID + " = " + uri.getPathSegments().get(1));
    }

    private String getSelectionWithID(Uri uri, String selection){
        String sel_str = getIdString(uri);
        if(!TextUtils.isEmpty(selection)){
            sel_str += " AND (" + selection + ")";
        }
        return sel_str;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
       if(mContext == null){
           return false;
       }

        ScoreDatabase sDB = new ScoreDatabase(mContext);
        db = sDB.getWritableDatabase();
        if(db == null){
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(TABLE_SCORES);

        if(getMatchedID(uri) == SCORES){
            sqLiteQueryBuilder.setProjectionMap(SCORES_PROJECTION_MAP);
        }
        else{
            sqLiteQueryBuilder.appendWhere(getIdString(uri));
        }

        if(sortOrder == null || sortOrder == ""){
            sortOrder = COL_SCORE;
        }

        Cursor c = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(mContext.getContentResolver(), uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        if(getMatchedID(uri) == SCORES){
            return "vnd.android.cursor.dir/vnd.cs175.myapp.scores";
        }
        else{
            return "vnd.android.cursor.item/vnd.cs175.myapp.scores";
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        long row = db.insert(TABLE_SCORES, "", contentValues);

        if(row > 0){
            Uri _uri = ContentUris.withAppendedId(URI, row);
            notifyChange(_uri);
            return _uri;
        }
        return null;
    }

    /*
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
    long row = database.insert("scores", "", contentValues);
        if(row > 0){
            Uri _uri = ContentUris.withAppendedId(URI, row);
            //notifyChange(_uri);
            return _uri;
        }
    }
    */

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = 0;
        String sel_str = (getMatchedID(uri) == SCORE_ID) ?
                getSelectionWithID(uri, s) : s;

        count = db.delete(TABLE_SCORES,s, strings);

        notifyChange(uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {


        return 0;
    }
}
