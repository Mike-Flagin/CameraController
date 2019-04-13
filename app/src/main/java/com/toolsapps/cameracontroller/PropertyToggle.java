package com.toolsapps.cameracontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class PropertyToggle extends ToggleButton {

    private Drawable drawable;

    public PropertyToggle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PropertyToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyToggle(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final Drawable d = drawable;
        if (d != null) {
            int h = d.getIntrinsicHeight();
            int w = d.getIntrinsicWidth();
            int y = (getHeight() - h) / 2;
            int x = (getWidth() - w) / 2;
            d.setBounds(x, y, x + w, y + h);
            d.draw(canvas);
        }
    }

    public void setButtonDrawable2(Drawable d) {
        this.drawable = d;
    }
}
