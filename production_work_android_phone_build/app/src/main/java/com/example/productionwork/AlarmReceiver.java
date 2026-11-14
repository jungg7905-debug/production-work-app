package com.example.productionwork;
import android.app.*;import android.content.*;import android.os.Build;
public class AlarmReceiver extends android.content.BroadcastReceiver{
 @Override public void onReceive(Context c,Intent i){
  String channel="work_alarm";NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(channel,"작업 교대 알람",NotificationManager.IMPORTANCE_HIGH));
  Intent open=new Intent(c,MainActivity.class);
  PendingIntent pi=PendingIntent.getActivity(c,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification n=new Notification.Builder(c,channel).setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
   .setContentTitle(i.getStringExtra("title")).setContentText(i.getStringExtra("body"))
   .setContentIntent(pi).setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).build();
  nm.notify(i.getIntExtra("id",0),n);
  AlarmScheduler.reschedule(c);
 }
}
