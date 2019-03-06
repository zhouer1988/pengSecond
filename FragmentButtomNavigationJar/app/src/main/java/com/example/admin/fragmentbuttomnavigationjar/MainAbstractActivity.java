package com.example.admin.fragmentbuttomnavigationjar;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MainAbstractActivity extends AppCompatActivity {


    public static final Integer LEFT = 0;
    public static final Integer RIGHT = 1;
    public static final Integer TOP = 2;
    public static final Integer BOTTOM = 3;
    public static final Integer BACKGROUND = 4;

    private static final int FLAG_TAG = 0;
    private static final int FLAG_TAG_FIRST = 1;
    private static final int FLAG_TAG_TWO = 2;
    private static final int FLAG_TAG_THREE = 3;
    private static final int FLAG_TAG_FOUR = 4;

    /**
     * viewpager要显示的内容
     */
    private List<Fragment> fragmentList;
    /**
     * 底部按钮的文本
     */
    private List<String> bottomButtonText;
    /**
     * 底部按钮上要展示的图标
     */
    private List<Integer> bottomButtonRes;
    /**
     * 图标的方位
     */
    private List<Integer> mIconDirection;
    /**
     * 文本的大小
     */
    private int mTextSize = -1;
    /**
     * 文本颜色
     */
    private String mColorStrs = "#555555";
    /**
     * 按钮图标的大小
     */
    private Rect mDrawableBounds = new Rect(0, 0, 32, 32);
    /**
     * 图标位置调整
     */
    private int mPaddingLeft = 10;
    private int mPaddingTop = 10;
    private int mPaddingRight = 10;
    private int mPaddingBottom = 10;
    /**
     * 底部ViewGroup
     */
    private LinearLayout mBottomButtonNavigationLl;
    /**
     * 中间的viewpager
     */
    private ViewPager mContainerViewPager;
    /**
     * 选中的时候底部按钮文本的颜色
     */
    private int mSelectedTextColor = Color.BLUE;
    /**
     * 设置某一个view的大小
     */
    private Map<Integer, int[]> mViewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainerViewPager = findViewById(R.id.container_view_pager);
        fragmentList = createContainerFragmentList();
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        mContainerViewPager.setAdapter(adapter);

        mBottomButtonNavigationLl = findViewById(R.id.bottom_button_navigation_ll);
        /**
         * 文本
         */
        bottomButtonText = createBottomButtonContainerText();

        /**
         * drawable的id
         */
        bottomButtonRes = createBottomButtonContainerRes();
        //icon的位置
        mIconDirection = settingBottomButtonIconDirection();
        //view的大小
        mViewSize = settingBottomButtonSize();
        //不能为空
        if (null != bottomButtonText) {
            for (int i = 0, count = bottomButtonText.size(); i < count; i++) {
                String s = bottomButtonText.get(i);
                TextView text = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                //设置params
                setViewSize(i, params);
                params.gravity = Gravity.CENTER;
                text.setGravity(Gravity.CENTER);
                text.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
                if (isCanAddViewBoolean()) {
                    Integer iconDirection;
                    if (null != mIconDirection) {
                        iconDirection = mIconDirection.get(i);
                        if (null == iconDirection) {
                            iconDirection = TOP;
                        }
                    } else {
                        iconDirection = TOP;
                    }
                    if (iconDirection.equals(BACKGROUND)) {
                        text.setBackgroundResource(bottomButtonRes.get(i));
                    } else {
                        if (-1 == mTextSize)
                            text.setTextSize(14);
                        else
                            text.setTextSize(mTextSize);
                        text.setText(s);
                        Drawable drawable = this.getResources().getDrawable(bottomButtonRes.get(i));
                        drawable.setBounds(mDrawableBounds);
                        if (iconDirection.equals(LEFT)) {
                            text.setCompoundDrawables(drawable, null, null, null);
                        } else if (iconDirection.equals(RIGHT)) {
                            text.setCompoundDrawables(null, null, drawable, null);
                        } else if (iconDirection.equals(TOP)) {
                            text.setCompoundDrawables(null, drawable, null, null);
                        } else if (iconDirection.equals(BOTTOM)) {
                            text.setCompoundDrawables(null, null, null, drawable);
                        }
                    }
                } else {
                    System.out.println("请确认底部图标和按钮数量是否一致");
                    if (-1 == mTextSize)
                        text.setTextSize(18);
                    else
                        text.setTextSize(mTextSize);
                }
                if (FLAG_TAG == i) {
                    text.setSelected(true);
                    text.setTextColor(mSelectedTextColor);
                } else {
                    text.setSelected(false);
                    text.setTextColor(Color.parseColor(mColorStrs));
                }
                mBottomButtonNavigationLl.addView(text, params);
            }
        } else {
            System.out.println("没有创建底部按钮的文本");
        }

        /**
         *  viewpager滑动监听
         */
        mContainerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i("onPageScrolled", position + "");
//                Log.i("onPageScrolled", positionOffset + "");
//                Log.i("onPageScrolled", positionOffsetPixels + "");
                /**
                 * positionOffset是当前页面滑动比例
                 * positionOffsetPixels是当前页面滑动像素
                 */
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("onPageSelected", position + "");
                /**
                 * 当onPageScrollStateChanged的状态是2时，这里就会返回当前页面的下标
                 */
                resetButtonStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i("TAG", state + "");
                /**
                 * 0  就是停止  1  手指压在屏幕上  2  滑动 手指离开屏幕
                 */
            }
        });

        /**
         * 给底部按钮添加点击事件
         */
        for (int i = FLAG_TAG, count = mBottomButtonNavigationLl.getChildCount(); i < count; i++) {
            final TextView childView = (TextView) mBottomButtonNavigationLl.getChildAt(i);
            final int finalI = i;
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = mContainerViewPager.getCurrentItem();
                    if (finalI != currentItem) {
                        mContainerViewPager.setCurrentItem(finalI, true);
                    }
                    childView.setSelected(true);
                    resetButtonStatus(finalI);
                }
            });


        }

    }

    /**
     * 设置view的大小
     *
     * @param i
     * @param params
     */
    private void setViewSize(int i, LinearLayout.LayoutParams params) {
        //首先判断子类有没有设置
        if (null != mViewSize && mViewSize.size() > 0) {
            int[] ints = mViewSize.get(i);
            if (null != ints) {
                for (int k = 0, count = ints.length; k < count; k++) {
                    int anInt = ints[k];
                    switch (k) {
                        case FLAG_TAG:
                            params.width = anInt;
                            break;
                        case FLAG_TAG_FIRST:
                            params.height = anInt;
                            break;
                        case FLAG_TAG_TWO:
                            params.leftMargin = anInt;
                            break;
                        case FLAG_TAG_THREE:
                            params.rightMargin = anInt;
                            break;
                    }
                }
            } else {
                params.weight = 1;
            }
        } else {
            params.weight = 1;
        }
    }

    /**
     * 设置底部view中某一个的大小
     * Integer 是指view在viewgroup中的下标值
     *
     * @return
     */
    protected Map<Integer, int[]> settingBottomButtonSize() {
        return null;
    }

    /**
     * 重置底部按钮状态
     */
    private void resetButtonStatus(int n) {
        for (int i = FLAG_TAG, count = mBottomButtonNavigationLl.getChildCount(); i < count; i++) {
            if (i != n) {
                mBottomButtonNavigationLl.getChildAt(i).setSelected(false);
                ((TextView) mBottomButtonNavigationLl.getChildAt(i)).setTextColor(Color.parseColor(mColorStrs));
            } else {
                mBottomButtonNavigationLl.getChildAt(i).setSelected(true);
                ((TextView) mBottomButtonNavigationLl.getChildAt(i)).setTextColor(mSelectedTextColor);
            }
        }
    }

    /**
     * 是否可以添加textView
     *
     * @return
     */
    private boolean isCanAddViewBoolean() {
        return null != bottomButtonRes && bottomButtonRes.size() > 0 && bottomButtonRes.size() == bottomButtonText.size();
    }

    /**
     * icon的位置
     *
     * @return
     */
    protected List<Integer> settingBottomButtonIconDirection() {
        return null;
    }

    /**
     * 底部按钮文本颜色
     *
     * @param mColorStrs
     */
    public void setmColorStrs(String mColorStrs) {
        this.mColorStrs = mColorStrs;
    }

    /**
     * 图标位置调整
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param isUseDefault 如果传进来是零的话是否使用默认值
     */
    public void setPadding(int left, int top, int right, int bottom, boolean isUseDefault) {
        if (FLAG_TAG < left) {
            mPaddingLeft = left;
        } else {
            if (!isUseDefault) {
                mPaddingLeft = left;
            }
        }

        if (FLAG_TAG < top) {
            mPaddingTop = top;
        } else {
            if (!isUseDefault) {
                mPaddingTop = top;
            }
        }
        if (FLAG_TAG < right) {
            mPaddingRight = right;
        } else {
            if (!isUseDefault) {
                mPaddingRight = right;
            }
        }
        if (FLAG_TAG < bottom) {
            mPaddingBottom = bottom;
        } else {
            if (!isUseDefault) {
                mPaddingBottom = bottom;
            }
        }
    }

    /**
     * 单个设置
     *
     * @param mPaddingLeft
     */
    public void setmPaddingLeft(int mPaddingLeft) {
        this.mPaddingLeft = mPaddingLeft;
    }

    /**
     * 单个设置
     *
     * @param mPaddingTop
     */
    public void setmPaddingTop(int mPaddingTop) {
        this.mPaddingTop = mPaddingTop;
    }

    /**
     * 单个设置
     *
     * @param mPaddingRight
     */
    public void setmPaddingRight(int mPaddingRight) {
        this.mPaddingRight = mPaddingRight;
    }

    /**
     * 单个设置
     *
     * @param mPaddingBottom
     */
    public void setmPaddingBottom(int mPaddingBottom) {
        this.mPaddingBottom = mPaddingBottom;
    }

    /**
     * 文本的大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    /**
     * 设置底部图标的id
     *
     * @return
     */
    @Nullable
    protected abstract List<Integer> createBottomButtonContainerRes();

    /**
     * 创建底部按钮
     * 如果是用图标来替换其中一个文本，需要传null
     *
     * @return
     */
    @Nullable
    protected abstract List<String> createBottomButtonContainerText();

    /**
     * 子类创建viewpager要显示的内容
     *
     * @return
     */
    @Nullable
    protected abstract List<Fragment> createContainerFragmentList();
}
