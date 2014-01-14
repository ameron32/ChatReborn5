package com.ameron32.chatreborn5.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.ameron32.chatreborn5.MainActivity;
import com.ameron32.chatreborn5.interfaces.ChatConnectionWatcher;
import com.ameron32.chatreborn5.notifications.NewMessageNotification;

public abstract class ChatService extends Service {
  public abstract String getTag();
  private static final Map<String, ChatService> chatServices = new HashMap<String, ChatService>();
  protected void register() {
    chatServices.put(getTag(), this);
  }
  
	private String START_NOTIFICATION_ID;
	private String STOP_NOTIFICATION_ID;
	
	@Override
	public IBinder onBind(Intent intent) {
		return getMyBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startNotification(getSTART_NOTIFICATION_ID());
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		watchers.clear();
		stopNotification(getSTOP_NOTIFICATION_ID());
		super.onDestroy();
	}

	// --------------------------------------
	// SERVICE RELATED NOTIFICATIONS
	// --------------------------------------

	private final Context context = this;

//	private NotificationManager init(NotificationManager nManager) {
//		if (nManager == null) 
//			return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		return nManager;
//	}
	
	private void startNotification(String id) {
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				context).setSmallIcon(R.drawable.like)
//				.setContentTitle(getSimpleName() + " Started")
//				.setContentText("Click to Open Application");
//
//		Intent targetIntent = new Intent(context, MainActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		builder.setContentIntent(contentIntent);
//		nManager = init(nManager);
//		nManager.notify(getSTART_NOTIFICATION_ID(), builder.build());

	  NewMessageNotification.notify(context, new Intent(context, MainActivity.class), getSimpleName() + " Started", "Click to Open Application", 0, getSTART_NOTIFICATION_ID());
	  
		clearNotification(getSTOP_NOTIFICATION_ID());
	}

	private void stopNotification(String id) {
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				context).setSmallIcon(R.drawable.delete)
//				.setContentTitle(getSimpleName() + " Stopped")
//				.setContentText("Click to Open Application");
//
//		Intent targetIntent = new Intent(context, MainActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		builder.setContentIntent(contentIntent);
//		init(nManager);
//		nManager.notify(getSTOP_NOTIFICATION_ID(), builder.build());

	  NewMessageNotification.notify(context, new Intent(context, MainActivity.class), getSimpleName() + " Started", "Click to Open Application", 0, getSTOP_NOTIFICATION_ID());
	  
		clearNotification(getSTART_NOTIFICATION_ID());
		
	}

	private String getSimpleName() {
		return getClass().getSimpleName();
	}

	public class MyBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	private IBinder myBinder = new MyBinder();

	protected IBinder getMyBinder() {
		return myBinder;
	}

	protected void setMyBinder(IBinder myBinder) {
		this.myBinder = myBinder;
	}
	
	private static final String MESSAGE_NOTIFICATIONS = "NewMessage";
	protected void notifyMessage(String msg) {
		createNotification(msg, "Click to OpenApplication", MESSAGE_NOTIFICATIONS);
	}
	
	protected void clearNotification(String id) {
		NewMessageNotification.cancel(context, id);
	}
	
	private void createNotification(String title, String text, String id) {
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				context).setSmallIcon(R.drawable.chess)
//				.setContentTitle(title)
//				.setContentText(text);
//		Intent targetIntent = new Intent(context, MainActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		builder.setContentIntent(contentIntent);
//		builder.setAutoCancel(true);
//		
//		init(nManager);
//		nManager.notify(getSTOP_NOTIFICATION_ID(), builder.build());
	  
	  NewMessageNotification.notify(context, new Intent(context, MainActivity.class), title, text, 0, MESSAGE_NOTIFICATIONS);
	}

	// --------------------------------------
	// NETWORK THREAD HANDLING
	// --------------------------------------
	
	
  abstract public class SimpleAsyncTask extends AsyncTask<Void, Void, Void> {
    
    abstract protected void doInBackground();
    
    abstract protected void onPostExecute();
    
    @Override
    final protected Void doInBackground(Void... unused) {
      doInBackground();
      
      return null;
    }
    
    @Override
    final protected void onPostExecute(Void unused) {
      onPostExecute();
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SimpleAsyncTask execute() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return (this);
      }
      
      return (SimpleAsyncTask) (super.execute());
    }
  }
  
  abstract public class SimpleProgressAsyncTask extends AsyncTask<Void, Integer, Void> {
    
    abstract protected void doInBackground();
    
    abstract protected void onPostExecute();
    
    abstract protected void onProgressUpdate(Integer values);
    
    @Override
    final protected Void doInBackground(Void... unused) {
      doInBackground();
      
      return null;
    }
    
    @Override
    final protected void onPostExecute(Void unused) {
      onPostExecute();
    }
    
    @Override
    final protected void onProgressUpdate(Integer... values) {
      onProgressUpdate(values);
      super.onProgressUpdate(values);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SimpleProgressAsyncTask execute() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return (this);
      }
      
      return (SimpleProgressAsyncTask) (super.execute());
    }
  }
	
	
  // --------------------------------------
  // REFERENCES
  // --------------------------------------
	
	private ChatConnectionState serviceState = ChatConnectionState.OFFLINE;
	public enum ChatConnectionState {
	  OFFLINE, PREPARING, PREPARED, CONNECTING, ONLINE, DISCONNECTING, DISABLING
	}
	public void changeState(ChatConnectionState nextState) {
	  ChatConnectionState prevState = serviceState;
	  serviceState = nextState;
	  for (ChatConnectionWatcher w : watchers) {
	    w.onChatConnectionStateChanged(this, prevState, nextState);
	  }
	}
	public ChatConnectionState getState() {
	  return serviceState;
	}
	
	private static final List<ChatConnectionWatcher> watchers = new ArrayList<ChatConnectionWatcher>();
	public void addWatcher(ChatConnectionWatcher watcher) {
	  watchers.add(watcher);
	}
	
	
	// --------------------------------------
	// GETTER / SETTER
	// --------------------------------------
	
	protected String getSTART_NOTIFICATION_ID() {
		return START_NOTIFICATION_ID;
	}

	protected void setSTART_NOTIFICATION_ID(String sTART_NOTIFICATION_ID) {
		START_NOTIFICATION_ID = sTART_NOTIFICATION_ID;
	}

	protected String getSTOP_NOTIFICATION_ID() {
		return STOP_NOTIFICATION_ID;
	}

	protected void setSTOP_NOTIFICATION_ID(String sTOP_NOTIFICATION_ID) {
		STOP_NOTIFICATION_ID = sTOP_NOTIFICATION_ID;
	}
}
