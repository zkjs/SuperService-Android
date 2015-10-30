package com.zkjinshi.superservice.view.scviewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.common.LoginActivity;

import java.util.ArrayList;

/**
 * Created by Samuel on 2015-07-06.
 */

public class SCViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentList;

    private int mNumberOfPage = 0;
    private int mBackgroundColor;

    public SCViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SCViewPager1Fragment());
        mFragmentList.add(new SCViewPager2Fragment());
        mFragmentList.add(new SCViewPager3Fragment());
        mFragmentList.add(new SCViewPager4Fragment());
    }

    public void setNumberOfPage(int numberOfPage) {
        mNumberOfPage = numberOfPage;
    }

    public void setFragmentBackgroundColor(int colorResource) {
        mBackgroundColor = colorResource;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if (position <= mFragmentList.size()-1){
            fragment = mFragmentList.get(position);
        }
        if (fragment == null) {
            SCViewPagerFragment sCViewPagerFragment = new SCViewPagerFragment();
            sCViewPagerFragment.setBackground(mBackgroundColor);
            fragment = sCViewPagerFragment;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mNumberOfPage;
    }

    public static class SCViewPager1Fragment extends Fragment {

        public static String TAG = SCViewPager1Fragment.class.getSimpleName();

        public SCViewPager1Fragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.guide_page1,null);
            Log.i(TAG,"onCreateView");
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.i(TAG, "onActivityCreated");
        }
    }

    public static class SCViewPager2Fragment extends Fragment {

        public static String TAG = SCViewPager2Fragment.class.getSimpleName();

        public SCViewPager2Fragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.guide_page2,null);
            Log.i(TAG,"onCreateView");
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.i(TAG, "onActivityCreated");
        }
    }

    public static class SCViewPager3Fragment extends Fragment {

        public static String TAG = SCViewPager3Fragment.class.getSimpleName();

        public SCViewPager3Fragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.guide_page3,null);
            Log.i(TAG,"onCreateView");
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.i(TAG, "onActivityCreated");
        }
    }

    public static class SCViewPager4Fragment extends Fragment {

        public static String TAG = SCViewPager4Fragment.class.getSimpleName();

        public SCViewPager4Fragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.guide_page4,null);
            view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(loginIntent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                }
            });
            Log.i(TAG, "onCreateView");
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.i(TAG, "onActivityCreated");
        }
    }

    public static class SCViewPagerFragment extends Fragment {

        private int color;

        public SCViewPagerFragment() {
            this.color = R.color.white;
        }

        public void setBackground(int inColor) {
            this.color = inColor;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LinearLayout view = new LinearLayout(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            view.setOrientation(LinearLayout.VERTICAL);
            view.setBackgroundColor(getResources().getColor(this.color));
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

}
