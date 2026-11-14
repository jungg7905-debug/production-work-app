package com.example.productionwork;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class AlarmRingActivity extends Activity {
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  if(Build.VERSION.SDK_INT>=27)setShowWhenLocked(true);
  if(Build.VERSION.SDK_INT>=27)setTurnScreenOn(true);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  LinearLayout layout=new LinearLayout(this);
  layout.setOrientation(LinearLayout.VERTICAL);
  layout.setGravity(Gravity.CENTER);
  layout.setPadding(36,48,36,48);
  TextView title=new TextView(this);
  title.setText(getIntent().getStringExtra("title")==null?"작업 교대 알람":getIntent().getStringExtra("title"));
  title.setTextSize(28);title.setGravity(Gravity.CENTER);
  TextView body=new TextView(this);
  body.setText(getIntent().getStringExtra("body")==null?"알람 시간입니다":getIntent().getStringExtra("body"));
  body.setTextSize(20);body.setGravity(Gravity.CENTER);body.setPadding(0,30,0,50);
  Button stop=new Button(this);stop.setText("알람 끄기");stop.setTextSize(22);
  stop.setOnClickListener(v->dismiss());
  layout.addView(title);layout.addView(body);layout.addView(stop);
  setContentView(layout);
 }
 private void dismiss(){
  Intent i=new Intent(this,RingingAlarmService.class).setAction("STOP");
  startService(i);finish();
 }
 @Override public void onBackPressed(){dismiss();}
}