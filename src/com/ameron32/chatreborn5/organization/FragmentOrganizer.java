package com.ameron32.chatreborn5.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ameron32.chatreborn5.fragments.BlankFragment;

import android.support.v4.app.Fragment;

public class FragmentOrganizer {
  
  public static List<FragmentReference>        ITEMS    = new ArrayList<FragmentReference>();
  
  public static Map<String, FragmentReference> ITEM_MAP = new HashMap<String, FragmentReference>();
  
  static {
    addItem(new FragmentReference("1", "Network Status", BlankFragment.newInstance(null, null, 1)));
    addItem(new FragmentReference("2", "Chat Management", BlankFragment.newInstance(null, null, 2)));
    addItem(new FragmentReference("3", "App Settings", BlankFragment.newInstance(null, null, 3)));
    addItem(new FragmentReference("4", "Player Info", BlankFragment.newInstance(null, null, 4)));
    addItem(new FragmentReference("5", "Character", BlankFragment.newInstance(null, null, 5)));
    addItem(new FragmentReference("6", "World", BlankFragment.newInstance(null, null, 6)));
    addItem(new FragmentReference("7", "Library", BlankFragment.newInstance(null, null, 7)));
    addItem(new FragmentReference("8", "Battle", BlankFragment.newInstance(null, null, 8)));
  }
  
  private static void addItem(FragmentReference item) {
    ITEMS.add(item);
    ITEM_MAP.put(item.id, item);
  }
  
  public static FragmentReference findItemById(int id) {
    for (FragmentReference fr : ITEMS) {
      if (Integer.decode(fr.id) == id) return fr;
    }
    return null;
  }
  
  public static class FragmentReference {
    
    public String   id;
    public String   title;
    public Fragment fragment;
    
    public FragmentReference(String id, String title, BlankFragment fragment) {
      this.id = id;
      this.title = title;
      fragment.setReference(this);
      this.fragment = fragment;
    }
    
    @Override
    public String toString() {
      return title;
    }
  }
}
