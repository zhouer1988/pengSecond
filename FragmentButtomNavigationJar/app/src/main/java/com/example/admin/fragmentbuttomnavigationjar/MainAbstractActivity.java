package com.example.admin.fragmentbuttomnavigationjar;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    /**
     * 当前显示的fragment的下标
     *
     * @param savedInstanceState
     */
    private int mCurrentIndex = 0;
    private FrameLayout mRightArrowFl;
    private FrameLayout mLeftArrowFl;
    /**
     * 右侧FrameLayout范围
     */
    private Rect mRightRect;
    /**
     * 左侧FrameLayout范围
     */
    private Rect mLeftRect;

    /**
     * 左右两个frame存活的时间
     */
    private volatile int mViewSurviveTime = 0;
    /**
     * 实现左右箭头的显示和隐藏
     */
    private FrameLayoutStatusListenerThread mThread;
    private TextView mLeftArrowTv;
    private TextView mRightArrowTv;

    /**
     * 两侧箭头的显示模式
     */
    public enum SideModeType {
        DEFAULT_MODE(0),
        LONG_LINE(1),
        ALWAYS_HIDE(2);

        int type;

        SideModeType(int type) {
            this.type = type;
        }

        public int getTModeType() {
            return type;
        }

        /**
         * 是否是默认模式
         *
         * @return
         */
        public boolean isDefaultMode() {
            return this.type == DEFAULT_MODE.getTModeType();
        }

        /**
         * 是否是一直隐藏模式
         *
         * @return
         */
        public boolean isHideMode() {
            return this.type == ALWAYS_HIDE.getTModeType();
        }
    }

    /**
     * 箭头模式
     */
    private SideModeType mSideModeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_abstract);
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

        //两侧箭头
        mRightArrowFl = findViewById(R.id.right_arrow_fl);
        mRightArrowTv = findViewById(R.id.right_arrow_tv);
        mLeftArrowFl = findViewById(R.id.left_arrow_fl);
        mLeftArrowTv = findViewById(R.id.left_arrow_tv);

        //初始化
        subClassInitSetting();
        if (mSideModeType.isDefaultMode() || mSideModeType.isHideMode()) {
            mRightArrowFl.setVisibility(View.INVISIBLE);
            mLeftArrowFl.setVisibility(View.INVISIBLE);
        }
        //初始化底部按钮
        initBottomButton();
        //ViewPager的滑动监听
        setViewpagerScrollListener();
        //底部按钮的点击监听事件
        setBottomButtonClickListener();
        //两侧箭头的显示和监听
        twoSidesArrowStatus();

    }

    /**
     * 初始化底部按钮
     */
    private void initBottomButton() {
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
    }

    /**
     * 给子类初始化一些父类中的参数
     */
    protected abstract void subClassInitSetting();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int[] rightLocationScreen = new int[2];
            mRightArrowFl.getLocationOnScreen(rightLocationScreen);
            mRightRect = new Rect(rightLocationScreen[0], rightLocationScreen[1],
                    rightLocationScreen[0] + mRightArrowFl.getWidth(),
                    rightLocationScreen[1] + mRightArrowFl.getHeight());

            mLeftArrowFl.getLocationOnScreen(rightLocationScreen);
            mLeftRect = new Rect(rightLocationScreen[0], rightLocationScreen[1],
                    rightLocationScreen[0] + mLeftArrowFl.getWidth(),
                    rightLocationScreen[1] + mLeftArrowFl.getHeight());
            Log.d("TAG", "mRightRect:" + mRightRect);
            Log.d("TAG", "mLeftRect:" + mLeftRect);
        }
    }

    /**
     * 两侧箭头的显示和监听
     */
    private void twoSidesArrowStatus() {
        //减一
        mLeftArrowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSurviveTime = 0;
                if (mCurrentIndex > FLAG_TAG) {
                    mCurrentIndex--;
                } else {
                    mCurrentIndex = 0;
                }
                refreshFragmentAndBottomButton();
            }
        });
        //加一
        mRightArrowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSurviveTime = 0;
                if (mCurrentIndex < (fragmentList.size() - 1)) {
                    mCurrentIndex++;
                } else {
                    mCurrentIndex = fragmentList.size() - 1;
                }
                refreshFragmentAndBottomButton();
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float rawX = ev.getRawX();
        float rawY = ev.getRawY();
        if ((mLeftRect.contains((int) rawX, (int) rawY) || mRightRect.contains((int) rawX, (int) rawY)) && (null == mSideModeType || mSideModeType.isDefaultMode())) {
            mRightArrowFl.setVisibility(View.VISIBLE);
            mLeftArrowFl.setVisibility(View.VISIBLE);
            if (null == mThread) {
                mThread = new FrameLayoutStatusListenerThread();
                mThread.start();
                mViewSurviveTime = 0;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 刷新UI
     */
    private void refreshFragmentAndBottomButton() {
        if (mCurrentIndex != mContainerViewPager.getCurrentItem()) {
            mContainerViewPager.setCurrentItem(mCurrentIndex, true);
            resetButtonStatus(mCurrentIndex);
        }
    }

    /**
     * 底部按钮的点击监听事件
     */
    private void setBottomButtonClickListener() {
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
                    mCurrentIndex = finalI;
                }
            });
        }
    }

    /**
     * ViewPager的滑动监听
     */
    private void setViewpagerScrollListener() {
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
                mCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i("TAG", state + "");
                /**
                 * 0  就是停止  1  手指压在屏幕上  2  滑动 手指离开屏幕
                 */
            }
        });
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


    class FrameLayoutStatusListenerThread extends Thread {

        @Override
        public void run() {
            Log.d("TAG", "=======");
            while (mViewSurviveTime < 5) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("ERROR", e.toString());
                }
                Log.d("TAG", "=======" + mViewSurviveTime);
                mViewSurviveTime++;
            }

            doSomeThingOnMainThread();

        }
    }

    /**
     * 在主线程处理一些事情
     */
    private void doSomeThingOnMainThread() {
        if (getMainLooper().getThread().getId() != Thread.currentThread().getId()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invisibleView();
                }
            });
        } else {
            invisibleView();
        }
    }

    /**
     * 隐藏两侧的FrameLayout
     */
    private void invisibleView() {
        mRightArrowFl.setVisibility(View.INVISIBLE);
        mLeftArrowFl.setVisibility(View.INVISIBLE);
        mThread = null;
    }

    /**
     * 设置左右箭头的显示模式
     *
     * @param mSideModeType
     */
    public void setSideModeType(SideModeType mSideModeType) {
        this.mSideModeType = mSideModeType;
    }
}
