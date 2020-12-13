package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatEditText;

public class AliPayEditText extends AppCompatEditText {
    // 画方框的画笔
    private Paint boxPaint;
    // 画密码的画笔（小圆点）
    private Paint passwdPaint;
    // 画文字的画笔
    private Paint textPaint;
    // 未输入文字时的方框线宽
    private int normalLineWidth;
    // 未输入文字时的方框颜色
    private int normalLineColor;
    // 输入文字时的方框线宽
    private int editLineWidth;
    // 输入文字时的方框颜色
    private int editLineColor;
    // 密码（小圆点）颜色
    private int passwdColor;
    // 密码（小圆点）半径
    private int passwdRadius;
    // 输入的文字大小
    private int textSize;
    // 输入的文字颜色
    private int textColor;
    // 输入最大长度
    private int maxLength;
    // 方框间距
    private int margin;
    // 震动时间
    private int vibratePeriod;
    // 震动幅度
    private int vibrateX;
    // 震动重复次数
    private int repeatCount;
    // 文字是否可见，为false时是密码模式，方框里显示的是小圆点
    private boolean textVisibility = true;
    // 文字是否加粗
    private boolean fakeBoldText = true;
    private int width;
    private int height;
    private String currentText = "";
    private Context context;

    private ObjectAnimator vibrateAnimator;
    private int currentRepeatCount;
    private boolean isAnimationShowing;

    // 输入回调
    private OnInputListener listener;

    public AliPayEditText(Context context) {
        this(context, null);
    }

    public AliPayEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public AliPayEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化方法
     * 初始化参数和默认设置
     *
     * @param context
     */
    private void init(Context context) {
        this.context = context;
        normalLineWidth = dp2px(1);
        normalLineColor = Color.parseColor("#dddddd");
        editLineWidth = dp2px(2);
        editLineColor = Color.parseColor("#000000");
        passwdRadius = dp2px(5);
        passwdColor = Color.parseColor("#000000");
        textSize = sp2px(16);
        textColor = Color.parseColor("#000000");
        maxLength = 6;
        margin = dp2px(10);
        repeatCount = 1;
        currentRepeatCount = 1;
        vibrateX = dp2px(10);
        vibratePeriod = 80;
        setFocusable(true);
        setBackgroundDrawable(null);
        setCursorVisible(false);
        setSingleLine();
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        setTextColor(Color.parseColor("#00000000"));
        setHintTextColor(Color.parseColor("#00000000"));

        boxPaint = new Paint();
        boxPaint.setColor(normalLineColor);
        boxPaint.setStrokeWidth(normalLineWidth);
        passwdPaint = new Paint();
        passwdPaint.setColor(passwdColor);
        passwdPaint.setStrokeWidth(passwdRadius);
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setFakeBoldText(fakeBoldText);
    }
    /**
     * 很重要
     * 在布局读取完毕时重新计算输入框的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = (width - (maxLength - 1) * margin - Math.max(normalLineWidth, editLineWidth)) / maxLength;
        getLayoutParams().height = height + Math.max(normalLineWidth, editLineWidth);
    }

    /**
     * 初始化和每次输入文字时重绘视图
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBox(canvas);
        drawText(canvas);
    }

    /**
     * 绘制方框
     *
     * @param canvas
     */
    private void drawBox(Canvas canvas) {
        // 循环绘制方框，个数与文字最大输入数maxLength相同
        for (int i = 0; i < maxLength; i++) {
            boxPaint.setColor(i < currentText.length() ? editLineColor : normalLineColor);
            boxPaint.setStrokeWidth(i < currentText.length() ? editLineWidth : normalLineWidth);

            // 绘制方框左边的线条
            canvas.drawLine(i * (height + margin) + (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, 0, i * (height + margin) + (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, height, boxPaint);
            // 绘制方框右边的线条
            canvas.drawLine(height + i * (height + margin) - (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, 0, height + i * (height + margin) - (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, height, boxPaint);
            // 绘制方框上边的线条
            canvas.drawLine(i * (height + margin), (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, height + i * (height + margin), (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, boxPaint);
            // 绘制方框下边的线条
            canvas.drawLine(i * (height + margin), height - (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, height + i * (height + margin), height - (i < currentText.length() ? editLineWidth : normalLineWidth) / 2, boxPaint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        for (int i = 0; i < currentText.length(); i++) {
            if (textVisibility) {
                // 非密码模式画文字
                float deltaX = textPaint.measureText(String.valueOf(currentText.charAt(i))) / 2;
                canvas.drawText(String.valueOf(currentText.charAt(i)), i * (height + margin) + height / 2 - deltaX, height / 2 + deltaX, textPaint);
            } else {
                // 密码文字画小圆点
                canvas.drawCircle(i * (height + margin) + height / 2, height / 2, passwdRadius, passwdPaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        currentText = text.toString();
        invalidate();
        if (currentText.length() == maxLength) {
            if (!isAnimationShowing && listener != null) {
                listener.onFinish(currentText);
            }
        } else {
            if (!isAnimationShowing && listener != null) {
                listener.onInput(currentText, currentText.length());
            }
        }
    }

    /**
     * 震动
     * 在一些情况（比如密码输入错误）时让整个输入框震动
     */
    public void vibrate() {
        currentRepeatCount = 0;
        isAnimationShowing = true;
        vibrateAnimator = ObjectAnimator.ofFloat(this, "TranslationX", 0, -vibrateX, 0, vibrateX, 0);
        vibrateAnimator.setDuration(vibratePeriod);
        vibrateAnimator.setInterpolator(new LinearInterpolator());
        vibrateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (currentRepeatCount < repeatCount) {
                    vibrateAnimator.start();
                    currentRepeatCount++;
                } else {
                    isAnimationShowing = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimationShowing = false;
            }
        });

        vibrateAnimator.start();
    }

    /**
     * 设置未输入文字时方框线宽
     *
     * @param normalLineWidth 线宽值，单位dp
     */
    public void setNormalLineWidth(int normalLineWidth) {
        this.normalLineWidth = dp2px(normalLineWidth);
        invalidate();
    }

    /**
     * 设置未输入文字时方框颜色
     *
     * @param normalLineColor 颜色值
     */
    public void setNormalLineColor(int normalLineColor) {
        this.normalLineColor = normalLineColor;
        invalidate();
    }

    /**
     * 设置输入文字时方框线宽
     *
     * @param editLineWidth 线宽值，单位dp
     */
    public void setEditLineWidth(int editLineWidth) {
        this.editLineWidth = dp2px(editLineWidth);
        invalidate();
    }

    /**
     * 设置输入文字时方框颜色
     *
     * @param editLineColor 颜色值
     */
    public void setEditLineColor(int editLineColor) {
        this.editLineColor = editLineColor;
        invalidate();
    }

    /**
     * 设置密码（小圆点）颜色
     *
     * @param passwdColor 颜色值
     */
    public void setPasswdColor(int passwdColor) {
        this.passwdColor = passwdColor;
        passwdPaint.setColor(passwdColor);
        invalidate();
    }

    /**
     * 设置密码（小圆点）半径
     *
     * @param passwdRadius 半径值，单位dp
     */
    public void setPasswdRadius(int passwdRadius) {
        this.passwdRadius = dp2px(passwdRadius);
        passwdPaint.setStrokeWidth(passwdRadius);
        invalidate();
    }

    /**
     * 设置文字大小
     * 之所以方法名加个Draw是因为setTextSize在TextView里有同名方法
     *
     * @param textSize 大小值，单位sp
     */
    public void setDrawTextSize(int textSize) {
        this.textSize = sp2px(textSize);
        textPaint.setTextSize(this.textSize);
        invalidate();
    }

    /**
     * 设置文字颜色
     * 之所以方法名加个Draw是因为setTextColor在TextView里有同名方法
     *
     * @param textColor 颜色值
     */
    public void setDrawTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }

    /**
     * 设置文字最大长度
     * 如果在布局文件中设置了maxLength属性，务必再设置一遍此方法，否则方框数默认为6个
     *
     * @param maxLength 长度值，整数
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        requestLayout();
    }

    /**
     * 设置方框间距
     *
     * @param margin 间距值，单位dp
     */
    public void setMargin(int margin) {
        this.margin = dp2px(margin);
        requestLayout();
    }

    /**
     * 设置震动时间
     *
     * @param vibratePeriod 震动时间，单位毫秒
     */
    public void setVibratePeriod(int vibratePeriod) {
        this.vibratePeriod = vibratePeriod;
    }

    /**
     * 设置文字是否可见
     * 设置为false时为密码模式，输入显示小圆点
     *
     * @param textVisibility
     */
    public void setTextVisibility(boolean textVisibility) {
        this.textVisibility = textVisibility;
        invalidate();
    }

    /**
     * 设置文字是否加粗，密码模式下无效
     *
     * @param fakeBoldText
     */
    public void setFakeBoldText(boolean fakeBoldText) {
        this.fakeBoldText = fakeBoldText;
        textPaint.setFakeBoldText(fakeBoldText);
    }

    /**
     * 设置震动幅度，即震动偏离原始位置的最大距离
     *
     * @param vibrateX 震动幅度，单位dp
     */
    public void setVibrateX(int vibrateX) {
        this.vibrateX = dp2px(vibrateX);
    }

    /**
     * 设置震动重复次数
     * 左右摆动一个来回视为重复一次
     *
     * @param repeatCount 重复次数，整数
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * 输入回调接口
     */
    public interface OnInputListener {
        /**
         * 输入事件发生且长度未达到最大长度时回调
         *
         * @param text          当前文字
         * @param currentLength 当前文字长度
         */
        void onInput(String text, int currentLength);

        /**
         * 输入长度达到最大长度时回调
         *
         * @param text 当前文字
         */
        void onFinish(String text);
    }

    /**
     * 设置输入完成时的回调接口
     *
     * @param listener
     */
    public void setOnInputListener(OnInputListener listener) {
        this.listener = listener;
    }

    /**
     * 重置输入框
     * 调用后文字清空
     */
    public void reset() {
        setText("");
        invalidate();
    }

    private int dp2px(float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
