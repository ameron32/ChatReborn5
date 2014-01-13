package com.ameron32.chatreborn5;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ameron32.chatreborn5.organization.FragmentOrganizer;

/**
 * A fragment representing a single Screen detail screen.
 */
public class ScreenDetailFragment extends Fragment {
  
  /**
   * The fragment argument representing the item ID that this fragment
   * represents.
   */
  public static final String                 ARG_ITEM_ID = "item_id";
  
  /**
   * The dummy content this fragment is presenting.
   */
  private FragmentOrganizer.FragmentReference mFragmentReference;
  
  private View                                mRootView;
  
  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public ScreenDetailFragment() {}
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.detail, menu);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_screen_detail, container, false);
    
    // Show the dummy content as text in a TextView.
    if (mFragmentReference != null) {
      ((TextView) mRootView.findViewById(R.id.screen_detail)).setText(mFragmentReference.title + " ......... " + mFragmentReference.title
          + " ......... " + mFragmentReference.title);
    }
    
    return mRootView;
  }
  
  public void setText(String text) {
    ((TextView) mRootView.findViewById(R.id.screen_detail)).setText(text + " ......... " + text + " ......... " + text);
    getActivity().getActionBar().setTitle(text);
  }
}
