package com.example.q.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by YQ on 2016/10/12.
 */

public class Indicator extends LinearLayout {
    private Paint mPaint;
    private Path path;
    private int mTriangleWidth;//三角形indicator的宽度
    private int mTriangleHeight;//三角形的高度
    private static final float RAD_TRIANGLE_WIDTH = 1 / 6f;//六分之一
    private  final int DIMENSION_TRIANGLE_WIDTH_MAX= (int) (getScreenWidth()/3*RAD_TRIANGLE_WIDTH);//indicator的最大宽度(屏幕得三分之一，的六分一直，及18分之一)
    private int mInitTranslationX;//初始的indicator偏移量
    private int mTranslationX;//每次indicator的偏移量
    private int mTabVisibleCount;
    private static final int COUNT_DEAFAULT_COUNT = 4;
    private List<String> titles;
    private ViewPager viewPager;//设置关联的viewpager
    private OnPageChangedListener lisenter;

    public void setLisenter(OnPageChangedListener lisenter) {
        this.lisenter = lisenter;
    }

    //用户调用这个接口而不用调用viewpager的监听
    public interface OnPageChangedListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    //占用的外部的接口，所以内部要自建接口，否则用户无法自定义其他内容
    public void setViewPager(ViewPager viewPager, int position) {
        this.viewPager = viewPager;
        //参数position是当前position,positionOffset(0<-->1)
        //目标position=当前position +/- positionOffset(左加右减)
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //右移，position=当前位置,左移，position=目标位置
                //右移,offset:0->1,左移offset:1->0,
                //tabWidth*positionOffset
                scroll(position, positionOffset);
                if (lisenter != null) {
                    lisenter.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                highLightTextView(position);
                if (lisenter != null) {
                    lisenter.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (lisenter != null) {
                    lisenter.onPageScrollStateChanged(state);
                }
            }
        });
        viewPager.setCurrentItem(position);
        highLightTextView(position);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取可见Tab的数量;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Indicator);
        mTabVisibleCount = array.getInt(R.styleable.Indicator_visible_tab_count, COUNT_DEAFAULT_COUNT);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEAFAULT_COUNT;
        }
        array.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));//圆角
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());//画布向右下移动
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount * RAD_TRIANGLE_WIDTH);//平分屏幕宽度后的6/1,如果只有一个item，那么这个宽度会很大，所以需要设置一个最大值
        mTriangleWidth=Math.min(mTriangleWidth,DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
        mTriangleHeight = mTriangleWidth / 2;
        //初始化三角形
        initTriangle();
    }

    private void initTriangle() {
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(mTriangleWidth, 0);//(0,0)->
        path.lineTo(mTriangleWidth / 2, -mTriangleHeight);//(0,0)->
        path.close();
    }

    public void scroll(int position, float positionOffset) {
//        Log.e("TAG","  position:  "+position+"  offset:  "+positionOffset);
        //indicator移动量
        int tabWidth = getWidth() / mTabVisibleCount;//tab的宽度
        mTranslationX = (int) (tabWidth * (positionOffset + position));
        //容器移动，在Tab处于移动至最后一个时
        Log.e("TAG", "1");
        if ((position >= mTabVisibleCount - 2) && positionOffset > 0 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                Log.e("TAG", "2");
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * positionOffset), 0);
            } else {
                Log.e("TAG", "3");
                this.scrollTo(position * tabWidth + (int) (tabWidth * positionOffset), 0);
            }
        }
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0) {
            return;
        }
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;//设置每一个item的宽度，这里只显示设定的个数
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    //得到屏幕宽度
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            this.titles = titles;
            for (String title : this.titles) {
                addView(generateTextView(title));
            }
        }
        setItemClickEvent();
    }

    private View generateTextView(String title) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(lp);
        return textView;
    }

    //设置可见Tab数量,在setTabTitles之前调用
    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
    }

    //高亮某个Tab文本
    private void highLightTextView(int position) {
        resetTextViewColor();
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(Color.RED);
        }
    }

    //重置Tab文本颜色
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
            }
        }
    }

    //设置Tab点击事件
    private void setItemClickEvent(){
        int cCount=getChildCount();
        for (int i=0;i<cCount;i++){
            final View view=getChildAt(i);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(finalI);
                }
            });
        }
    }
}
