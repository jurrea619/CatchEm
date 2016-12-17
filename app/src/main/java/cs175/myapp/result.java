package cs175.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*
Results screen that shows player's score from game that finished, along with all time high score
 */
public class result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScore);

        int score = getIntent().getIntExtra("Score", 0);
        scoreLabel.setText(score + "");
        //store in database

        SharedPreferences settings = getSharedPreferences("Game_Data", Context.MODE_PRIVATE);
        int highScore = settings.getInt("High_Score", 0);

        if(score > highScore){
            highScoreLabel.setText("High Score : " + score);

            //Save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("High_Score", score);
            editor.commit();
        }
        else{
            highScoreLabel.setText("High Score : " + highScore);
        }

        /*
        //retrieve top 5 records
        String URL = "content://cs175.myapp.ScoresProvider/scores";
        Uri scores  = Uri.parse(URL);
        Cursor c = managedQuery(scores, null, null, null, "points");

        if(c.moveToFirst()){
            do{
                Toast.makeText(result.this, c.getString(c.getColumnIndex(ScoresProvider.COL_NAME)) + ", "
                        + c.getString(c.getColumnIndex(ScoresProvider.COL_SCORE)), Toast.LENGTH_LONG).show();
            } while(c.moveToNext());
        }
        */
    }

    //restart landscape version
    public void tryAgainLand(View view){
        startActivity(new Intent(getApplicationContext(), gameLandscape.class));
    }

    // restart portrait version
    public void tryAgainPort(View view){
        startActivity(new Intent(getApplicationContext(), gamePortrait.class));
    }

    //return to main menu
    public void mainMenu(View view){
        startActivity(new Intent(getApplicationContext(), Start.class));
    }

    //disable back button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
