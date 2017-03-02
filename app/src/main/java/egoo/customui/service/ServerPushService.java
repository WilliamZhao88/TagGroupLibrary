package egoo.customui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import egoo.customui.MainActivity;
import egoo.customui.R;

public class ServerPushService extends Service{
    //获取消息线程
    private MessageThread messageThread = null;
    //点击查看
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;
    //通知栏消息
    private int messageNotificationID = 1000;
    private Notification messageNotification = null;
    private NotificationManager messageNotificationManager = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化
        messageNotification = new Notification.Builder(this)
                .setContentTitle("新消息")
                .setContentText("这是一条新的测试消息")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
//        messageNotification.icon = R.mipmap.ic_launcher;  //通知图片
//        messageNotification.tickerText = "新消息";         //通知标题
//        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        //点击查看
        messageIntent = new Intent(this,MainActivity.class);
        messagePendingIntent = PendingIntent.getActivity(this, 0, messageIntent, 0);
        //开启线程
        MessageThread thread = new MessageThread();
        thread.isRunning = true;
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * 从服务端获取消息
     * @author zhanglei
     *
     */
    class MessageThread extends Thread{
        //运行状态
        public boolean isRunning = true;
        @Override
        public void run() {
            while(isRunning){
                try {
                    //休息10秒
                    Thread.sleep(10000);
                    if(getServerMessage().equals("yes")){
                        //发布消息
                        messageNotificationManager.notify(messageNotificationID, messageNotification);
                        //避免覆盖消息，采取ID自增
                        messageNotificationID++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /***
     * 模拟了服务端的消息。实际应用中应该去服务器拿到message
     * @return
     */
    public String getServerMessage(){
        return "yes";
    }
}
