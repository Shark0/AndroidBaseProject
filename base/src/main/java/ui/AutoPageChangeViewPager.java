package ui;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by Shark on 2016/5/23.
 */
public class AutoPageChangeViewPager extends ViewPager implements Runnable, OnTouchListener {

    private Handler handler;
    private long durationTime;

    public AutoPageChangeViewPager(Context context) {
        super(context);
    }

    public AutoPageChangeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideAutoScrollAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    public void run() {
        int selection = getCurrentItem();
        int count = getAdapter().getCount();
        selection = (selection + 1) % count;
        setCurrentItem(selection, true);
        handler.postDelayed(this, durationTime);
    }

    public void showAutoScrollAnimation(long durationTime) {
        if(handler == null) {
            handler = new Handler();
        }
        handler.removeCallbacks(this);
        this.durationTime = durationTime;
        handler.postDelayed(this, durationTime);
        setOnTouchListener(this);
    }

    private void hideAutoScrollAnimation() {
        if(handler != null) {
            handler.removeCallbacks(this);
            setOnTouchListener(null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideAutoScrollAnimation();
        return false;
    }
}
