package com.example.studyhotspot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SessionAddParticipants extends AppCompatActivity implements RecyclerViewAddParticipantAdapter.OnItemClickListener{

    private static final String TAG = "SESSIONADDParticipants";

    ArrayList<String> addedFriendList = new ArrayList<String>();
    ArrayList<String> friendNameList = new ArrayList<>();
    private ArrayList<String> participants;

    private UserDatabaseManager userDatabaseManager = new UserDatabaseManager(this);

    private String userID;
    private String userName;
    private String userEmail;

    private TextView participantCount;
    private FloatingActionButton addParticipants;

    private FloatingActionButton refresh;

    private RecyclerView recyclerView;
    private RecyclerViewAddParticipantAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_add_participants);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            participants = extras.getStringArrayList("Participants");
        }

        setUpFriends();
        setUpContent();
    }


    private void setUpFriends(){
        userDatabaseManager.getUserAddedFriends(friendNameList);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initRecyclerView();
            }
        }, 1500);
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerParticipants);
        adapter = new RecyclerViewAddParticipantAdapter(friendNameList,this);

        if (participants.size() > 0) {
            for (int i = 0; i < friendNameList.size(); i++) {
                if (participants.contains(friendNameList.get(i))) {
                    adapter.setPositives(i);
                }
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setUpContent(){
        participantCount = findViewById(R.id.participantCount);
        addParticipants = findViewById(R.id.addParticipants);

        participantCount.setText(""+participants.size());

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Participants",participants);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRecyclerView();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: "+position);
        if (participants.contains(friendNameList.get(position))){
            participants.remove(friendNameList.get(position));
        }
        else {
            participants.add(friendNameList.get(position));
        }
        System.out.println(participants);
        participantCount.setText(""+participants.size());
    }
}
