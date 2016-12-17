package cs175.myapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import static cs175.myapp.R.id.catcher;
import static cs175.myapp.R.id.catcherUp;
import static cs175.myapp.R.id.frame;

/*
Landscape mode requires the user to tilt the screen
for player movement

Using basic game format from portrait mode, I created the landscape version with similar features but
added functionality from the accelerometer as well as other features
 */
public class gameLandscape extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = gameLandscape.class.getSimpleName();

    // labels and pickups
    private TextView scoreLabel;
    private TextView startLabel;
    private TextView landscapeInstructions;
    private ImageView catcherUp;
    private ImageView orange;
    private ImageView green;
    private ImageView bonus;
    private ImageView spike1;
    private ImageView spike2;

    // Player/pick up position variables
    private int catcherY;
    private int catcherX;
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


    // initial score
    private int score = 0;
    private Player player;
    private float y;

    // speed
    private int catcherSpeed;
    private int orangeSpeed;
    private int greenSpeed;
    private int bonusSpeed;
    private int spikeSpeed;

    // size
    private int frameHeight;
    private int frameWidth;
    private int catcherSize;
    private int catcherWidth;
    private int screenWidth;
    private int screenHeight;

    // initialize classes to use
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status check
    private boolean action_flg = false;
    private boolean checkGameStart = false;

    //Sensor intialize
    SensorManager mySensors;
    Sensor myAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_landscape);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        landscapeInstructions = (TextView) findViewById(R.id.landscapeInstructions);
        catcherUp = (ImageView) findViewById(R.id.catcherUp);
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
        catcherSpeed = Math.round(screenWidth / 60f);
        orangeSpeed = Math.round(screenHeight / 60f);
        greenSpeed = Math.round(screenHeight / 60f);
        bonusSpeed = Math.round(screenHeight / 30f);
        spikeSpeed = Math.round(screenHeight / 40f);

        //move outside screen
        orange.setX(screenHeight + 30);
        orange.setY(screenHeight + 30);
        green.setX(screenHeight + 30);
        green.setY(screenHeight + 30);
        bonus.setX(screenHeight + 30);
        bonus.setY(screenHeight + 30);
        spike1.setX(screenHeight + 30);
        spike1.setY(screenHeight + 30);

        spike2.setX(screenHeight + 30);
        spike2.setY(screenHeight + 30);

        scoreLabel.setText("Score : " + 0);

        mySensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    //attach listener when user returns
    @Override
    protected void onResume(){
        super.onPause();
        mySensors.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //detach listener when screen is not visible to user
    @Override
    protected void onPause(){
        super.onPause();
        mySensors.unregisterListener(this);
    }


    // constant update of game
    public void changePos(){

        //check for any hits
        hitCheck();

        //orange box vars
        orangeY += orangeSpeed;
        if(orangeY > frameHeight){
            orangeY = (int) -100f;
            orangeX = (int) Math.floor(Math.random() * (screenWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        //green
        greenY += greenSpeed;
        if(greenY > frameHeight){
            greenY = (int) -100f;
            greenX = (int) Math.floor(Math.random() * (screenWidth - green.getWidth()));
        }
        green.setX(greenX);
        green.setY(greenY);

        // spike vars
        spike1Y += spikeSpeed;
        if(spike1Y > frameHeight){
            spike1Y = (int) -80f;
            spike1X = (int) Math.floor(Math.random() * (screenWidth - spike1.getWidth()));
        }
        spike1.setX(spike1X);
        spike1.setY(spike1Y);

        spike2Y += spikeSpeed;
        if(spike2Y > frameHeight){
            spike2Y = (int) -80f;
            spike2X = (int) Math.floor(Math.random() * (screenWidth - spike2.getWidth()));
        }
        spike2.setX(spike2X);
        spike2.setY(spike2Y);

        // bonus vars
        bonusY += bonusSpeed;
        if(bonusY > frameHeight){
            bonusY = (int) -1500f;
            bonusX = (int) Math.floor(Math.random() * (screenWidth - bonus.getWidth()));
        }
        bonus.setX(bonusX);
        bonus.setY(bonusY);

        //update score
        scoreLabel.setText("Score : " + player.getScore());
    }

    public void hitCheck(){
        //check is center of ball is in catcher

        //orange
        int orangeCenterX = orangeX + orange.getWidth() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        if(orangeCenterY < frameHeight && orangeCenterY > frameHeight - catcherSize &&
                catcherX <= orangeCenterX && orangeCenterX <= catcherX + catcherWidth){
            player.incrementScore(10);
            orangeY = frameHeight + 40;
            sound.playHitSound();
        }

        //green
        int greenCenterX = greenX + green.getWidth() / 2;
        int greenCenterY = greenY + green.getHeight() / 2;

        if(greenCenterY < frameHeight && greenCenterY > frameHeight - catcherSize &&
                catcherX <= greenCenterX && greenCenterX <= catcherX + catcherWidth){
            player.incrementScore(25);
            greenY = frameHeight + 40;
            sound.playHitSound();
        }

        ///bonus
        int bonusCenterX = bonusX + bonus.getWidth() / 2;
        int bonusCenterY = bonusY + bonus.getHeight() / 2;

        if(bonusCenterY < frameHeight && bonusCenterY > frameHeight - catcherSize &&
                catcherX <= bonusCenterX && bonusCenterX <= catcherX + catcherWidth){
            player.incrementScore(30);
            bonusY = frameHeight + 40;
            sound.playHitSound();
        }

        //spikes
        int spikeCenterX = spike1X + spike1.getWidth() / 2;
        int spikeCenterY = spike1Y + spike1.getHeight() / 2;

        int spike2CenterX = spike2X + spike2.getWidth() / 2;
        int spike2CenterY = spike2Y + spike2.getHeight() / 2;

        if((spikeCenterY < frameHeight && spikeCenterY > frameHeight - catcherSize &&
                catcherX <= spikeCenterX && spikeCenterX <= catcherX + catcherWidth) ||
                (spike2CenterY < frameHeight && spike2CenterY > frameHeight - catcherSize &&
                        catcherX <= spike2CenterX && spike2CenterX <= catcherX + catcherWidth)){
            timer.cancel();
            timer = null;

            sound.playGameOverSound();

            //show result
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("Score", player.getScore());
            startActivity(intent);
        }
    }




    // check user touch on screen
    public boolean onTouchEvent(MotionEvent me){
        //user hasn't started game
        if(!checkGameStart){
            //user has officially start
            checkGameStart = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameWidth = frame.getWidth();
            frameHeight = frame.getHeight();
            //frameWidth

            catcherX = (int) catcherUp.getX();
            catcherY = (int) catcherUp.getY();
            catcherSize = catcherUp.getHeight();
            catcherWidth = catcherUp.getWidth();
            catcherUp.setX(885);
            catcherUp.setY(frameHeight - catcherSize);
            catcherX = (int) catcherUp.getX();
            catcherY = (int) catcherUp.getY();

            //remove start label
            startLabel.setVisibility(View.GONE);
            landscapeInstructions.setVisibility(View.GONE);

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


    // grabs value from accelerometer to change player position
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(checkGameStart) {
            //pull y values for tilt along y axis
            y = sensorEvent.values[1];

            catcherX = (int) catcherUp.getX();
            catcherX += (y * 15 );
            catcherY = frameHeight - catcherSize;

            //set min and max for box to stay on screen
            if(catcherX < 0) catcherX = 0;
            if(catcherX > screenWidth - catcherWidth) catcherX = screenWidth - catcherSize;
            catcherUp.setX(catcherX);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }
}
