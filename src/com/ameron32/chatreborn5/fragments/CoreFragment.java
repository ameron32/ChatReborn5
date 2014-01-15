package com.ameron32.chatreborn5.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.interfaces.OnFragmentInteractionListener;
import com.ameron32.chatreborn5.organization.FragmentOrganizer;
import com.ameron32.chatreborn5.organization.FragmentOrganizer.FragmentReference;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link CoreFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link CoreFragment#newInstance} factory method
 * to create an instance of this fragment.
 * 
 */
public class CoreFragment extends Fragment {
  
  public static final boolean                       DEBUG      = true;
  
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  protected static final String                     id         = "fragmentId";
  
  // TODO: Rename and change types of parameters
  protected int                                     fragmentId;
  
  private final List<OnFragmentInteractionListener> mListeners = new ArrayList<OnFragmentInteractionListener>();
  
  /**
   * Use this factory method to create a new instance of this fragment using the
   * provided parameters.
   * 
   * @param fragmentId
   *          Id of the new fragment.
   * @return A new instance of fragment BlankFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static CoreFragment newInstance(int fragmentId) {
    CoreFragment fragment = new CoreFragment();
    Bundle args = new Bundle();
    args.putInt(id, fragmentId);
    fragment.setArguments(args);
    return fragment;
  }
  
  protected FragmentReference parentReference;
  
  public void setReference(FragmentReference parentReference) {
    this.parentReference = parentReference;
  }
  
  public CoreFragment() {
    Log.d("CoreFragment", "constructor");
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d("CoreFragment", "onCreate");
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      fragmentId = getArguments().getInt(id);
      
      setReference(FragmentOrganizer.findItemById(fragmentId));
    }
  }
  
  protected View mRootView;
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d("CoreFragment", "onCreateView");
    // Inflate the layout for this fragment
    mRootView = inflater.inflate(R.layout.fragment_blank, container, false);
    
    // DEBUG
    if (DEBUG) {
      if (mRootView == null) { throw new NullPointerException("mRootView is null"); }
      if (mRootView.findViewById(R.id.new_text_view) == null) { throw new NullPointerException("View = mRootView.findViewById is null"); }
      if (parentReference == null) { throw new NullPointerException("parentReference is null"); }
      if (parentReference.title == null) { throw new NullPointerException("parentReference.title is null"); }
    }
    
    ((TextView) mRootView.findViewById(R.id.new_text_view)).setText(parentReference.title);
    
    return mRootView;
  }
  
  // TODO: Rename method, update argument and hook method into UI event
  public void onThingHappened(String thingThatHappened) {
    if (mListeners != null && mListeners.size() > 0) {
      for (OnFragmentInteractionListener l : mListeners)
        l.onFragmentInteraction(thingThatHappened);
    }
  }
  
  @Override
  public void onAttach(Activity activity) {
    Log.d("CoreFragment", "onAttach");
    super.onAttach(activity);
    try {
      mListeners.add((OnFragmentInteractionListener) activity);
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
    }
  }
  
  @Override
  public void onDetach() {
    Log.d("CoreFragment", "onDetach");
    super.onDetach();
    mListeners.clear();
  }
  
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    Log.d("CoreFragment", "onConfigurationChanged");
    super.onConfigurationChanged(newConfig);
  }
  
  @Override
  public void onDestroy() {
    Log.d("CoreFragment", "onDestroy");
    super.onDestroy();
  }
  
  @Override
  public void onDestroyView() {
    Log.d("CoreFragment", "onDestroyView");
    super.onDestroyView();
  }
  
  @Override
  public void onPause() {
    Log.d("CoreFragment", "onPause");
    super.onPause();
  }
  
  @Override
  public void onResume() {
    Log.d("CoreFragment", "onResume");
    super.onResume();
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    Log.d("CoreFragment", "onSaveInstanceState");
    super.onSaveInstanceState(outState);
  }
  
  @Override
  public void onStart() {
    Log.d("CoreFragment", "onStart");
    super.onStart();
  }
  
  @Override
  public void onStop() {
    Log.d("CoreFragment", "onStop");
    super.onStop();
  }
  
}
