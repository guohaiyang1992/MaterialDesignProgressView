package com.guohaiyang.materialdesignprogressviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

//import com.guohaiyang.materialdesignprogressviewlib.R;

/**
 * Created by guohaiyang on 2017/3/29.
 * 原理超简单的进度条:material design 风格
 * 支持功能：
 * 自定义颜色
 * 自定义宽度
 * 自定义速度，最快是15 目前有三档 slow,medium,quick
 * 可设置开始运行或者暂停
 * 自定义半径
 * 自定义开始位置默认为270度
 */

public class MaterialDesignProgressView extends View {
    //config
    private int cicleColor = Color.BLACK;
    private int radius = 30;  //单位是px
    private int progress;
    private int speed = 5;//默认速度
    private int beiginProgress = 270;//开始角度
    private boolean isAdd = true;
    private boolean isRunning = true;
    private float lineWidth = 10f;
    private boolean isExit = false;

    //draw
    private Paint mPaint;
    private RectF rect;


    public MaterialDesignProgressView(Context context) {
        this(context, null);
    }

    public MaterialDesignProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialDesignProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUserConfig(attrs, defStyleAttr);
        initDrawConfig();
        rxUpdate();
    }

    private void initUserConfig(@Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialDesignProgressView, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = ta.getIndex(i);//因为将其作为lib引入的时候，此时的R文件不是final的了，不能作为swith 关键字，所以此处使用if
            if (attr == R.styleable.MaterialDesignProgressView_circle_beigin_degree) {
                beiginProgress = ta.getInt(attr, beiginProgress);

            } else if (attr == R.styleable.MaterialDesignProgressView_circle_color) {
                cicleColor = ta.getColor(attr, cicleColor);

            } else if (attr == R.styleable.MaterialDesignProgressView_circle_line_width) {
                lineWidth = Math.min(20, ta.getDimensionPixelSize(attr, 10));

            } else if (attr == R.styleable.MaterialDesignProgressView_circle_radius) {
                radius = ta.getDimensionPixelSize(attr, radius);

            } else if (attr == R.styleable.MaterialDesignProgressView_circle_speed) {
                speed = Math.min(15, ta.getInt(attr, speed));

            } else if (attr == R.styleable.MaterialDesignProgressView_circle_running) {
                isRunning = ta.getBoolean(attr, isRunning);

            }
        }


        ta.recycle();


    }


    private void initDrawConfig() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        //让链接圆滑
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(cicleColor);
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setBeiginProgress(int beiginProgress) { //设置开始相当于重置
        this.beiginProgress = beiginProgress;
        progress = 0;
        isAdd = true;
    }

    public int getBeiginProgress() {
        return beiginProgress;
    }

    public void setCicleColor(int cicleColor) {
        this.cicleColor = cicleColor;
        mPaint.setColor(cicleColor);
    }

    public int getCicleColor() {
        return cicleColor;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = Math.min(20, lineWidth);//最大不超过20px
        mPaint.setStrokeWidth(this.lineWidth);
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setSpeed(int speed) {
        this.speed = Math.min(15, speed);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    private void rxUpdate() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isExit) {
                    if (isRunning) {//开关
                        if (progress < 280 && isAdd) {
                            progress += speed / 2;//此处缩小约等于前端减速
                            if (progress >= 280) {
                                isAdd = false;
                            }
                            beiginProgress += speed;
                        } else {
                            progress -= speed / 3; //此处缩小约等于前端加速
                            if (progress <= 5) {
                                progress = 5;
                                isAdd = true;
                            }
                            beiginProgress += speed * 1.5;
                        }

                        beiginProgress = caculationProgress(beiginProgress);
                        try {
                            sleep(30);//更新间隙
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        postInvalidate();
                    }
                }


            }
        }.start();
    }

    int caculationProgress(int progress) {
        if (progress > 360) {
            progress -= 360;
        }
        return progress;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rect = new RectF();
        rect.left = getHeight() / 2 - radius;
        rect.right = getHeight() / 2 + radius;
        rect.top = getHeight() / 2 - radius;
        rect.bottom = getHeight() / 2 + radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int heightMeasureSpec) {
        int type = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (type == MeasureSpec.EXACTLY) {//固定值或者match_parent
            return size;
        } else {//wrap_content
            int height = (int) (getPaddingTop() + getPaddingBottom() + lineWidth * 2 + radius * 2);
            return height;
        }
    }


    private int measureWidth(int widthMeasureSpec) {
        int type = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (type == MeasureSpec.EXACTLY) {//固定值或者match_parent
            return size;
        } else {//wrap_content
            int width = (int) (getPaddingLeft() + getPaddingRight() + lineWidth * 2 + radius * 2);
            return width;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, beiginProgress, progress, false, mPaint);
    }


    //使用完成后比如销毁activity的时候，请回调此处，销毁使用的资源
    public void clearUsingRes() {
        isExit = true; //用于退出
        isRunning = false;//用暂停和开始
    }

}
