package com.artigile.android.game;

import android.view.SurfaceHolder;

/**
 * @author ivanbahdanau
 */
public class SurfaceHolderContainer {

    private static SurfaceHolder surfaceHolder;

    public static SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public static void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        SurfaceHolderContainer.surfaceHolder = surfaceHolder;
    }
}
