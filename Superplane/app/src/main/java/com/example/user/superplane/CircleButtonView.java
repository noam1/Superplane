package com.example.user.superplane;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom button view that looks like a circle.
 * It supports canvas animations and activates when it is pressed.
 * It can also be deactivated.
 */
public class CircleButtonView extends View {
    private final double SECOND = 1000000000.0;

    private final float LAYOUT_WIDTH_RATIO = 0.70f;
    private final float OUTER_CIRCLE_RADIUS_RATIO = 0.75f;
    private final float INNER_CIRCLE_RATIO = 0.90f;

    private final double ANIMATION_DURATION_SECONDS = 0.25;
    private final double IMAGE_ROTATION_PERIOD_TIME = 1.5;

    private boolean isActivated = false;
    private long activationBeginTime;

    private Paint paint;
    private Drawable aircraftDrawable;

    private int outerCircleColor;
    private int innerCircleColor;
    private int centerImageColor;

    private int outerCircleActivatedColor;
    private int innerCircleActivatedColor;
    private int centerImageActivatedColor;

    private int rotatingImageColor;

    private CircleButtonStatusListener statusListener;

    public CircleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(clickListener);

        paint = new Paint();
        aircraftDrawable = getResources().getDrawable(R.drawable.airplane_vector, null);

        outerCircleColor = getResources().getColor(R.color.colorPrimary);
        innerCircleColor = getResources().getColor(R.color.colorPrimaryLight);
        centerImageColor = getResources().getColor(R.color.colorAccent);

        outerCircleActivatedColor = getResources().getColor(R.color.colorAccentLight);
        innerCircleActivatedColor = getResources().getColor(R.color.colorAccent);
        centerImageActivatedColor = getResources().getColor(R.color.colorAccentDark);

        rotatingImageColor = getResources().getColor(R.color.colorAccent);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isActivated)
            {
                activate();

                if (statusListener != null)
                    statusListener.buttonActivated();
            }
            else
            {
                deactivate();

                if (statusListener != null)
                    statusListener.buttonDeactivated();
            }
        }
    };

    /**
     * Setter for the status listener.
     * @param listener The listener.
     */
    public void setStatusListener(CircleButtonStatusListener listener)
    {
        this.statusListener = listener;
    }

    /**
     * Activates the animation of the button.
     */
    public void activate() {
        if (isActivated)
            return;

        isActivated = true;
        activationBeginTime = System.nanoTime();

        invalidate();
    }

    /**
     * Deactivates the animation of the button.
     */
    public void deactivate() {
        if (!isActivated)
            return;

        isActivated = false;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = ((View) (getParent())).getWidth();
        int size = (int) (parentWidth * LAYOUT_WIDTH_RATIO);

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isActivated) {
            long curTime = System.nanoTime();
            double deltaSeconds = (double) (curTime - activationBeginTime) / SECOND;
            double animationRatio = deltaSeconds / ANIMATION_DURATION_SECONDS;
            double angleRatio = deltaSeconds / IMAGE_ROTATION_PERIOD_TIME;

            float angle = (float)(2 * Math.PI * angleRatio);

            if (animationRatio < 1.0) {
                int outerColor = interpolateColor(outerCircleColor, outerCircleActivatedColor, animationRatio);
                int innerColor = interpolateColor(innerCircleColor, innerCircleActivatedColor, animationRatio);
                int imageColor = interpolateColor(centerImageColor, centerImageActivatedColor, animationRatio);

                drawButton(outerColor, innerColor, imageColor, canvas);

                int alpha = (int)(255.0 * animationRatio);

                drawRotatingImage(rotatingImageColor, alpha, angle, canvas);
            }
            else
            {
                drawButton(outerCircleActivatedColor, innerCircleActivatedColor, centerImageActivatedColor, canvas);
                drawRotatingImage(rotatingImageColor, 255, angle, canvas);
            }

            invalidate();
        } else {
            drawButton(outerCircleColor, innerCircleColor, centerImageColor, canvas);
        }
    }

    /**
     * Draws the circle of the button including the airplane image with the specified colors and canvas.
     * @param outerCircleColor The outer color of the circle.
     * @param innerCircleColor The inner color of the circle.
     * @param imageColor The color of the airplane image.
     * @param canvas The canvas with which to draw.
     */
    private void drawButton(int outerCircleColor, int innerCircleColor, int imageColor, Canvas canvas) {
        int radius = (int)((getWidth() * OUTER_CIRCLE_RADIUS_RATIO) / 2.0);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        paint.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setColor(innerCircleColor);
        canvas.drawCircle(centerX, centerX, radius * INNER_CIRCLE_RATIO, paint);

        int imageSize = (int) (Math.sqrt(2) * radius);

        int imageLeft = centerX - (imageSize / 2);
        int imageRight = centerX + (imageSize / 2);
        int imageTop = centerY - (imageSize / 2);
        int imageBottom = centerY + (imageSize / 2);

        aircraftDrawable.setBounds(imageLeft, imageTop, imageRight, imageBottom);
        aircraftDrawable.setTint(imageColor);
        aircraftDrawable.setAlpha(255);
        aircraftDrawable.draw(canvas);
    }

    /**
     * Draw the rotating airplane image.
     * @param imageColor The airplane image color.
     * @param alpha The opacity value of the image.
     * @param angle The angle with relation to 'up' to draw the image from.
     * @param canvas The canvas with which to draw.
     */
    private void drawRotatingImage(int imageColor, int alpha, float angle, Canvas canvas)
    {
        int radius = (int)((getWidth() * OUTER_CIRCLE_RADIUS_RATIO) / 2.0);

        int imageSize = (int)((getWidth() * (1 - OUTER_CIRCLE_RADIUS_RATIO)) / 2.0);
        int imageDistance = radius + (imageSize / 2);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int imageCenterX = centerX + (int)(imageDistance * Math.cos(angle));
        int imageCenterY = centerY - (int)(imageDistance * Math.sin(angle));

        int imageLeft = imageCenterX - (imageSize / 2);
        int imageRight = imageCenterX + (imageSize / 2);
        int imageTop = imageCenterY - (imageSize / 2);
        int imageBottom = imageCenterY + (imageSize / 2);

        canvas.rotate(-(float) Math.toDegrees(angle), imageCenterX, imageCenterY);

        aircraftDrawable.setBounds(imageLeft, imageTop, imageRight, imageBottom);
        aircraftDrawable.setTint(imageColor);
        aircraftDrawable.setAlpha(alpha);
        aircraftDrawable.draw(canvas);

        canvas.restore();
    }

    /**
     * Linearly interpolates two hex colors with a given linear ratio.
     * @param colorBegin The first color.
     * @param colorEnd The second color.
     * @param ratio The linear ratio with which to interpolate.
     * @return Returns the interpolated hex-color.
     */
    private int interpolateColor(int colorBegin, int colorEnd, double ratio)
    {
        int beginA = colorBegin >> 24;
        int beginR = (colorBegin >> 16) & 0xff;
        int beginG = (colorBegin >> 8) & 0xff;
        int beginB = colorBegin & 0xff;

        int endA = colorEnd >> 24;
        int endR = (colorEnd >> 16) & 0xff;
        int endG = (colorEnd >> 8) & 0xff;
        int endB = colorEnd & 0xff;

        int resultA = beginA + (int)((endA - beginA) * ratio);
        int resultR = beginR + (int)((endR - beginR) * ratio);
        int resultG = beginG + (int)((endG - beginG) * ratio);
        int resultB = beginB + (int)((endB - beginB) * ratio);

        return resultB | (resultG << 8) | (resultR << 16) | (resultA << 24);
    }
}
