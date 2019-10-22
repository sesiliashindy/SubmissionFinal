package com.example.user.submissionfinal.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.submissionfinal.R;
import com.example.user.submissionfinal.activity.BottomNavActivity;
import com.example.user.submissionfinal.adapter.SectionPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MovieSearchFragment(),"Movie");
        adapter.addFragment(new TvSearchFragment(),"TvShow");

        viewPager.setAdapter(adapter);
    }

    public void onResume(){
        super.onResume();

        // Set title bar
        ((BottomNavActivity) getActivity())
                .setActionBarTitle("Search");
    }
}
