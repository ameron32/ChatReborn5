package com.ameron32.chatreborn5.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.R.id;
import com.ameron32.chatreborn5.R.layout;
import com.ameron32.chatreborn5.dummy.UserContent;
import com.ameron32.chatreborn5.dummy.UserContent.User;

public class UserAdapter extends BaseAdapter {
  
  // TODO replace with a collection of real data
  private static final List<User> DATA = UserContent.ITEMS;
  
  private LayoutInflater               mInflater;
  
  public UserAdapter(Context context) {
    // Cache the LayoutInflate to avoid asking for a new one each time.
    mInflater = LayoutInflater.from(context);
    
  }
  
  /**
   * @see android.widget.ListAdapter#getCount()
   */
  public int getCount() {
    return DATA.size();
  }
  
  /**
   * @see android.widget.ListAdapter#getItem(int)
   */
  public Object getItem(int position) {
    return DATA.get(position);
  }
  
  /**
   * Use the array index as a unique id.
   * 
   * @see android.widget.ListAdapter#getItemId(int)
   */
  public long getItemId(int position) {
    return position;
  }
  
  /**
   * Make a view to hold each row.
   * 
   * @see android.widget.ListAdapter#getView(int, android.view.View,
   *      android.view.ViewGroup)
   */
  public View getView(int position, View convertView, ViewGroup parent) {
    // A ViewHolder keeps references to children views to avoid unneccessary
    // calls
    // to findViewById() on each row.
    ViewHolder holder;
    
    // When convertView is not null, we can reuse it directly, there is no need
    // to reinflate it. We only inflate a new View when the convertView supplied
    // by ListView is null.
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.list_item_user, parent, false);
      
      // Creates a ViewHolder and store references to the two children views
      // we want to bind data to.
      holder = new ViewHolder();
      // TODO store references to your views
      holder.title = (TextView) convertView.findViewById(R.id.name);
      holder.subtitle = (TextView) convertView.findViewById(R.id.notes);
      holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
      
      convertView.setTag(holder);
    }
    else {
      // Get the ViewHolder back to get fast access to the TextView
      // and the ImageView.
      holder = (ViewHolder) convertView.getTag();
    }
    
    // TODO Bind your data efficiently with the holder.
    holder.title.setText(DATA.get(position).name);
    holder.subtitle.setText(DATA.get(position).notes);
    holder.thumbnail.setImageResource(DATA.get(position).thumbnail);
    
    return convertView;
  }
  
  static class ViewHolder {
    
    // TODO define members for each view in the item layout
    TextView  title;
    TextView  subtitle;
    ImageView thumbnail;
  }
  
}
