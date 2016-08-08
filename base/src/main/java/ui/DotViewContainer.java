package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shark0 on 2016/6/6.
 */
public class DotViewContainer extends LinearLayout {

    private List<ImageView> dotViewList = new ArrayList<>();

    private int selectImageResource;
    private int unSelectImageResource;

    public DotViewContainer(Context context) {
        super(context);
    }

    public DotViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DotViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initDotView(int size, int selectImageResource, int unSelectImageResource, int spacePixel) {
        setOrientation(LinearLayout.HORIZONTAL);
        this.selectImageResource = selectImageResource;
        this.unSelectImageResource = unSelectImageResource;
        setGravity(Gravity.CENTER);
        removeAllViews();
        dotViewList.clear();
        for(int i = 0; i < size; i ++) {
            ImageView dotView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int margin = spacePixel / 2;
            layoutParams.setMargins(margin, margin, margin, margin);
            dotView.setLayoutParams(layoutParams);
            dotView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dotView.setImageResource(i == 0? selectImageResource: unSelectImageResource);
            addView(dotView);
            dotViewList.add(dotView);
        }
    }

    public void setSelectionPosition(int position) {
        for(ImageView view: dotViewList) {
            int index = dotViewList.indexOf(view);
            view.setImageResource(index == position? selectImageResource: unSelectImageResource);
        }
    }
}
