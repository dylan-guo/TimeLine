package com.example.timeline2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TimeLineView extends View {
    private Paint mPaint;
    public static int num;
    /**
     * 状态文本
     */
    private List<String> mPointTxt;

    //完成时间文本
    private List<String> mTimeTxt;
    /**
     * 步数
     */
    private float mStep = 1;
    /**
     * 圆形x坐标组
     */
    private int[] mXpoints;
    private float[] fXpoints;
    private int mPreLineColor;
    private int mStartedLineColor;

    private int mStartedCircleColor;
    private int mUnderwayCircleColor;
    private int mPreCircleColor;

    private int mStartedStringColor;
    private int mUnderwayStringColor;
    private int mPreStringColor;

    private int mRadius = 10;
    private float mTextSize = 20;
    private float mLineWidth = 5;
    private OnStepChangedListener mOnStepChangedListener;
    private Builder mBuilder;

    public TimeLineView(Context paramContext) {
        this(paramContext, null);
    }

    public TimeLineView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public TimeLineView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
        initAttrs(paramAttributeSet);
    }

    private void initAttrs(AttributeSet paramAttributeSet) {
        if (paramAttributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.TimeLineView);
            mStartedLineColor = typedArray.getColor(R.styleable.TimeLineView_startedLineColor, ContextCompat.getColor(getContext(), R.color.colorTheme));
            mPreLineColor = typedArray.getColor(R.styleable.TimeLineView_preLineColor, Color.GRAY);

            mStartedCircleColor = typedArray.getColor(R.styleable.TimeLineView_startedCircleColor, ContextCompat.getColor(getContext(), R.color.colorTheme));
            mUnderwayCircleColor = typedArray.getColor(R.styleable.TimeLineView_underwayCircleColor, ContextCompat.getColor(getContext(), R.color.colorTheme));
            mPreCircleColor = typedArray.getColor(R.styleable.TimeLineView_preCircleColor, Color.GRAY);

            mStartedStringColor = typedArray.getColor(R.styleable.TimeLineView_startedStringColor, ContextCompat.getColor(getContext(), R.color.colorTheme));
            mUnderwayStringColor = typedArray.getColor(R.styleable.TimeLineView_underwayStringColor, ContextCompat.getColor(getContext(), R.color.colorTheme));
            mPreStringColor = typedArray.getColor(R.styleable.TimeLineView_preStringColor, Color.GRAY);
            mTextSize = typedArray.getDimension(R.styleable.TimeLineView_textSize, 20);
            mRadius = (int) typedArray.getDimension(R.styleable.TimeLineView_tlradius, 10);
            mLineWidth = typedArray.getDimension(R.styleable.TimeLineView_lineWidth, 5);
            typedArray.recycle();
        }
    }


    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mStartedLineColor = Color.BLUE;
        this.mPreLineColor = Color.GRAY;
        this.mStartedCircleColor = mStartedLineColor;
        this.mUnderwayCircleColor = mStartedCircleColor;
        this.mPreCircleColor = mPreLineColor;
        this.mStartedStringColor = mStartedLineColor;
        this.mUnderwayStringColor = mStartedStringColor;
        this.mPreStringColor = mPreLineColor;
        this.mPointTxt = new ArrayList();
        this.mPointTxt.add("step 1");
        this.mPointTxt.add("step 2");
        this.mPointTxt.add("step 3");
        this.mBuilder = new Builder();
    }

    public Builder builder() {
        return mBuilder;
    }

    public void setPointStrings(@NonNull List<String> pointStringList, @FloatRange(from = 1.0) float step, @NonNull List<String> mTimeTxt) {
        if (pointStringList == null || pointStringList.isEmpty()) {
            this.mTimeTxt.clear();
            mPointTxt.clear();
            mStep = 0;
        } else {
            this.mTimeTxt = new ArrayList<>(mTimeTxt);
            mPointTxt = new ArrayList(pointStringList);
            mStep = Math.min(step, mPointTxt.size());
        }
        invalidate();
    }

    public void setStep(float step) {
        num = 0;
        this.mStep = Math.min(step, this.mPointTxt.size());
        invalidate();
    }

    public boolean nextStep() {
        num = 0;
        if (mStep + 1 > mPointTxt.size()) {
            return false;
        } else {
            mStep++;
            invalidate();
            return true;
        }
    }

    public float getStep() {
        return mStep;
    }

    public void setOnStepChangedListener(OnStepChangedListener listener) {
        this.mOnStepChangedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initCalc();
        drawCircle(canvas, 1, this.mPointTxt.get(0), this.mXpoints[0]);
        if (mOnStepChangedListener != null) {
            mOnStepChangedListener.onchanged(this, (int) (this.mStep - 0.5), this.mPointTxt.get((int) (mStep - 1.5)));
        }
        boolean needContinue = false;
        //循环画出时间线
        for (int i = 1; i < mPointTxt.size(); i++) {
            if (this.fXpoints[i] != 0 && this.mStep != 1) {
          //     Log.d("ccc", "绘制未完成的的线条 ");
                drawLine(canvas, true, this.mXpoints[(i - 1)], this.fXpoints[i]+10);
                if (i == mPointTxt.size() - 1) {
                    drawLine(canvas, false, (int) this.fXpoints[i] - mRadius * 2, this.mXpoints[i]);
                }
                needContinue = true;
            } else {
                if (needContinue) {
                    needContinue = false;
                 //  Log.d("ccc", "绘制没到达线条");
                    drawLine(canvas, this.mStep > i, (int) this.fXpoints[i - 1] - mRadius * 3, this.mXpoints[i]);
                } else {
                 //   Log.d("绘制完成线条", "1");
                    drawLine(canvas, this.mStep > i, this.mXpoints[(i - 1)], this.mXpoints[i]);
                }
            }
            drawCircle(canvas, i + 1, this.mPointTxt.get(i), this.mXpoints[i]);
        }
    }

    private void initCalc() {
        int len = this.mPointTxt.size();
        this.mXpoints = new int[len];
        this.fXpoints = new float[len];
        if (len > 1) {
            int strlen = (int) (getWordCount(this.mPointTxt.get(0)) * this.mTextSize);
            this.mXpoints[0] = Math.max(strlen, this.mRadius);
            this.fXpoints[0] = Math.max(strlen, this.mRadius);
            strlen = (int) (getWordCount(this.mPointTxt.get(len - 1)) * this.mTextSize);
            this.mXpoints[len - 1] = getWidth() - Math.max(strlen, this.mRadius);
            int dx = (this.mXpoints[len - 1] - this.mXpoints[0]) / (len - 1);
            float offset = this.mStep % 1;
            if (this.mStep - offset == len - 1 && this.mStep != len - 1) {
                this.fXpoints[len - 1] = (int) (mXpoints[0] + dx * (len - 2) + offset * dx);
            }
            for (int i = 1; i < this.mXpoints.length - 1; i++) {
                if (i == (int) (this.mStep - offset) && mStep > i) {
                    this.fXpoints[i] = (int) (mXpoints[0] + dx * (i - 1) + offset * dx);
                } else {
                    this.fXpoints[i] = 0;
                }
                this.mXpoints[i] = mXpoints[0] + dx * i;
            }
        }
    }

    private float getWordCount(String s) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length / 4.0f;
    }

    private void drawCircle(Canvas canvas, int drawStep, String text, int dx) {
        int textSize = mPointTxt.size();
        num = num + 1;
        this.mPaint.setColor(this.mStep == drawStep ? mUnderwayCircleColor : this.mStep > drawStep ? mStartedCircleColor : mPreCircleColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeWidth(2.0F);
        canvas.drawCircle(dx, getHeight() - this.mRadius - 1 - 70, this.mRadius, this.mPaint);
        this.mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(dx, getHeight() - this.mRadius - 1 - 70, this.mRadius - 5, this.mPaint);
        this.mPaint.setColor(this.mStep == drawStep ? mUnderwayStringColor : this.mStep > drawStep ? mStartedStringColor : mPreStringColor);
        this.mPaint.setTextSize(this.mTextSize);
//        Log.d("dd", "drawCircle: " + num);
        if (num == textSize || num == 1) {
            canvas.drawText(text, dx - getWordCount(text) * this.mTextSize, getHeight() - this.mRadius * 2, this.mPaint);
        } else {
            canvas.drawText(text, dx - getWordCount(text) * this.mTextSize, getHeight() - this.mRadius * 2 - 100, this.mPaint);
        }
    }

    private void drawLine(Canvas paramCanvas, boolean isStart, int startX, float endX) {
        this.mPaint.setColor(isStart ? mStartedLineColor : mPreLineColor);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setStrokeWidth(mLineWidth);
        //求出完成的线条 画上完成时
        if (num != Math.floor(mStep) && isStart) {
            paramCanvas.drawText(mTimeTxt.get(num - 1), endX - getWordCount(mTimeTxt.get(num - 1)) * this.mTextSize - 150, getHeight() - this.mRadius * 2 - 10, this.mPaint);
        }
        //画线
        paramCanvas.drawLine(this.mRadius * 1.2F + startX, getHeight() - this.mRadius - 1 - 70, endX - this.mRadius * 1.2F, getHeight() - this.mRadius - 1 - 70, this.mPaint);
    }

    public interface OnStepChangedListener {
        void onchanged(TimeLineView view, int step, String stepStr);
    }

    public class Builder {

        private Builder() {
        }

        /**
         * 状态文本
         */
        public Builder pointStrings(@NonNull List<String> pointStringList, @IntRange(from = 1) int step) {
            if (pointStringList == null || pointStringList.isEmpty()) {
                mPointTxt.clear();
                mStep = 0;
            } else {
                mPointTxt = new ArrayList(pointStringList);
                mStep = Math.min(step, mPointTxt.size());
            }
            return this;
        }

        /**
         * 状态文本
         */
        public Builder pointStrings(@NonNull String[] pointStringList, @IntRange(from = 1) int step) {
            if (pointStringList == null) {
                mPointTxt.clear();
                mStep = 0;
                return this;
            } else {
                return pointStrings(new ArrayList<>(Arrays.asList(pointStringList)), step);
            }
        }

        /**
         * 文本大小
         */
        public Builder textSize(float px) {
            mTextSize = px;
            return this;
        }

        /**
         * 未开始状态线条颜色
         */
        public Builder preLineColor(@ColorInt int preLineColor) {
            mPreLineColor = preLineColor;
            return this;
        }

        /**
         * 已进行状态线条颜色
         */
        public Builder startedLineColor(@ColorInt int startedLineColor) {
            mStartedLineColor = startedLineColor;
            return this;
        }


        /**
         * 未开始状态圆颜色
         */
        public Builder preCircleColor(@ColorInt int preCircleColor) {
            mPreCircleColor = preCircleColor;
            return this;
        }


        /**
         * 进行中状态圆颜色
         */
        public Builder underwayCircleColor(@ColorInt int underwayCircleColor) {
            mUnderwayCircleColor = underwayCircleColor;
            return this;
        }

        /**
         * 已进行状态圆颜色
         */
        public Builder startedCircleColor(@ColorInt int startedCircleColor) {
            mStartedCircleColor = startedCircleColor;
            return this;
        }


        /**
         * 未开始状态文本颜色
         */
        public Builder preStringColor(@ColorInt int preStringColor) {
            mPreStringColor = preStringColor;
            return this;
        }


        /**
         * 进行中状态文本颜色
         */
        public Builder underwayStringColor(@ColorInt int underwayStringColor) {
            mUnderwayStringColor = underwayStringColor;
            return this;
        }

        /**
         * 已进行状态文本颜色
         */
        public Builder startedStringColor(@ColorInt int startedStringColor) {
            mStartedStringColor = startedStringColor;
            return this;
        }

        /**
         * 圆半径
         */
        public Builder radius(int px) {
            mRadius = px;
            return this;
        }

        /**
         * 线条宽度
         */
        public Builder lineWidth(float lineWidth) {
            mLineWidth = lineWidth;
            return this;
        }

        /**
         * 重新绘制
         */
        public void load() {
            invalidate();
        }
    }
}

