package com.opportunitymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    public static final String ACCOUNT_NAME = "com.opportunitymanagement.accountname";
    public static final String ACCOUNT_ID = "com.opportunitymanagement.accountid";

    //view objects
    EditText editTextName;
    Spinner spinnerGenre;
    Button buttonAddArtist;
    ListView listViewArtists;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //a list to store all the artist from firebase database
    List<Account> accounts;

    //our database reference object
    DatabaseReference databaseAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //getting the reference of accounts node
        databaseAccounts = FirebaseDatabase.getInstance().getReference("accounts");

        //getting views
        editTextName = findViewById(R.id.editTextName);
        spinnerGenre = findViewById(R.id.spinnerGenres);
        listViewArtists = findViewById(R.id.listViewArtists);
        buttonAddArtist = findViewById(R.id.buttonAddArtist);

        //list to store accounts
        accounts = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            //display welcome message
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome, " + user.getDisplayName() + "!");
            }
        };

        //adding an onclicklistener to button
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addAccount()
                //the method is defined below
                //this method is actually performing the write operation
                addAccount();
            }
        });

        //attaching listener to listview
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected account
                Account account = accounts.get(i);

                //creating an intent
                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);

                //putting account name and id to intent
                intent.putExtra(ACCOUNT_ID, account.getAccountId());
                intent.putExtra(ACCOUNT_NAME, account.getAccountName());

                //starting the activity with intent
                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
                Account account = accounts.get(i);
                showUpdateDeleteDialog(account.getAccountId(), account.getAccountName());
                return true;
            }
        });
    }

    public void addAccount(){
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String address = spinnerGenre.getSelectedItem().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)){
            String id = databaseAccounts.push().getKey();
            //creating an Account Object
            Account account = new Account(id, name, address);

            //Saving the account
            databaseAccounts.child(id).setValue(account);

            //setting editText to blank again
            editTextName.setText("");

            //Displaying a success Toast
            Toast.makeText(this, "Account added", Toast.LENGTH_LONG).show();
        }
        else {
            //if value is not given show toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private boolean updateAccount(String id, String name, String address){
        //getting specified account reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("accounts").child(id);

        //updating account
        Account account = new Account(id, name, address);
        dR.setValue(account);

        Toast.makeText(getApplicationContext(), "Account updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteAccount(String id){
        //getting the specified account reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("accounts").child(id);

        //removing accounts
        dR.removeValue();

        //getting the opportunities references for a specific account
        DatabaseReference drOpportunities = FirebaseDatabase.getInstance().getReference("opportunities").child(id);

        //removing all opportunities
        drOpportunities.removeValue();
        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    private void showUpdateDeleteDialog(final String accountId, String accountName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle(accountName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String address = spinnerGenre.getSelectedItem().toString();

                if (!TextUtils.isEmpty(name)){
                    updateAccount(accountId, name, address);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(accountId);
                b.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //attaching value Event Listener
        databaseAccounts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing previous account list
                accounts.clear();
                //iterating through all nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    //getting accounts
                    Account account = postSnapshot.getValue(Account.class);
                    //adding to list
                    accounts.add(account);
                }
                //creating adapter
                AccountList accountAdapter = new AccountList(MainActivity.this, accounts);
                //attaching adapter to the list view
                listViewArtists.setAdapter(accountAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}