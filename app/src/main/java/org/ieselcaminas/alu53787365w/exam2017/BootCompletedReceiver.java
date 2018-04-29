package org.ieselcaminas.alu53787365w.exam2017;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver {
        public BootCompletedReceiver() {
        }
        public static final int MY_ID_NOTIF = 1234;
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
                  //we double check here for only boot complete event

                  if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                             //here we start the service
                             Intent serviceIntent = new Intent(context, ServiceAfterBoot.class);
                             context.startService(serviceIntent);
                      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                      boolean displayLocation = pref.getBoolean("option1", false);
                      boolean sendNotification = pref.getBoolean("option2", false);
                      //Si esta habilitat el displayLocation s'iniciara l'activitat principal
                      if(displayLocation){
                          intent = new Intent(context, MainActivity.class);
                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          context.startActivity(intent);
                      }
                      //Si el sendNotification esta habilitat i el displayLocation esta deshabilitat
                      //s'enviara una notificacio per anar a les preferencies
                      if(sendNotification && !displayLocation){
                          //Notifications
                          NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                          Intent notIntent = new Intent(context, MyPreferencesActivity.class);
                          // We use a PendingIntent to create the notification
                          PendingIntent contIntent = PendingIntent.getActivity(context, 0, notIntent, 0);
                          int icon = android.R.drawable.stat_sys_warning;
                          CharSequence textState = "No display for postal code";
                          CharSequence textContent = "Setup postal code display";
                          long time = System.currentTimeMillis();
                          Notification
                                  notification = new Notification.Builder(context)
                                  .setSmallIcon(icon)
                                  .setContentTitle(textState)
                                  .setContentText(textContent)
                                  .setWhen(time)
                                  .setContentIntent(contIntent)
                                  .build();
                          notification.flags |= Notification.FLAG_AUTO_CANCEL;
                          notification.defaults |= Notification.DEFAULT_SOUND;
                          notification.defaults |= Notification.DEFAULT_VIBRATE;
                          notification.defaults |= Notification.DEFAULT_LIGHTS;

                          notManager.notify(MY_ID_NOTIF, notification);
                      }

                 }

       }


}