package com.example.newlab9.XOGame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.newlab9.R;
import com.example.newlab9.XnOActivity;


public class StartFragment extends Fragment {

    private Button btn_start_xo;
    private RadioButton xo_2_player, game_with_computer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_start_xo = view.findViewById(R.id.btn_start_xo);

        game_with_computer = view.findViewById(R.id.game_with_computer);
        game_with_computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XnOActivity.multiPlayer = false;
            }
        });

        xo_2_player = view.findViewById(R.id.xo_2_player);
        xo_2_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XnOActivity.multiPlayer = true;
            }
        });

        btn_start_xo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                XnOActivity.scoreX = 0;
                XnOActivity.scoreO = 0;
                transaction.addToBackStack(GameFragment.TAG);
                transaction.replace(R.id.main_xo_frame, new GameFragment());
                transaction.commit();
            }
        });
    }
}