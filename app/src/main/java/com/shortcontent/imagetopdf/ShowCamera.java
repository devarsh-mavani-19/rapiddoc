package com.shortcontent.imagetopdf;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceHolder holder;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedSizes;

    Camera.Size mPreviewSize;
    Camera.Size mSize;
    public ShowCamera(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder= getHolder();
        holder.addCallback(this);
        mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        mSupportedSizes = camera.getParameters().getSupportedPictureSizes();

    }
    public ShowCamera(Context context) {
        super(context);
    }

    public ShowCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShowCamera(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            mSize = getOptimalPreviewSize(mSupportedSizes, width, height);

        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("surface changed");
        Camera.Parameters params = camera.getParameters();
//        mSupportedPreviewSizes = params.getSupportedPreviewSizes();
//        List<Camera.Size> sizes = params.getSupportedPictureSizes();
//        Camera.Size size = sizes.get(0);
//        for(int i=0;i<sizes.size();i++)
//        {
//            if(sizes.get(i).width > size.width)
//                size = sizes.get(i);
//        }


        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE) {
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }
        else {
            params.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }

        params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        params.setPictureSize(mSize.width, mSize.height);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}
