package com.ameron32.chatreborn5.fragments;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.R.layout;
import com.ameron32.chatreborn5.notifications.NewMessageNotification;
import com.ameron32.chatreborn5.organization.FragmentOrganizer;
import com.ameron32.chatreborn5.organization.FragmentOrganizer.FragmentReference;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link BlankFragment#newInstance} factory method
 * to create an instance of this fragment.
 * 
 */
public class BlankFragment extends Fragment {
  public static final boolean DEBUG = true;
  
  
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String           ARG_PARAM1 = "param1";
  private static final String           ARG_PARAM2 = "param2";
  private static final String           id = "fragmentId";
  
  // TODO: Rename and change types of parameters
  private String                        mParam1;
  private String                        mParam2;
  private int                           fragmentId;
  
  private OnFragmentInteractionListener mListener;
  
  /**
   * Use this factory method to create a new instance of this fragment using the
   * provided parameters.
   * 
   * @param param1
   *          Parameter 1.
   * @param param2
   *          Parameter 2.
   * @return A new instance of fragment BlankFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static BlankFragment newInstance(String param1, String param2, int fragmentId) {
    BlankFragment fragment = new BlankFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    args.putInt(id, fragmentId);
    fragment.setArguments(args);
    return fragment;
  }
  
  private FragmentReference parentReference;
  public void setReference(FragmentReference parentReference) {
    this.parentReference = parentReference;
  }
  
  public BlankFragment() {
    // Required empty public constructor
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
      fragmentId = getArguments().getInt(id);
      
      setReference(FragmentOrganizer.findItemById(fragmentId));
    }
  }
  
  View mRootView;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    mRootView = inflater.inflate(R.layout.fragment_blank, container, false);
    
    if (mRootView == null) {
      throw new NullPointerException("mRootView is null"); 
    }
    if (mRootView.findViewById(R.id.new_text_view) == null) {
      throw new NullPointerException("View = mRootView.findViewById is null");
    }
    if (parentReference == null) {
      throw new NullPointerException("parentReference is null");
    }
    if (parentReference.title == null) {
      throw new NullPointerException("parentReference.title is null");
    }
    
    
    ((TextView) mRootView.findViewById(R.id.new_text_view)).setText(parentReference.title);
    
    return mRootView;
  }
  
  // TODO: Rename method, update argument and hook method into UI event
  public void onThingHappened(String thingThatHappened) {
    if (mListener != null) {
      mListener.onFragmentInteraction(thingThatHappened);
    }
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
    }
    
    NewMessageNotification.notify(getActivity(), this.getClass().getSimpleName() + " attached", fragmentId);
  }
  
  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
  

  public interface OnFragmentInteractionListener {
    
    public void onFragmentInteraction(String thingThatHappened);
  }
  
}
