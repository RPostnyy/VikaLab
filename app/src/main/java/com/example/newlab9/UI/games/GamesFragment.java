package com.example.newlab9.UI.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.newlab9.R;
import com.example.newlab9.XnOActivity;

public class GamesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        // Find card views by their IDs and set click listeners
        CardView cardGame1 = view.findViewById(R.id.cardGame1);
        CardView cardGame2 = view.findViewById(R.id.cardGame2);
        CardView cardGame3 = view.findViewById(R.id.cardGame3);
        CardView cardGame4 = view.findViewById(R.id.cardGame4);

        cardGame1.setOnClickListener(v -> openGame1Activity());
        cardGame2.setOnClickListener(v -> openGame2Activity());
        cardGame3.setOnClickListener(v -> openGame3Activity());
        cardGame4.setOnClickListener(v -> openGame4Activity());

        return view;
    }

    private void openGame1Activity() {
        // Intent logic for opening Game 1 Activity
        Intent intent = new Intent(getActivity(), XnOActivity.class);
        startActivity(intent);
    }

    private void openGame2Activity() {
        // Intent logic for opening Game 2 Activity
        Toast.makeText(getContext(), "Game 2 Activity", Toast.LENGTH_SHORT).show();
    }

    private void openGame3Activity() {
        // Intent logic for opening Game 3 Activity
        Toast.makeText(getContext(), "Game 3 Activity", Toast.LENGTH_SHORT).show();
    }

    private void openGame4Activity() {
        // Intent logic for opening Game 4 Activity
        Toast.makeText(getContext(), "Game 4 Activity", Toast.LENGTH_SHORT).show();
    }
}
