package com.example.productionwork;
import android.content.*;
import android.os.Build;
public class AlarmReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
  Intent ring=new Intent(c,RingingAlarmService.class);
  ring.putExtra("title",i.getStringExtra("title"));
  ring.putExtra("body",i.getStringExtra("body"));
  try{
   if(Build.VERSION.SDK_INT>=26)c.startForegroundService(ring);
   else c.startService(ring);
  }catch(Exception e){android.util.Log.e("WorkAlarm","Unable to start alarm",e);}
  AlarmScheduler.reschedule(c);
 }
}
