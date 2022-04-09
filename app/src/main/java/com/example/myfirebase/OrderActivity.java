package com.example.myfirebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference mPostReference;

    Button btn_Insert;
    EditText edit_menu1;
    EditText edit_menu2;
    EditText edit_menu3;
    EditText edit_menu4;
    EditText edit_msg;
    TextView text_menu1;
    TextView text_menu2;
    TextView text_menu3;
    TextView text_menu4;
    TextView text_temp;
    TextView text_type;
    CheckBox check_hall;
    CheckBox check_deliver;

    long temp_id;
    int menu1;
    int menu2;
    int menu3;
    int menu4;
    String msg;
    String type = "";
    String strTime = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        strTime = sdf.format(cal.getTime());

        text_type= (TextView) findViewById(R.id.text_gender);
        check_hall = (CheckBox) findViewById(R.id.check_hall);
        check_hall.setOnClickListener(this);
        check_deliver = (CheckBox) findViewById(R.id.check_deliver);
        check_deliver.setOnClickListener(this);
        btn_Insert = (Button) findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);
        edit_menu1 = (EditText) findViewById(R.id.edit_menu1);
        edit_menu2 = (EditText) findViewById(R.id.edit_menu2);
        edit_menu3 = (EditText) findViewById(R.id.edit_menu3);
        edit_menu4 = (EditText) findViewById(R.id.edit_menu4);
        edit_msg = (EditText) findViewById(R.id.edit_require);
        text_menu1 = (TextView) findViewById(R.id.text_menu1);
        text_menu2 = (TextView) findViewById(R.id.text_menu2);
        text_menu3 = (TextView) findViewById(R.id.text_menu3);
        text_menu4 = (TextView) findViewById(R.id.text_menu3);

        getFirebaseDatabase();

        btn_Insert.setEnabled(true);
    }

    public void setInsertMode(){
        edit_menu1.setText("");
        edit_menu2.setText("");
        edit_menu3.setText("");
        edit_menu4.setText("");
        edit_msg.setText("");
        btn_Insert.setEnabled(true);
    }

    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(menu1, menu2, menu3, menu4, msg, type);
            postValues = post.toMap();
        }
        childUpdates.put("/"+strTime+"/" + temp_id, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    String[] info = {String.valueOf(get.menu1), String.valueOf(get.menu2), String.valueOf(get.menu3), String.valueOf(get.menu4), get.msg, get.type};
                    String Result = info[0]+info[1]+info[2]+info[3]+info[4]+info[5];
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0]+info[1]+info[2]+info[3]+info[4]+info[5]);
                }
                temp_id = dataSnapshot.getChildrenCount()+1;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child(strTime);

        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:
                Toast.makeText(getApplicationContext(), "주문이 완료되었습니다.", Toast.LENGTH_LONG).show();
                menu1 = Integer.parseInt(edit_menu1.getText().toString());
                menu2 = Integer.parseInt(edit_menu2.getText().toString());
                menu3 = Integer.parseInt(edit_menu3.getText().toString());
                menu4 = Integer.parseInt(edit_menu4.getText().toString());
                msg = edit_msg.getText().toString();

                postFirebaseDatabase(true);
                getFirebaseDatabase();
                setInsertMode();

                check_deliver.setChecked(false);
                check_hall.setChecked(false);
                break;

            case R.id.check_hall:
                check_deliver.setChecked(false);
                type = "홀";
                break;

            case R.id.check_deliver:
                check_hall.setChecked(false);
                type = "배달";
                break;
        }
    }
}