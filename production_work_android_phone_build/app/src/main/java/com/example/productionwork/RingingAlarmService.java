package com.example.productionwork;
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.*;

public class RingingAlarmService extends Service {
 private MediaPlayer player;
 private Vibrator vibrator;
 private static final String CHANNEL="work_ringing_alarm_v2";
 public static final int NOTIFICATION_ID=19001;
 @Override public IBinder onBind(Intent intent){return null;}
 @Override public int onStartCommand(Intent intent,int flags,int startId){
  if(intent!=null && "STOP".equals(intent.getAction())){stopSelf();return START_NOT_STICKY;}
  String title=intent==null?"작업 교대 알람":intent.getStringExtra("title");
  String body=intent==null?"알람 시간입니다":intent.getStringExtra("body");
  if(title==null)title="작업 교대 알람";
  if(body==null)body="알람 시간입니다";
  NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
  NotificationChannel channel=new NotificationChannel(CHANNEL,"작업 교대 벨소리",NotificationManager.IMPORTANCE_HIGH);
  channel.setSound(null,null);
  channel.enableVibration(false);
  nm.createNotificationChannel(channel);
  Intent screen=new Intent(this,AlarmRingActivity.class).putExtra("title",title).putExtra("body",body);
  screen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
  PendingIntent full=PendingIntent.getActivity(this,1,screen,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Intent stop=new Intent(this,RingingAlarmService.class).setAction("STOP");
  PendingIntent dismiss=PendingIntent.getService(this,2,stop,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification n=new Notification.Builder(this,CHANNEL)
   .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
   .setContentTitle(title).setContentText(body)
   .setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX)
   .setVisibility(Notification.VISIBILITY_PUBLIC)
   .setOngoing(true).setContentIntent(full)
   .setFullScreenIntent(full,true)
   .addAction(android.R.drawable.ic_menu_close_clear_cancel,"알람 끄기",dismiss).build();
  startForeground(NOTIFICATION_ID,n);
  if(player==null){
   try{
    Uri sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    if(sound==null)sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    player=new MediaPlayer();
    player.setDataSource(this,sound);
    player.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
    player.setLooping(true);
    player.prepare();
    player.start();
   }catch(Exception e){android.util.Log.e("WorkAlarm","Unable to play alarm",e);if(player!=null){player.release();player=null;}}
   vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
   if(vibrator!=null&&vibrator.hasVibrator())vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,600,400},0));
  }
  return START_NOT_STICKY;
 }
 @Override public void onDestroy(){
  if(player!=null){try{player.stop();}catch(Exception ignored){}player.release();player=null;}
  if(vibrator!=null)vibrator.cancel();
  super.onDestroy();
 }
}