package com.example.j14014.kadai2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by MacbookPro on 2016/02/01.
 */
public class StartView extends Activity implements View.OnClickListener{

    // ゲームスタートボタン
    private Button StartButton;

    // スコア表示ボタン
    private Button ScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);

        // GAME START Button
        StartButton = (Button) findViewById(R.id.StartButton);
        StartButton.setOnClickListener(this);

        // SCORE Button
        ScoreButton = (Button) findViewById(R.id.ScoreButton);
        ScoreButton.setOnClickListener(this);

        /*
        //フルスクリーン
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        gameView = new GameView(this);
        setContentView(gameView);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        // Game View 起動
        if (view.equals(StartButton)) {
            Intent start = new Intent(StartView.this, PairingView.class);
            startActivity(start);
        }
        // Scoreを起動
        else if (view.equals(ScoreButton)) {
            Intent score = new Intent(StartView.this, ScoreView.class);
            startActivity(score);
        }
    }
}
