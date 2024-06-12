package com.example.newlab9;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.newlab9.XOGame.StartFragment;

public class XnOActivity extends AppCompatActivity {

    private FrameLayout main_xo_frame;

    public static boolean multiPlayer = true;
    public static int scoreX = 0, scoreO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_xogame);

        main_xo_frame = findViewById(R.id.main_xo_frame);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_xo_frame, new StartFragment());
        transaction.commit();
    }
}
