package com.example.admin.fragmentbuttomnavigationjar;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.admin.fragmentbuttomnavigationjar.fragments.ItemFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends MainAbstractActivity {

    private int MAX_FRAGMENT_COUNT = 5;


    @Nullable
    @Override
    protected List<Integer> createBottomButtonContainerRes() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(R.drawable.home_drawable);
        integerList.add(R.drawable.shoping_drawable);
        integerList.add(R.drawable.plus_drawable);
        integerList.add(R.drawable.message_drawable);
        integerList.add(R.drawable.me_drawable);
        return integerList;
    }

    @Override
    protected List<Integer> settingBottomButtonIconDirection() {
        List<Integer> directionList = new ArrayList<>();
        directionList.add(TOP);
        directionList.add(TOP);
        directionList.add(BACKGROUND);
        directionList.add(TOP);
        directionList.add(TOP);
        return directionList;
    }

    @Override
    protected Map<Integer, int[]> settingBottomButtonSize() {
        Map<Integer, int[]> integerMap = new HashMap<>();
        integerMap.put(2, new int[]{60, 60, 35, 35});
        return integerMap;
    }

    @Nullable
    @Override
    protected List<String> createBottomButtonContainerText() {
        List<String> stringList = new ArrayList<>();
        addButtonText(stringList);
        return stringList;
    }

    /**
     * 创建底部按钮的文本
     *
     * @param stringList
     */
    private void addButtonText(List<String> stringList) {
        for (int i = 0; i < MAX_FRAGMENT_COUNT; i++) {
            if (2 == i) {
                stringList.add(null);
            } else {
                stringList.add("按钮" + i);
            }
        }
    }

    @Nullable
    @Override
    protected List<Fragment> createContainerFragmentList() {
        List<Fragment> fragmentList = new ArrayList<>();
        addFragment(fragmentList);
        return fragmentList;
    }

    /**
     * 添加Fragment到集合中
     */
    private void addFragment(@Nullable List<Fragment> fragmentList) {
        for (int i = 0; i < MAX_FRAGMENT_COUNT; i++) {
            ItemFragment itemFragment = new ItemFragment();
            fragmentList.add(itemFragment);
        }
    }
}
