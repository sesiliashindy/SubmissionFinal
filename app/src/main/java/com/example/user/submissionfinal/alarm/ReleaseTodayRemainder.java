package com.example.user.submissionfinal.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.model.Movie;

import java.util.Calendar;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ReleaseTodayRemainder extends BroadcastReceiver {
    private static final int NOTIF_ID_REPEATING = 101;
    private static int notifId;

    public ReleaseTodayRemainder(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        notifId = intent.getIntExtra("id", 0);
        String title = intent.getStringExtra("movieTitle");

        showAlarmNotification(context, title, notifId);
    }

    private void showAlarmNotification(Context context, String title, int notifId) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "showAlarmNotification: " + notifId);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(title)
                .setContentText("Today " + title + " release")
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, builder.build());
        }
    }

    public void setRepeatingAlarm(Context context, List<Movie> movies){
        Log.d(TAG, "setRepeatingAlarm: " + movies.size());

        int notifDelay = 0;

        for (int i = 0; i < movies.size(); i++) {
            cancelAlarm(context);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, ReleaseTodayRemainder.class);
            intent.putExtra("movieTitle", movies.get(i).getName());
            intent.putExtra("id", notifId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 30);

            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + notifDelay, AlarmManager.INTERVAL_DAY, pendingIntent);
            }

            notifId ++;
            notifDelay += 1000;
        }

        Toast.makeText(context, "Release reminder set up", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getPendingIntent(context));
        }
    }

    private static PendingIntent getPendingIntent(Context context) {

        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    }
}
