package com.example.vaccinestatuscheck;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    TextView addButton;
    Button fixButton;
    Button loginButton;
    String shared = "file";
    Switch aSwitch;
    private ArrayList<alarmView> arrayList;
    private alarmViewAdaptor mainAdapter;
    public static Context context;
    String strUserPicture, usename;
    Boolean start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        usename = intent.getStringExtra("name");
        strUserPicture = intent.getStringExtra("profileImg");
        Log.d("msg", "service start");

        TextView point = findViewById(R.id.credit);
        if (point.getText() != "0") {
            int updateCredit = preConfig.readCreditPref(this);
            point.setText(String.valueOf(updateCredit));
        }
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100);

        Animation anim2 = new AlphaAnimation(1.0f, 0.0f);
        anim2.setDuration(100);

        int credit = intent.getIntExtra("credit", 0);
        Log.d("msg", String.valueOf(credit));
        if (credit == 1) {
            Log.d("msg2", String.valueOf(credit));
            int newCredit = Integer.parseInt((String) point.getText()) + credit;
            Log.d("newcredit", String.valueOf(newCredit));
            preConfig.writeCreditPref(this, newCredit);
            point.startAnimation(anim);
            point.setText(String.valueOf(newCredit));
            point.setAnimation(anim2);
        }

        if (usename == null | strUserPicture == null) {
            usename = preConfig.readNamePref(this);
            strUserPicture = preConfig.readPicturePref(this);
        }

        TextView username = findViewById(R.id.username);
        username.setText(usename);
        ImageView userpicture = findViewById(R.id.userPicture);
        Glide.with(this).load(strUserPicture).into(userpicture);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        arrayList = preConfig.readListFromPref(this);
//        for(int i=0; i<arrayList.size(); i++){
//            Log.d("list", String.valueOf(arrayList.get(i).alarmTime));
//        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        TextView intro = findViewById(R.id.intro);
        TextView intro2 = findViewById(R.id.intro2);
        //??? ?????? ??????
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            Log.d("list0", String.valueOf(arrayList.size()));
            intro.setVisibility(View.VISIBLE);
            intro2.setVisibility(View.VISIBLE);
        } else if(arrayList.size() == 0){
            intro.setVisibility(View.VISIBLE);
            intro2.setVisibility(View.VISIBLE);
        }else {
            intro.setVisibility(View.INVISIBLE);
            intro2.setVisibility(View.INVISIBLE);
            Log.d("listx", String.valueOf(arrayList.size()));
            for (int i = 0; i < arrayList.size(); i++) {
                Log.d("list", String.valueOf(arrayList.get(i).alarmTime));
            }
        }
        mainAdapter = new alarmViewAdaptor(arrayList);
        recyclerView.setAdapter(mainAdapter);
        addButton = findViewById(R.id.fixButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeAlarmActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        //?????? ??????
        mainAdapter.setOnItemClickListener(new alarmViewAdaptor.OnItemClickListenr() {
            @Override
            public void onItemClick(View v, int position) {
                int id = mainAdapter.items.get(position).alarmId;
                Intent intent1 = new Intent(getApplicationContext(), FixAlarmAcitivity.class);
                String friendName = mainAdapter.items.get(position).helperName;
                String friendImage = mainAdapter.items.get(position).helperImageId;
                String message = mainAdapter.items.get(position).message;
                String friendId = mainAdapter.items.get(position).friendId;
                Log.d("??????", friendName);
                Log.d("?????? ??????id", friendId);
                intent1.putExtra("??????", friendName);
                intent1.putExtra("??????", friendImage);
                intent1.putExtra("??????", id);
                intent1.putExtra("??????", position);
                intent1.putExtra("?????????", message);
                intent1.putExtra("??????id", friendId);
                startActivityForResult(intent1, 105);
            }
        });

        mainAdapter.setOnItemClickListener(new alarmViewAdaptor.OnItemClickListenr2() {
            @Override
            public void onItemClick(View v, int position) {
                intro.setVisibility(View.VISIBLE);
                intro2.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            TextView intro = findViewById(R.id.intro);
            TextView intro2 = findViewById(R.id.intro2);
            if (data != null) {
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }

                if (arrayList.size() == 0) {
                    intro.setVisibility(View.INVISIBLE);
                    intro2.setVisibility(View.INVISIBLE);
                }

                Log.d("101 check", "right");
                int hour = data.getIntExtra("??????", 0);
                int minute = data.getIntExtra("???", 0);
                String helperName = data.getStringExtra("????????????");
                String helperImageId = data.getStringExtra("????????????");
                String message = data.getStringExtra("?????????");
                String friendId = data.getStringExtra("??????id");
                Log.d("??????id", friendId);
                String dayNight = null;
                int alarmId = data.getIntExtra("??????", 0);
                if (hour > 12) {
                    dayNight = "PM";
                    hour = hour - 12;
                } else {
                    dayNight = "AM";
                }
                if (minute < 10) {
                    alarmView alarmView = new alarmView(dayNight+" ",hour + ":" + "0" + minute, helperName, alarmId, helperImageId, message, friendId);
                    arrayList.add(alarmView);
                } else {
                    alarmView alarmView = new alarmView(dayNight+" ",hour + ":" + minute, helperName, alarmId, helperImageId, message, friendId);
                    arrayList.add(alarmView);
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    Log.d("list", String.valueOf(arrayList.get(i).alarmTime));
                }
                //??????
                Collections.sort(arrayList, new Comparator<alarmView>() {
                    @Override
                    public int compare(alarmView o1, alarmView o2) {
                        if (o1.alarmId > o2.alarmId) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                preConfig.writeListPref(this, arrayList);
                mainAdapter.notifyDataSetChanged();
            }

        } else if (requestCode == 102) {
            int credit = data.getIntExtra("credit", 0);
            Log.d("msg2", String.valueOf(credit));
            if (credit == 1) {
                TextView point = findViewById(R.id.credit);
                Log.d("point", String.valueOf(point));
                int newCredit = Integer.parseInt((String) point.getText()) + credit;
                Log.d("newcredit", String.valueOf(newCredit));
                point.setText(String.valueOf(newCredit));
            }
        }
        else if (requestCode == 105) {
            Log.d("msg", "105");
            if (data != null) {
                int hour = data.getIntExtra("??????", 0);
                int minute = data.getIntExtra("???", 0);
                int position = data.getIntExtra("??????", 0);
                Log.d("position", String.valueOf(position));
                String helperName = data.getStringExtra("????????????");
                String helperImageId = data.getStringExtra("????????????");
                String message = data.getStringExtra("?????????");
                String friendId = data.getStringExtra("??????id");
                int newAlarmId = data.getIntExtra("??????", 0);
                String dayNight = null;

                if (hour > 12) {
                    dayNight = "PM";
                    hour = hour - 12;
                } else {
                    dayNight = "AM";
                }

                if (minute < 10) {
                    alarmView alarmView = new alarmView(dayNight+" ",hour + ":" + "0" + minute, helperName, newAlarmId, helperImageId, message, friendId);
                    arrayList.set(position, alarmView);
                    Collections.sort(arrayList, new Comparator<alarmView>() {
                        @Override
                        public int compare(alarmView o1, alarmView o2) {
                            if (o1.alarmId > o2.alarmId) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    preConfig.writeListPref(this, arrayList);
                    mainAdapter.notifyDataSetChanged();

                } else {
                    alarmView alarmView = new alarmView(dayNight+" ",hour + ":" + minute, helperName, newAlarmId, helperImageId, message, friendId);
                    arrayList.set(position, alarmView);
                    Collections.sort(arrayList, new Comparator<alarmView>() {
                        @Override
                        public int compare(alarmView o1, alarmView o2) {
                            if (o1.alarmId > o2.alarmId) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    preConfig.writeListPref(this, arrayList);
                    mainAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("msg", "service stop");
        preConfig.writeListPref(this, arrayList);
        preConfig.writeNamePref(this, usename);
        preConfig.writePicturePref(this, strUserPicture);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}