package com.hskj.meettingsys.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.hskj.meettingsys.R;
import com.hskj.meettingsys.listener.CallBack;
import com.hskj.meettingsys.ui.MainActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MqttService extends Service {

    public static String clientId = "litf";
    //    public static final String BROKER_URL = "tcp://172.20.10.5:1883";
//    public static final String BROKER_URL = "http://172.16.30.185:8080/send?topic=sss&content=5555bvfnhgfh";
//    public static final String BROKER_URL = "tcp://172.16.30.185:1883";//zzx
//    public static final String BROKER_URL = "tcp://172.16.30.234:1883";//东东
    public static final String BROKER_URL = "tcp://172.16.30.226:1883";//浩浩
    public static final String LOCAL_URL = "tcp://192.168.10.2111:1883";//浩浩
    //    public static final String TOPIC = "com.ceiv.hw.communication.rx5";//东东
    public static  String TOPIC_MEETING_LIST = "";//浩浩
    public static  String TOPIC_MEETING_CUR = "";//浩浩
    //    private String userName = "dongdongjia";//东东
//    private String passWord = "dongdongjia";//东东
    private static String userName = "atv1111";
    private static String passWord = "atv1111";
    private static String roomNum;//会议室编号

    public MqttClient mqttClient;
    public MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private ConnectivityManager mConnectivityManager; //网络状态监测
    private static CallBack mCallBack;
    private static String[] topicFilters ;
    private static int[] qos ;

    public static void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public MqttService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("com.litf")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("namePlate正在运行")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(110, notification);      // 开始前台服务
        init();
        connect();
        return super.onStartCommand(intent, flags, startId);
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化相关数据
     */
    public void init() {
        clientId = clientId + System.currentTimeMillis();
        roomNum = SDCardUtils.readTxt();
        TOPIC_MEETING_LIST = roomNum + "_meetList";
        TOPIC_MEETING_CUR = roomNum + "_currtMeet";
        topicFilters = new String[]{TOPIC_MEETING_CUR,TOPIC_MEETING_LIST};
        qos = new int[]{0,1};
        LogUtil.i("===当前会议室编号：", roomNum+"");

        // todo 设置主题
        try {
            mqttClient = new MqttClient(LOCAL_URL, clientId, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);//设置自动重连
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失，进行重新连接
                    if (isNetworkAvailable()) {
                        reconnectIfNecessary();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    LogUtil.i("===", "接收消息主题 : " + topic+";接收消息Qos :"  + message.getQos());
                    String str = new String(message.getPayload());
                    LogUtil.i("===MqttService", "topic:" + topic + ";----message:" + str);
                    mCallBack.setData(topic, str);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    long messageId = token.getMessageId();
                    LogUtil.e("===", "messageId=:" + messageId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    /**
     * 如果网络状态正常则返回true反之flase
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        return (info == null) ? false : info.isConnected();
    }

    /**
     * 进行重新连接前判断client状态
     */
    public synchronized void reconnectIfNecessary() {
        if (mqttClient == null || !mqttClient.isConnected()) {
            connect();
        }
    }
    public  void publish(String topic ,String jsonString,int qos) {
        try {
            if (mqttClient!=null) {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
//                message.setRetained(isRetained);
                message.setPayload(jsonString.getBytes());
                mqttClient.publish(topic, message);
                LogUtil.d("===","发送topic : "+topic+"；发送的消息msg："+jsonString);
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    /*连接服务器，并订阅消息主题*/
    private void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient.connect(options);
//                    mqttClient.subscribe(TOPIC_MEETING_CUR);
//                    mqttClient.subscribe(TOPIC_MEETING_LIST);
                    mqttClient.subscribe(topicFilters);
//                    mqttClient.subscribe(topicFilters,qos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 调用init() 方法之后，调用此方法。
     */
    public void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mqttClient.isConnected() && isNetworkAvailable()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 网络状态发生变化接收器
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("BroadcastReceiver", "Connectivity Changed...");
            if (!isNetworkAvailable()) {
                Toast.makeText(context, "网络连接不可用，请检查网络", Toast.LENGTH_SHORT).show();
//                scheduler.shutdownNow();//如果当前无网络，调用此方法，此时打开app会崩溃，
            } else {
                startReconnect();
            }
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
