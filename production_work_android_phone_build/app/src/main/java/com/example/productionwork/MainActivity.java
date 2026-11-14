package com.example.productionwork;
import android.app.*;import android.os.*;import android.content.*;import android.provider.Settings;import android.net.Uri;import android.webkit.*;import org.json.*;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b){super.onCreate(b);
  web=new WebView(this);setContentView(web);web.getSettings().setJavaScriptEnabled(true);
  web.getSettings().setDomStorageEnabled(true);web.addJavascriptInterface(new Bridge(),"AndroidAlarm");
  web.setWebViewClient(new WebViewClient());web.loadUrl("file:///android_asset/index.html");
  AlarmScheduler.reschedule(this);
 }
 public class Bridge{
  @JavascriptInterface public void sync(String json){getSharedPreferences("native",0).edit().putString("config",json).apply();AlarmScheduler.reschedule(MainActivity.this);}
  @JavascriptInterface public void requestPermissions(){runOnUiThread(()->{
   if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=getPackageManager().PERMISSION_GRANTED)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},10);
   if(Build.VERSION.SDK_INT>=31){AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(!am.canScheduleExactAlarms()){Intent i=new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName()));startActivity(i);}}
  });}
 }
 @Override protected void onResume(){super.onResume();AlarmScheduler.reschedule(this);}
}
