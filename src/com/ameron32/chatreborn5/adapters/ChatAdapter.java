package com.ameron32.chatreborn5.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.notifications.NewMessageBar;

public class ChatAdapter extends BaseAdapter implements Filterable {
  
  private final Context          context;
  private ViewHolder             holder;
  private final LayoutInflater   inflater;
  
  private Map<Long, MessageBase> mData  = Global.Local.clientChatHistory.getFilteredChatHistory("standard").getFilteredHistory();
  private Map<Long, MessageBase> mFData = new TreeMap<Long, MessageBase>();
  
  public ChatAdapter(Context context) {
    super();
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.context = context;
    mFData.putAll(mData);
  }
  
  public int getCount() {
    return mFData.size();
  }
  
  public MessageBase getItem(int position) {
    return mFData.get(getKeyAt(position));
  }
  
  public long getItemId(int arg0) {
    return arg0;
  }
  
  private long getKeyAt(int position) {
    int counter = 0;
    for (Map.Entry<Long, MessageBase> entry : mFData.entrySet()) {
      if (position == counter) return entry.getKey();
      counter++;
    }
    return -1l;
  }
  
  private MessageBase item;
  
  @Override
  public View getView(final int position, View convertView, final ViewGroup parent) {
    item = getItem(position);
    // item.attachTags(MessageTag.MessageViewed);
    
    if (item instanceof SystemMessage) {
      convertView = inflater.inflate(R.layout.chat_sysmsg_ui, parent, false);
      holder = new ViewHolder();
      holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
      holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
      holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
      
      convertView.setTag(holder);
    }
    if (item instanceof ChatMessage) {
      // check for continued message
      boolean useDefaultInflater = true;
      if (position != 0) {
        final MessageBase previousItem = getItem(position - 1);
        if (previousItem instanceof ChatMessage && previousItem.name.equals(item.name)) {
          
          convertView = inflater.inflate(R.layout.chat_bubble_continue, parent, false);
          holder = new ViewHolder();
          holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
          // holder.tvUsr = (TextView)
          // convertView.findViewById(R.id.tvUsr);
          holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
          
          convertView.setTag(holder);
          useDefaultInflater = false;
        }
      }
      
      if (useDefaultInflater) {
        convertView = inflater.inflate(R.layout.chat_bubble_ui, parent, false);
        holder = new ViewHolder();
        holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
        holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
        
        convertView.setTag(holder);
      }
      
      int count = item.getTagCount();
      if (count > 0) {
        holder.rlTag = (RelativeLayout) convertView.findViewById(R.id.rlTagCount);
        holder.rlTag.setVisibility(RelativeLayout.VISIBLE);
        holder.tvTagCount = (TextView) convertView.findViewById(R.id.tvTagCount);
        holder.tvTagCount.setText(String.valueOf(count));
        
      }
    }
    
    // both share slide rear
    holder.bComment = (ImageButton) convertView.findViewById(R.id.bAddComment);
    holder.bTag = (ImageButton) convertView.findViewById(R.id.bAddTags);
    holder.bEdit = (ImageButton) convertView.findViewById(R.id.bEditMessage);
    holder.bHide = (ImageButton) convertView.findViewById(R.id.bHideMessage);
    holder.bDelete = (ImageButton) convertView.findViewById(R.id.bDeleteMessage);
    
    // tmp OnClickListener
    final View.OnClickListener tmp = new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        NewMessageBar.showMessage(context, "Not yet implemented.");
      }
    };
    
    // cheating to allow SystemMessage to avoid these buttons
    if (item instanceof ChatMessage) {
      holder.bComment.setOnClickListener(tmp);
      holder.bTag.setOnClickListener(tmp);
      holder.bEdit.setOnClickListener(tmp);
      holder.bHide.setOnClickListener(tmp);
      holder.bDelete.setOnClickListener(tmp);
    }
    
    Long timeStamp = item.getTimeStamp();
    holder.tvTime.setText(new SimpleDateFormat("h:mma", Locale.US).format(timeStamp));
    if (holder.tvUsr != null) holder.tvUsr.setText(item.name);
    
    holder.tvMsg.setText(item.getText());
    // holder.tvMsg.setTypeface(Loader.fonts.get(Fonts.neutonregular));
    
    item = null;
    return convertView;
  }
  
  public static class ViewHolder {
    
    TextView tvTime, tvUsr, tvMsg;
    ImageButton bComment, bTag, bEdit, bHide, bDelete;
    
    RelativeLayout rlComment, rlTag, rlEdit;
    TextView       tvCommentCount, tvTagCount, tvEditCount;
    
    public void clear() {
      rlComment = null;
      rlTag = null;
      rlEdit = null;
      tvCommentCount = null;
      tvTagCount = null;
      tvEditCount = null;
    }
  }
  
  public void clear() {
    mData.clear();
  }
  
  public void addAll(TreeMap<Long, MessageBase> history) {
    mData.putAll(history);
  }
  
  public void remove(int position) {
    mData.remove(getKeyAt(position));
  }
  
  @Override
  public Filter getFilter() {
    mFData.putAll(mData);
    return myFilter;
  }
  
  private MessageTag[] testFilter = { MessageTag.HasStartedTypingMessage, MessageTag.HasStoppedTypingMessage };
  
  private Filter     myFilter   = new Filter() {
                                  
    @Override
    protected FilterResults performFiltering(final CharSequence constraint) {
      FilterResults filterResults = new FilterResults();
      filterResults.values = new TreeMap<Long, MessageBase>();
      // constraint is the result from text you
      // want to filter against. objects is your
      // data set you will filter from
      if (constraint != null && mData != null) {
        for (Map.Entry<Long, MessageBase> entry : mFData.entrySet()) {
          MessageBase item = entry.getValue();
          
          boolean fail = false;
          // do whatever you wanna do here
          // adding result set output array
          if (constraint.length() > 0 && !item.getText().contains(constraint)) {
            Log.d("ChatAdapter", "exclude " + item.getText() + " with constraint "
                + constraint.length());
            fail = true;
          }
          if (item.hasAnyOfTags(testFilter)) {
            Log.d("ChatAdapter", "exclude " + item.getText() + " with Filter");
            fail = true;
          }
          
          // if it didn't fail, add it to the results
          if (!fail)
            ((Map<Long, MessageBase>) filterResults.values).put(item.getTimeStamp(), item);
        }
        
        // following two lines is very important
        // as publish result can only take
        // FilterResults objects
        filterResults.count = ((Map<Long, MessageBase>) filterResults.values).size();
      }
      return filterResults;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(final CharSequence contraint,
        final FilterResults results) {
      mFData.clear();
      if (results.values != null) mFData.putAll((Map<Long, MessageBase>) results.values);
      if (results.count > 0) {
        notifyDataSetChanged();
      }
      else {
        notifyDataSetInvalidated();
      }
    }
  };
}
