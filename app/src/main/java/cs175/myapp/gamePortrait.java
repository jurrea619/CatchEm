package cs175.myapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/*
Portrait mode of game, where user's avatar is placed on left margin of screen. Objects will fly at user from the right side of screen.
User controls avatar through touching/holding screen and releasing
 */
public class gamePortrait extends AppCompatActivity {

    // labels and pickups
    private TextView scoreLabel;
    private TextView startLabel;
    private TextView portraitInstructions;
    private ImageView catcher;
    private ImageView orange;
    private ImageView bonus;
    private ImageView green;
    private ImageView spike1;
    private ImageView spike2;

    // Player/pick up position variables
    private int catcherY;
    private int orangeX;
    private int orangeY;
    private int greenX;
    private int greenY;
    private int bonusX;
    private int bonusY;
    private int spike1X;
    private int spike1Y;
    private int spike2X;
    private int spike2Y;
    private Player player;

    // initial score
    private int score = 0;

    // speed
    private int catcherSpeed;
    private int orangeSpeed;
    private int greenSpeed;
    private int bonusSpeed;
    private int spike1Speed;
    private int spike2Speed;

    // size
    private int frameHeight;
    private int catcherSize;
    private int screenWidth;
    private int screenHeight;

    // initialize classes to use
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status check
    private boolean touching = false;
    private boolean checkGameStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_portrait);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        portraitInstructions = (TextView) findViewById(R.id.portraitInstructions);
        catcher = (ImageView) findViewById(R.id.catcher);
        orange = (ImageView) findViewById(R.id.orange);
        green = (ImageView) findViewById(R.id.green);
        bonus = (ImageView) findViewById(R.id.bonus);
        spike1 = (ImageView) findViewById(R.id.spike1);
        spike2 = (ImageView) findViewById(R.id.spike2);

        sound = new SoundPlayer(this);

        player = new Player("Joshua");

        //get screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //use screen height and width to calc speed
        catcherSpeed = Math.round(screenHeight / 60f);
        orangeSpeed = Math.round(screenWidth / 70f);
        greenSpeed = Math.round(screenWidth / 60f);
        bonusSpeed = Math.round(screenWidth / 30f);
        spike1Speed = Math.round(screenWidth / 40f);
        spike2Speed = Math.round(screenWidth / 40f);

        //move outside screen
        orange.setX(-80.0f);
        orange.setY(-80.0f);
        green.setX(-80.0f);
        green.setY(-80.0f);
        bonus.setX(-80.0f);
        bonus.setY(-80.0f);
        spike1.setX(-80.0f);
        spike1.setY(-80.0f);
        spike2.setX(-80.0f);
        spike2.setY(-80.0f);


        scoreLabel.setText("Score : " + 0);

    }

    @Override
    protected void onPause(){
        super.onPause();
        /*
        timer.cancel();
        timer = null;
        checkGameStart = false;
        startLabel.setVisibility(TextView.VISIBLE);
        */
    }

    // constant update of game
    public void changePos(){

        //check for any hits
        hitCheck();

        //orange box vars
        orangeX -= orangeSpeed;
        if(orangeX < 0){
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //green box vars
        greenX -= greenSpeed;
        if(greenX < 0){
            greenX = screenWidth + 20;
            greenY = (int) Math.floor(Math.random() * (frameHeight - green.getHeight()));
        }
        green.setX(greenX);
        green.setY(greenY);

        // spike vars
        spike1X -= spike1Speed;
        if(spike1X < 0){
            spike1X = screenWidth + 10;
            spike1Y = (int) Math.floor(Math.random() * (frameHeight - spike1.getHeight()));
        }
        spike1.setX(spike1X);
        spike1.setY(spike1Y);

        spike2X -= spike2Speed;
        if(spike2X < 0){
            spike2X = screenWidth + 10;
            spike2Y = (int) Math.floor(Math.random() * (frameHeight - spike2.getHeight()));
        }
        spike2.setX(spike2X);
        spike2.setY(spike2Y);

        // bonus vars
        bonusX -= bonusSpeed;
        if(bonusX < 0){
            bonusX = screenWidth + 500;
            bonusY = (int) Math.floor(Math.random() * (frameHeight - bonus.getHeight()));
        }
        bonus.setX(bonusX);
        bonus.setY(bonusY);


        // if user is touching screen, move up
        if(touching == true){
            //touching
            catcherY -= catcherSpeed;
        }

        //user is not touching screen
        else{
            //releasing
            catcherY += catcherSpeed;
        }

        //set min and max for box to stay on screen
        if(catcherY < 0) catcherY = 0;
        if(catcherY > frameHeight - catcherSize) catcherY = frameHeight - catcherSize;

        catcher.setY(catcherY);

        //update score
        scoreLabel.setText("Score : " + player.getScore());
    }

    public void hitCheck(){
        //check is center of ball is in catcher
        //orange
        int orangeCenterX = orangeX + orange.getHeight() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        //0 <= orangeCenterX <= catcherWidth
        //catcherY <= orangeCenterY <= catcherY + catcherHeight

        if(0 <= orangeCenterX && orangeCenterX <= catcherSize &&
                catcherY <= orangeCenterY && orangeCenterY <= catcherY + catcherSize){
            player.incrementScore(10);
            orangeX = -10;
            sound.playHitSound();
        }

        //green
        int greenCenterX = greenX + green.getHeight() / 2;
        int greenCenterY = greenY + green.getHeight() / 2;


        if(0 <= greenCenterX && greenCenterX <= catcherSize &&
                catcherY <= greenCenterY && greenCenterY <= catcherY + catcherSize){
            player.incrementScore(25);
            greenX = -10;
            sound.playHitSound();
        }

        ///bonus
        int bonusCenterX = bonusX + bonus.getWidth() / 2;
        int bonusCenterY = bonusY + bonus.getHeight() / 2;

        if(0 <= bonusCenterX && bonusCenterX <= catcherSize &&
                catcherY <= bonusCenterY && bonusCenterY <= catcherY + catcherSize){
            player.incrementScore(30);
            bonusX = -10;
            sound.playHitSound();
        }

        //spike spikes
        int spike1CenterX = spike1X + spike1.getWidth() / 2;
        int spike1CenterY = spike1Y + spike1.getHeight() / 2;
        int spike2CenterX = spike2X + spike2.getWidth() / 2;
        int spike2CenterY = spike2Y + spike2.getHeight() / 2;

        if((0 <= spike1CenterX && spike1CenterX <= catcherSize &&
                catcherY <= spike1CenterY && spike1CenterY <= catcherY + catcherSize) ||
                (0 <= spike2CenterX && spike2CenterX <= catcherSize &&
                catcherY <= spike2CenterY && spike2CenterY <= catcherY + catcherSize)
                ){
            timer.cancel();
            timer = null;

            sound.playGameOverSound();

            //add score to database
            ContentValues contentValues = new ContentValues();

            contentValues.put(ScoresProvider.COL_NAME, "Joshua");
            contentValues.put(ScoresProvider.COL_SCORE, score);

            Uri _uri = getContentResolver().insert(ScoresProvider.URI, contentValues);

            Toast.makeText(getApplicationContext(), _uri.toString(), Toast.LENGTH_LONG).show();

            //show result
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("Score", player.getScore());
            startActivity(intent);
        }

    }

    public boolean onTouchEvent(MotionEvent me){
        //user hasn't started game
        if(!checkGameStart){
            //user has officially start
            checkGameStart = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            //frameWidth

            //place user into starting position
            catcherY = (int) catcher.getY();
            catcherSize = catcher.getHeight();

            //remove start label
            startLabel.setVisibility(View.GONE);
            portraitInstructions.setVisibility(View.GONE);

            //activate game timer
            //update position every 20 millis
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);
        }
        else {

            //monitor for user touch and set touching value accordingly
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                touching = true;
            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                touching = false;
            }
        }
        return true;
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
