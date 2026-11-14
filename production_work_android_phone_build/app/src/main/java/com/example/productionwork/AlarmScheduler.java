package com.example.productionwork;
import android.app.*;import android.content.*;import android.os.Build;import org.json.*;import java.util.*;
public class AlarmScheduler{
 static int minutes(String s){String[] p=s.split(":");return Integer.parseInt(p[0])*60+Integer.parseInt(p[1]);}
 static String[][] blocks(String shift){return shift.equals("주간")?new String[][]{{"06:50","08:50"},{"09:00","11:00"},{"11:40","13:40"},{"13:50","15:30"}}:new String[][]{{"15:40","17:40"},{"17:50","19:50"},{"20:30","22:30"},{"22:40","00:20"}};}
 static ArrayList<Integer> times(String shift,String kind,int lead){
  ArrayList<Integer> out=new ArrayList<>();String[][] b=blocks(shift);
  int interval=kind.equals("half")?30:kind.equals("hour")||kind.equals("under")?60:40;
  if(kind.equals("under"))lead=10;if(kind.equals("fender"))lead=3;
  for(int idx=0;idx<4;idx++){
   int start=minutes(b[idx][0]),end=minutes(b[idx][1]);if(end<=start)end+=1440;
   if(idx==3&&kind.equals("under")&&shift.equals("주간")){out.add(minutes("14:30"));out.add(minutes("15:21"));continue;}
   int step=idx==3&&(kind.equals("half")||kind.equals("hour"))?45:interval;
   for(int target=start+step;target<=end;target+=step){int alarm=target-lead;if(alarm>=start&&alarm<end)out.add(alarm%1440);}
  }return out;
 }
 static void reschedule(Context c){
  String json=c.getSharedPreferences("native",0).getString("config","");if(json.isEmpty())return;
  try{
   JSONObject cfg=new JSONObject(json);String active=cfg.optString("shift","주간");JSONArray a=cfg.optJSONArray("alarms");if(a==null)return;
   AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
   for(int x=0;x<1000;x++){PendingIntent old=PendingIntent.getBroadcast(c,10000+x,new Intent(c,AlarmReceiver.class),PendingIntent.FLAG_NO_CREATE|PendingIntent.FLAG_IMMUTABLE);if(old!=null)am.cancel(old);}
   Calendar now=Calendar.getInstance();long nowMillis=System.currentTimeMillis();int slot=0;
   for(int j=0;j<a.length();j++){
    JSONObject item=a.getJSONObject(j);String shift=item.optString("shift"),kind=item.optString("kind");
    if(!shift.equals(active))continue;
    int lead=item.optInt("lead",5);
    String title=kind.equals("half")?"30분마다 교대":kind.equals("hour")?"1시간마다 교대":kind.equals("under")?"언더커버 작업":"휀다가니쉬 작업";
    for(int m:times(shift,kind,lead)){
     Calendar t=Calendar.getInstance();t.set(Calendar.HOUR_OF_DAY,m/60);t.set(Calendar.MINUTE,m%60);t.set(Calendar.SECOND,0);t.set(Calendar.MILLISECOND,0);
     if(t.getTimeInMillis()<=nowMillis)t.add(Calendar.DAY_OF_MONTH,1);
     Intent intent=new Intent(c,AlarmReceiver.class).putExtra("title",title).putExtra("body",shift+" · "+String.format(Locale.KOREA,"%02d:%02d",m/60,m%60)).putExtra("id",10000+slot);
     PendingIntent pi=PendingIntent.getBroadcast(c,10000+slot,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
     if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms())am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,t.getTimeInMillis(),pi);
     else am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,t.getTimeInMillis(),pi);
     slot++;
    }
   }
  }catch(Exception e){android.util.Log.e("WorkAlarm","Schedule error",e);}
 }
}
