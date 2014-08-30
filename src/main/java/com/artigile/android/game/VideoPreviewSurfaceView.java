package com.artigile.android.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * need this custom preview so we can override this preview size since we don't don't need this
 * preview be visible.
 * @author ivanbahdanau
 */
public class VideoPreviewSurfaceView extends SurfaceView{


    public VideoPreviewSurfaceView(Context context) {
        super(context);
    }

    public VideoPreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPreviewSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(1, 1);

    }
}
