package com.example.expensemanager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.expensemanager.R;

/**
 * Custom View vẽ vòng tròn tiến độ bằng Canvas chuẩn Mockup Productive:
 * - Track màu xám xanh
 * - Cung tiến độ màu cam hổ phách bo tròn đầu (Paint.Cap.ROUND)
 * - Hiển thị số phần trăm (%) ở chính giữa
 */
public class CircularProgressView extends View {

    private Paint trackPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF arcBounds;

    private int progress = 80; // Giá trị phần trăm (0 - 100)
    private float strokeWidth = 14f;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        strokeWidth = getResources().getDisplayMetrics().density * 5.5f;

        // Cọ vẽ đường track nền
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeWidth);
        trackPaint.setColor(ContextCompat.getColor(getContext(), R.color.petrol_track));

        // Cọ vẽ cung tiến độ màu cam
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.amber_orange));

        // Cọ vẽ chữ phần trăm
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(getResources().getDisplayMetrics().density * 13f);
        textPaint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        arcBounds = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float pad = strokeWidth / 2f + 4f;
        arcBounds.set(pad, pad, w - pad, h - pad);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. Vẽ vòng tròn nền
        canvas.drawOval(arcBounds, trackPaint);

        // 2. Vẽ cung tiến độ theo %
        float sweepAngle = (progress / 100f) * 360f;
        canvas.drawArc(arcBounds, -90f, sweepAngle, false, progressPaint);

        // 3. Vẽ chữ % ở giữa
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float yOffset = (fm.descent + fm.ascent) / 2f;
        canvas.drawText(progress + "%", arcBounds.centerX(), arcBounds.centerY() - yOffset, textPaint);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        invalidate();
    }

    public int getProgress() {
        return progress;
    }
}

