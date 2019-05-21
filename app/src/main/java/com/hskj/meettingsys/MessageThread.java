package com.hskj.meettingsys;

import android.os.Handler;
import android.os.Message;

public class MessageThread extends Thread {

  private static final int CURRENTDATETIME = 111;
  private static MessageThread messageThread;
  private OnGetMQTTMessageListener listener;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case CURRENTDATETIME:
          if (listener !=null){
            listener.onGetMQTTMessage();
          }
          break;
      }
    }
  };

  public MessageThread(OnGetMQTTMessageListener listener) {

    this.listener = listener;
  }


  @Override
  public void run() {
    super.run();
    do {
      try {
        Thread.sleep(10000);
        Message msg = new Message();
        msg.what = CURRENTDATETIME;
        mHandler.sendMessage(msg);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (true);


  }
    
}
