package egoo.customui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhaoweiliang on 17/2/6.
 */
public class GradientSpinningRoundView extends View {
    private int mWidth,mHeight;
    private int mRadius;
    private int mRotationAngle = -90;

    private int mStrokeWidth;
    private int[] mColors;
    private Paint mPaint;

    public GradientSpinningRoundView(Context context) {
        this(context,null);
    }

    public GradientSpinningRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.GradientSpinningRoundView);

        mStrokeWidth = ta.getDimensionPixelOffset(R.styleable.GradientSpinningRoundView_strokeWidth,4);
        int startColor = ta.getColor(R.styleable.GradientSpinningRoundView_circleStartColor,Color.GREEN);
        int endColor = ta.getColor(R.styleable.GradientSpinningRoundView_circleEndColor,Color.WHITE);

        mColors = new int[]{startColor,endColor};

        ta.recycle();

        init();
    }

    public GradientSpinningRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mRadius = mWidth / 2;
        mPaint.setShader(new SweepGradient(mRadius,mRadius,mColors, null));
    }

    private void init(){
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        ValueAnimator va = ValueAnimator.ofInt(-90,270);
        va.setDuration(5000);
        va.setInterpolator(new LinearInterpolator());
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.INFINITE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotationAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        va.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mRotationAngle, mRadius, mRadius);
        canvas.drawCircle(mRadius,mRadius, mRadius - mStrokeWidth / 2,mPaint);
    }
}
