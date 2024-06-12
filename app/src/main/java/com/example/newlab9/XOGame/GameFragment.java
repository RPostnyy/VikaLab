package com.example.newlab9.XOGame;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newlab9.Models.ChessboardAdapter;
import com.example.newlab9.R;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    private RecyclerView rv_chessboard;
    private ChessboardAdapter chessboardAdapter;
    public static boolean turnO = true;
    public static TextView tv_turn, tv_win_o, tv_win_x, tv_win;
    private Button xo_reset, btn_again, btn_xo_home;
    public static ImageView img_stroke, img_win;
    public static RelativeLayout rl_win;
    public static String TAG = GameFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_turn = view.findViewById(R.id.tv_turn);
        rv_chessboard = view.findViewById(R.id.rv_chessboard);

        xo_reset = view.findViewById(R.id.xo_reset);
        btn_xo_home = view.findViewById(R.id.btn_xo_home);
        btn_again = view.findViewById(R.id.btn_again);

        img_stroke = view.findViewById(R.id.img_stroke);

        rl_win = view.findViewById(R.id.rl_win);

        tv_win_o = view.findViewById(R.id.tv_win_o);
        tv_win_x = view.findViewById(R.id.tv_win_x);
        tv_win = view.findViewById(R.id.tv_win);
        img_win = view.findViewById(R.id.img_win);

        ArrayList<Bitmap> arrBms = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            arrBms.add(null);
        }
        chessboardAdapter = new ChessboardAdapter(getContext(), arrBms);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rv_chessboard.setLayoutManager(layoutManager);
        rv_chessboard.setAdapter(chessboardAdapter);

        xo_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        btn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_win.setVisibility(View.INVISIBLE);
                reset();
            }
        });

        btn_xo_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                getFragmentManager().popBackStack();
            }
        });

    }

    private void reset() {
        ArrayList<Bitmap> arrBms = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            arrBms.add(null);
        }
        chessboardAdapter.setArrBms(arrBms);
        chessboardAdapter.notifyDataSetChanged();
        turnO = true;
        tv_turn.setText("Ход O");
        img_stroke.setImageBitmap(null);
    }
}