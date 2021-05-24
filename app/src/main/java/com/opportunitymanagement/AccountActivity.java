package com.opportunitymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity{

    Button buttonAddTrack;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    TextView textViewRating, textViewArtist;
    ListView listViewTracks;

    DatabaseReference databaseOpportunities;

    List<Opportunity> opportunities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();

        databaseOpportunities = FirebaseDatabase.getInstance().getReference("opportunities").child(intent.getStringExtra(MainActivity.ACCOUNT_ID));

        buttonAddTrack = (Button) findViewById(R.id.buttonAddTrack);
        editTextTrackName = (EditText) findViewById(R.id.editTextName);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);
        textViewRating = (TextView) findViewById(R.id.textViewRating);
        textViewArtist = (TextView) findViewById(R.id.textViewArtist);
        listViewTracks = (ListView) findViewById(R.id.listViewTracks);

        opportunities = new ArrayList<>();

        textViewArtist.setText(intent.getStringExtra(MainActivity.ACCOUNT_NAME));

        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewRating.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOpportunity();
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        databaseOpportunities.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                opportunities.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Opportunity opportunity = postSnapshot.getValue(Opportunity.class);
                    opportunities.add(opportunity);
                }
                OpportunityList opportunityListAdapter = new OpportunityList(AccountActivity.this, opportunities);
                listViewTracks.setAdapter(opportunityListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveOpportunity(){
        String opportunityName = editTextTrackName.getText().toString().trim();
        int status = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(opportunityName)){
            String id = databaseOpportunities.push().getKey();
            Opportunity opportunity = new Opportunity(id, opportunityName, status);
            databaseOpportunities.child(id).setValue(opportunity);

            Toast.makeText(this, "Opportunity saved", Toast.LENGTH_LONG).show();
            editTextTrackName.setText("");
        }else{
            Toast.makeText(this, "Please enter opportunity name", Toast.LENGTH_LONG).show();
        }
    }


}