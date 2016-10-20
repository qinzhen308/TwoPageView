package com.paulz.towpageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.OverScroller;

/**
 * Created by paulz on 2016/9/21.
 */
public class TwoPageLayout extends ViewGroup {
    Context context;
    View pageOne;
    View pageTwo;
    private int mTouchSlop;
    private int mPageChangeLimit;
    private int currentPage;
    private boolean isIntercept;

    OnPageChangeListener mOnPageChangeListener;
    public OverScroller scroller;


    public TwoPageLayout(Context context) {
        super(context);
        init(context);
    }

    public TwoPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TwoPageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    private void init(Context context){
        this.context=context;
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop=vc.getScaledTouchSlop();
        //滑动翻页的临界值
        mPageChangeLimit=150;
        scroller=new OverScroller(getContext());
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=ev.getX();
                downY=ev.getY();
                isIntercept=touchChildToTop();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isIntercept)return false;
                if(Math.abs(downY-ev.getY())>mTouchSlop){
                    //滑动开始
                    if(currentPage==1&&downY-ev.getY()<0){
                        isScrolling=true;
                        return true;
                    }else if(currentPage==0&&downY-ev.getY()>0){
                        isScrolling=true;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isScrolling=false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isScrolling=false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(pageOne==null){
            pageOne=getChildAt(0);
        }
        if(pageTwo==null){
            pageTwo=getChildAt(1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (pageOne!=null&&pageOne.getVisibility() != GONE) {
                pageOne.layout(0+getPaddingLeft(), 0+getPaddingTop(), getWidth()-getPaddingRight(), getHeight()-getPaddingBottom());
            }
            if (pageTwo!=null&&pageTwo.getVisibility() != GONE) {
                pageTwo.layout(0+getPaddingLeft(), getHeight(), getWidth()-getPaddingRight(), getHeight()+getHeight()-getPaddingBottom());
            }
    }

    private float downX;
    private float downY;
    private boolean isScrolling;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=event.getX();
                downY=event.getY();
                isIntercept=touchChildToTop();
                break;
            case MotionEvent.ACTION_MOVE:
                if(currentPage==1&&downY-event.getY()<0){
                    isScrolling=true;
                }else if(currentPage==0&&downY-event.getY()>0){
                    isScrolling=true;
                }else {
                    isScrolling=false;
                }
                if(currentPage==1&&!isIntercept){
                    isScrolling=false;
                }
                if(isScrolling){
                    if(currentPage==0&&downY-event.getY()>0){//整体向上偏移,翻到下一页
                        scrollTo(0,(int)(downY-event.getY()));
                    }else if(currentPage==1&&downY-event.getY()<0){//翻到上一页
                        scrollTo(0,getHeight()+(int)(downY-event.getY()));
                    }else {
                        return false;
                    }
                }else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isScrolling){
                    if(currentPage==0){
                        if((downY-event.getY()>mPageChangeLimit)){
                            scroller.startScroll(0,getScrollY(),0,getHeight()-getScrollY(),500);
                            invalidate();
//                            scrollTo(0,getHeight());
                            currentPage=1;
                            if(mOnPageChangeListener!=null){
                                mOnPageChangeListener.onPageChanged(currentPage,pageTwo,true);
                            }
                        }else {
                            scroller.startScroll(0,getScrollY(),0,-getScrollY(),300);
                            invalidate();
//                            scrollTo(0,0);
                        }
                    }else {
                        if(Math.abs(downY-event.getY())>mPageChangeLimit){
                            scroller.startScroll(0,getScrollY(),0,-getScrollY(),500);
                            invalidate();
                            if(mOnPageChangeListener!=null){
                                mOnPageChangeListener.onPageChanged(currentPage,pageOne,true);
                            }
                            currentPage=0;
                        }else {
                            scroller.startScroll(0,getScrollY(),0,pageOne.getHeight()-getScrollY(),300);
                            invalidate();
//                            scrollTo(0,pageOne.getHeight());
                        }
                    }
                    isScrolling=false;
                }
                isIntercept=false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isScrolling=false;
                scrollTo(0,0);
                isIntercept=false;
                break;
        }
        return true;
    }

    private WebView childOne;
    private ListView childTwo;
    private int curTouchChild;

    public void setScrollerChildren(WebView childOne, ListView childTwo){
        this.childOne=childOne;
        this.childTwo=childTwo;
        curTouchChild=0;
    }

    public void changeCurScrollerChild(int i){
        curTouchChild=i;
    }


    //实现第二页中包含可滑动控件时是否滑到顶部
    //从而决定本控件是否处理touch事件。
    private boolean touchChildToTop(){
        if(curTouchChild==0&&childOne!=null&&childOne.getScrollY()<=0){
            return true;
        }else if(curTouchChild==1&&childTwo!=null&&childTwo.getScrollY()<=0&&childTwo.getFirstVisiblePosition()==0){
            return true;
        }else {
            return false;
        }
    }


    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            scrollTo(0,scroller.getCurrY());
            postInvalidate();
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
        mOnPageChangeListener=onPageChangeListener;

    }

    public interface OnPageChangeListener{
        public void onPageChanged(int page, View currentView,boolean bySelf);
    }


}
