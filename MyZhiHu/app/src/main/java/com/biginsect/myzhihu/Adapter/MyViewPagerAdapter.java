package com.biginsect.myzhihu.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by administration on 2016/8/22.
 */
public class MyViewPagerAdapter extends PagerAdapter {
    private List<View> viewList;
    private List<String> titles;

    public MyViewPagerAdapter(List<View> viewList,List<String> titles) {
        this.viewList = viewList;
        this.titles = titles;
    }

    @Override
    public int getCount() {
            return viewList.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }



}
