package com.example.asus.project4th;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce;
    private Button btnSignin,btnSignup;
    private ViewFlipper imgFlipper;
    private TextView logotext;

    final private int sliders[]={
            R.drawable.background,
            R.drawable.background3,
            R.drawable.background5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Log.d("TextView",logotext.getText().toString());

        for(int slide: sliders){
            sliderFlipper(slide);
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(MainActivity.this,Signin1Activity.class);
                startActivity(signin);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup=new Intent(MainActivity.this,Signup1Activity.class);
                startActivity(signup);
            }
        });

    }
    public void init(){
        btnSignin=(Button)findViewById(R.id.btnSignin);
        btnSignup=(Button)findViewById(R.id.btnSignup);
        imgFlipper=findViewById(R.id.viewFlipper);
        logotext=findViewById(R.id.logotext);
    }

    public void sliderFlipper(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);
        Drawable bg = getDrawable(image);
        bg.setAlpha(200);
        imgFlipper.addView(imageView);
        imgFlipper.setFlipInterval(7000);
        imgFlipper.setAutoStart(true);
        imgFlipper.setInAnimation(this,android.R.anim.slide_in_left);
        imgFlipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(1);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
