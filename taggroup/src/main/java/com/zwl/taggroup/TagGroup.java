package com.zwl.taggroup;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ZWL on 2016/9/21.
 */
public class TagGroup extends ViewGroup {

    private Context mContext;

    private final int DEFAULT_SHOW_NUM = 8;
    private int mStartPosition = 0;
    private ArrayList<String> mTagList;

    private float mBorderStrokeWidth;
    private float mTextSize;
    private int mTagDefaultColor;
    private int mTagSelectedColor;
    private float mTagCornerRadius;
    private int mTagTextPadding;
    private float mVerticalSpacing;
    private float mHorizontalSpacing;

    private Drawable mTagSeletedDrawable;
    private Drawable mTagUnselectedDrawable;

    private TextView[] mTvPool;
    private ImageView mImgChangeTag;

    public TagGroup(Context context) {
        this(context,null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        Resources resources = context.getResources();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagGroupViviSDK);

        mBorderStrokeWidth = ta.getDimensionPixelOffset(R.styleable.TagGroupViviSDK_borderStrokeWidth,4);
        mTextSize = ta.getDimension(R.styleable.TagGroupViviSDK_textSize,32);
        mTagDefaultColor = ta.getColor(R.styleable.TagGroupViviSDK_tagDefaultColor,0xff515151);
        mTagSelectedColor = ta.getColor(R.styleable.TagGroupViviSDK_tagSelectedColor,0xff232935);
        mTagCornerRadius = ta.getDimensionPixelOffset(R.styleable.TagGroupViviSDK_tagCornerRadius,32);
        mTagTextPadding = ta.getDimensionPixelOffset(R.styleable.TagGroupViviSDK_tagTextPadding,26);
        mVerticalSpacing = ta.getDimensionPixelOffset(R.styleable.TagGroupViviSDK_verticalSpacing,24);
        mHorizontalSpacing = ta.getDimensionPixelOffset(R.styleable.TagGroupViviSDK_horizontalSpacing,48);

        ta.recycle();

        initTagView();

        mTagSeletedDrawable = resources.getDrawable(R.mipmap.tag_selected_vivisdk);
        mTagSeletedDrawable.setBounds(0,2,(int)mTextSize,(int)mTextSize);
        mTagUnselectedDrawable = resources.getDrawable(R.mipmap.tag_unselected_vivisdk);
        mTagUnselectedDrawable.setBounds(0,2,(int)mTextSize,(int)mTextSize);
    }

    private void initTagView() {
        mTvPool = new TextView[DEFAULT_SHOW_NUM];
        for (int i = 0; i < DEFAULT_SHOW_NUM; i++){
            mTvPool[i] = new TextView(mContext);

            mTvPool[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
            mTvPool[i].setTextColor(createColorStateList());
            mTvPool[i].setBackgroundDrawable(createStateListDrawable());

            mTvPool[i].setPadding(mTagTextPadding,mTagTextPadding - 4,mTagTextPadding,mTagTextPadding - 4);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            mTvPool[i].setOnTouchListener(mOnTagTouchListener);

            mTvPool[i].setLayoutParams(params);
        }

        mImgChangeTag = new ImageView(getContext());
        LayoutParams params = new LayoutParams((int)mTextSize + mTagTextPadding * 2,(int)mTextSize + mTagTextPadding * 2);
        mImgChangeTag.setLayoutParams(params);
        mImgChangeTag.setImageResource(R.mipmap.change_labels_vivisdk);
        mImgChangeTag.setOnClickListener(mOnTagChangeClickListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0;
        int rowWidth = 0;
        int rowMaxHeight = 0;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE){
                rowWidth += childWidth;
                //进入下一行
                if (rowWidth > widthSize){
                    if (++row > 1)
                        break;
                    rowWidth = childWidth;
                    height += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = childHeight;
                }else {
                    rowMaxHeight = Math.max(rowMaxHeight,childHeight);
                }
                rowWidth += mHorizontalSpacing;
            }
        }

        height += rowMaxHeight;
        height += getPaddingTop() + getPaddingBottom();

        //如果只有一行，让宽度包裹tags
        if (row == 0){
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        }else {
            width = widthSize;
        }

        setMeasuredDimension(widthSize,height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("onlayout");

        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;
        int row = 0;

        final int count = getChildCount();

        TextView lastView = null;
        int changeTagTop = 0;

        int i = 0;
        for (; i < count; i++){
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE){
                //进入下一行
                if ((childLeft + width) > parentRight){
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = height;
                    ++row;
                    if (row == 2){
                        break;
                    }
                }else {
                    rowMaxHeight = Math.max(rowMaxHeight,height);
                }

                child.layout(childLeft,childTop,childLeft + width,childTop + height);

                childLeft += width + mHorizontalSpacing;

                if (row < 2) {
                    if (child instanceof TextView)
                        lastView = (TextView) child;
                    else
                        lastView = null;
                    changeTagTop = childTop;
                }
            }
        }

        if (lastView != null){
            lastView.layout(-10000,0,0,0);
        }

        mImgChangeTag.layout(parentRight - ((int)mTextSize + mTagTextPadding * 2), changeTagTop, parentRight, parentBottom);
    }

    private ColorStateList createColorStateList(){
        int[] colors = new int[]{mTagSelectedColor, mTagSelectedColor, mTagDefaultColor};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{android.R.attr.state_selected};
        states[2] = new int[]{};

        return new ColorStateList(states,colors);
    }

    private StateListDrawable createStateListDrawable(){
        StateListDrawable tagSLD = new StateListDrawable();

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(mTagCornerRadius);
        gd.setStroke((int)mBorderStrokeWidth,mTagSelectedColor);
        tagSLD.addState(new int[]{android.R.attr.state_pressed},gd);

        gd = new GradientDrawable();
        gd.setCornerRadius(mTagCornerRadius);
        gd.setStroke((int)mBorderStrokeWidth,mTagSelectedColor);
        tagSLD.addState(new int[]{android.R.attr.state_selected},gd);

        gd = new GradientDrawable();
        gd.setCornerRadius(mTagCornerRadius);
        gd.setStroke((int)mBorderStrokeWidth,mTagDefaultColor);
        tagSLD.addState(new int[]{},gd);

        return tagSLD;
    }

    public void setTags(ArrayList<String> tagList){
        mTagList = tagList;

        changeTags();
    }

    private void changeTags(){
        removeAllViews();

        final int size = mTagList.size();
        int tvPosition = mStartPosition;
        for (int i = 0; i < DEFAULT_SHOW_NUM && i < size; i++){
            mTvPool[i].setText(mTagList.get(tvPosition));

            mTvPool[i].setCompoundDrawables(null,null,mTagUnselectedDrawable,null);

            addView(mTvPool[i]);

            mTvPool[i].setSelected(false);

            tvPosition++;

            if (tvPosition == size){
                tvPosition = 0;
            }
        }

        mStartPosition = tvPosition > size - 1 ? 0 : tvPosition;

        addView(mImgChangeTag);

        requestLayout();
    }

    public String getSelectedTags(){
        StringBuilder tags = new StringBuilder();
        for (TextView tv : mTvPool) {
            if (tv.isSelected()){
                if (tags.length() != 0)     tags.append(",");
                tags.append(tv.getText().toString());
            }
        }

        if (tags.length() == 0)
            return "";
        else
            return tags.toString();
    }

    private class OnTagTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView textView = (TextView)v;

            boolean isSelected = textView.isSelected();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN : {
                    textView.setSelected(!isSelected);
                    break;
                }
            }

            if (textView.isSelected()){
                textView.setCompoundDrawables(null,null,mTagSeletedDrawable,null);
            }else {
                textView.setCompoundDrawables(null,null,mTagUnselectedDrawable,null);
            }
            return true;
        }
    }

    private class OnTagChangeClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            changeTags();
        }
    }

    private OnTagChangeClickListener mOnTagChangeClickListener = new OnTagChangeClickListener();

    private OnTagTouchListener mOnTagTouchListener = new OnTagTouchListener();
}
