package com.ameron32.chatreborn5.views;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.interfaces.ChatConnectionWatcher;
import com.ameron32.chatreborn5.notifications.NewMessageBar;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatService.ChatConnectionState;

public class SendBar extends RelativeLayout implements ChatConnectionWatcher {
  
  private Context                   context;
  
  private LayoutInflater            inflater;
  
  private MultiAutoCompleteTextView etMessage;
  private ImageButton               btn_clear;
  private ImageButton               voice;
  private ImageButton               ibSend;
  private ImageView                 redButton;
//  private ImageView                 voiceMicView;
  
  private TextView                  tvDebug;
  
  public SendBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    init();
  }
  
  public SendBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init();
  }
  
  public SendBar(Context context) {
    super(context);
    this.context = context;
    init();
  }
  
  private void init() {
    Log.d("SendBar", "init");
    
    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.send_text, this, true);
    
    etMessage = (MultiAutoCompleteTextView) findViewById(R.id.message);
    btn_clear = (ImageButton) findViewById(R.id.clear_text);
    
    ibSend = (ImageButton) findViewById(R.id.send_button);
    voice = (ImageButton) findViewById(R.id.mic_button);
    redButton = (ImageView) findViewById(R.id.redButton);
//    voiceMicView = (ImageView) findViewById(R.id.mic_sound_view);
    // voiceMicView.setImageDrawable(createVMV());
    
    tvDebug = (TextView) findViewById(R.id.tvDebug);

    ibSend.setVisibility(INVISIBLE);
    voice.setVisibility(VISIBLE);
    btn_clear.setVisibility(INVISIBLE);
    
    setHint("Message");
    setButtonClearListener();
    setVoiceListener();
    setShowHideButtonsListener();
    setSendListener();
    
    if (ServicesOrganizer.chatClient != null) {
      if (ServicesOrganizer.chatClient.getState() == ChatConnectionState.OFFLINE) hideMe();
      else if (ServicesOrganizer.chatClient.getState() == ChatConnectionState.ONLINE) showMe();
      else hideMe();
    } else { hideMe(); }
  }
  
  private void hideMe() {
    setVisibility(INVISIBLE);
  }
  
  private void showMe() {
    setVisibility(VISIBLE);
  }
  
  private void tvd(String s) {
    tvDebug.setText(s);
  }
  
  private void setButtonClearListener() {
    btn_clear.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        resetEditText();
      }
    });
  }
  
  SpeechRecognizer sr;
  boolean          isListening = false;
  
  private void setVoiceListener() {
    sr = SpeechRecognizer.createSpeechRecognizer(context.getApplicationContext());
    sr.setRecognitionListener(new RecognitionListener() {
      
      @Override
      public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        tvd("onBeginningOfSpeech");
      }
      
      @Override
      public void onBufferReceived(byte[] buffer) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void onRmsChanged(float rmsdB) {
        // TODO Auto-generated method stub
        // voiceMicView.setScaleX(rmsdB);
        // voiceMicView.setScaleY(rmsdB);
        
      }
      
      @Override
      public void onResults(Bundle results) {
        ArrayList<String> voiceresults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (voiceresults != null) {
          appendToEditText(voiceresults.get(0));
        }
        tvd("onResults");
      }
      
      @Override
      public void onReadyForSpeech(Bundle params) {
//        voice.setImageDrawable(getResources().getDrawable(R.drawable.micwhite));
//        voiceMicView.setVisibility(ImageView.VISIBLE);
        tvd("onReadyForSpeech");
      }
      
      @Override
      public void onPartialResults(Bundle partialResults) {
        // TODO Auto-generated method stub
        tvd("onPartialResults");
      }
      
      @Override
      public void onEvent(int eventType, Bundle params) {
        // TODO Auto-generated method stub
        tvd("onEvent");
      }
      
      @Override
      public void onError(int error) {
        // TODO Auto-generated method stub
        // voice.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
        // voiceMicView.setVisibility(ImageView.INVISIBLE);
        tvd("onError " + err(error));
      }
      
      private String err(int errorCode) {
        String code = "";
        switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
          code = "Error Audio";
          break;
        case SpeechRecognizer.ERROR_CLIENT:
          code = "Error Client";
          break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
          code = "Error Insufficient Permissions";
          break;
        case SpeechRecognizer.ERROR_NETWORK:
          code = "Error Network";
          break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
          code = "Error Network Timeout";
          break;
        case SpeechRecognizer.ERROR_NO_MATCH:
          code = "Error No Match";
          break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
          code = "Error Recognizer Busy";
          break;
        case SpeechRecognizer.ERROR_SERVER:
          code = "Error Server";
          break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
          code = "Error SpeechTimeout";
          break;
        }
        return code;
      }
      
      @Override
      public void onEndOfSpeech() {
//        voice.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
//        voiceMicView.setVisibility(ImageView.INVISIBLE);
        tvd("onEndOfSpeech");
      }
      
    });
    voice.setOnTouchListener(new View.OnTouchListener() {
      
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          sr.startListening(RecognizerIntent.getVoiceDetailsIntent(context.getApplicationContext()));
          break;
        case MotionEvent.ACTION_UP:
          sr.stopListening();
          break;
        }
        return false;
      }
    });
  }
  
  private void setShowHideButtonsListener() {
    etMessage.addTextChangedListener(new TextWatcher() {
      
      boolean toggle = false;
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (connected) {
          if (s.length() > 0) {
            if (toggle == false) {
              toggle = true;
//              SendTask sendTask = new SendTask(isTypingListener, false);
//              sendTask.execute();
              sendTypingMessage(true);
              
              btn_clear.setVisibility(RelativeLayout.VISIBLE);
              ibSend.setVisibility(RelativeLayout.VISIBLE);
              voice.setVisibility(RelativeLayout.INVISIBLE);
              redButton.setImageDrawable(getResources().getDrawable(R.drawable.blue_button_x));
            }
          }
          else {
            if (toggle == true) {
//              SendTask sendTask = new SendTask(isNotTypingListener, false);
//              sendTask.execute();
              sendTypingMessage(false);
              toggle = false;
            }
            if (ibSend.getVisibility() != RelativeLayout.INVISIBLE) {
              btn_clear.setVisibility(RelativeLayout.INVISIBLE);
              ibSend.setVisibility(RelativeLayout.INVISIBLE);
              voice.setVisibility(RelativeLayout.VISIBLE);
              redButton.setImageDrawable(getResources().getDrawable(R.drawable.red_button_x));
            }
          }
        }
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      
      @Override
      public void afterTextChanged(Editable s) {}
    });
  }
  
  public Editable getText() {
    Editable text = etMessage.getText();
    return text;
  }
  
  private void resetEditText() {
    etMessage.setText("");
    // etMessage.requestFocus();
  }
  
//  public class SendTask extends AsyncTask<String, Integer, String> {
//    
//    private Runnable listener;
//    private boolean  resetWhenComplete;
//    
//    public SendTask(Runnable listener, boolean resetWhenComplete) {
//      this.listener = listener;
//      this.resetWhenComplete = resetWhenComplete;
//    }
//    
//    @Override
//    protected String doInBackground(String... params) {
//      listener.run();
//      return null;
//    }
//    
//    @Override
//    protected void onPostExecute(String result) {
//      super.onPostExecute(result);
//      if (resetWhenComplete) resetEditText();
//    }
//    
//  }
  
  private void setSendListener() {
    ibSend.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
    etMessage.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)
            && (!event.isShiftPressed())) {
          sendMessage();
          return true;
        }
        
        // voice
        switch (event.getAction()) {
        case KeyEvent.ACTION_DOWN:
          if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            sr.startListening(RecognizerIntent.getVoiceDetailsIntent(context.getApplicationContext()));
          }
          break;
        case KeyEvent.ACTION_UP:
          if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            sr.stopListening();
          }
          break;
        }
        return false;
      }
    });
    etMessage.setOnEditorActionListener(new OnEditorActionListener() {
      
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          sendMessage();
          return true;
        }
        return false;
      }
    });
    
  }
  
  private void appendToEditText(String s) {
    etMessage.getText().append(s);
  }
  
  public void setHint(String hint) {
    etMessage.setHint(hint);
  }
  
  boolean connected = false;
//  
//  public void setConnected(boolean connected) {
//    this.connected = connected;
//    
//    if (this.connected) {
//      setVisibility(VISIBLE);
//    }
//    else {
//      setVisibility(INVISIBLE);
//    }
//    
//  }
  
//  Runnable isTypingListener, isNotTypingListener;
  
//  public void setIsTypingListener(Runnable runnable) {
//    isTypingListener = runnable;
//  }
//  
//  public void setIsNotTypingListener(Runnable runnable) {
//    isNotTypingListener = runnable;
//  }
  
  private void sendMessage() {
    
    if (ServicesOrganizer.chatClient != null) {
      final String msg = getText().toString().trim();
      if (msg != null && msg.length() > 0) {
        ServicesOrganizer.chatClient.sendMessage(msg);
      }
    }
    else { NewMessageBar.showMessage(context, "ChatClient has not been initialized."); }
    
    resetEditText();
  }
  
  private void sendTypingMessage(boolean isTyping) {
    final SystemMessage systemMessage = new SystemMessage();
    systemMessage.name = Global.Local.username;
    if (isTyping) systemMessage.setText("isTyping");
    else systemMessage.setText("isNotTyping");
    systemMessage.attachTags(MessageTag.ServerChatter);
    ServicesOrganizer.chatClient.sendMessage(systemMessage);
  }  

  @Override
  public void onChatConnectionStateChanged(ChatService chatService, ChatConnectionState prevState,
      ChatConnectionState nextState) {
    if (chatService.getTag().equals("ChatClient")) {
      if (nextState == ChatConnectionState.OFFLINE) {
        connected = false;
        this.post(new Runnable() {
          
          public void run() {
            hideMe();
          }
        });
      }
      if (nextState == ChatConnectionState.ONLINE) {
        connected = true;
        this.post(new Runnable() {
          
          public void run() {
            showMe();
          }
        });
      }
    }
  }
}
