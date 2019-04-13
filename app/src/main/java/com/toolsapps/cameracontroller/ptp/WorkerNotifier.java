package com.toolsapps.cameracontroller.ptp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.toolsapps.cameracontroller.MainActivity;
import com.toolsapps.cameracontroller.R;
import com.toolsapps.cameracontroller.util.NotificationIds;

public class WorkerNotifier implements Camera.WorkerListener {

        private final NotificationManager notificationManager;
        private final NotificationCompat.Builder notification;
        private final int uniqueId = NotificationIds.getInstance().getUniqueIdentifier(WorkerNotifier.class.getName() + ":running");;

        public WorkerNotifier(Context context) {
            notification =
                    new NotificationCompat.Builder(context, "0")
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle(context.getString(R.string.worker_content_title))
                            .setContentText(context.getString(R.string.worker_content_text));
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            notification.setContentIntent(resultPendingIntent);
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(uniqueId, notification.build());

        }

        @Override
        public void onWorkerStarted() {
            notificationManager.notify(uniqueId, notification.build());
        }

        @Override
        public void onWorkerEnded() {
            notificationManager.cancel(uniqueId);
        }

}
