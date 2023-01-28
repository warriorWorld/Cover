package com.harbinger.cover.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.harbinger.cover.R;


/**
 * Created by acorn on 2022/10/31.
 */
public class CoverView extends View {
    private final String TAG = "CoverView";
    private Context context;
    private WindowManager wm;
    private Point windowSize = new Point();
    private Point originalSize;
    private View layout, cover;
    private WindowManager.LayoutParams params;
    private Handler mH = new Handler(Looper.getMainLooper());
    private OnCoverViewClickListener onCoverClickListener;

    public CoverView(Context context, View layout, OnCoverViewClickListener onCoverClickListener) {
        super(context);
        this.context = context;
        this.onCoverClickListener = onCoverClickListener;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        wm.getDefaultDisplay().getSize(windowSize);
        this.layout = layout;
        initUI();
        mH.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 500);
    }

    private void initUI() {
        cover = layout.findViewById(R.id.cover);
        cover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCoverClickListener) {
                    onCoverClickListener.onCoverClick();
                }
            }
        });
        cover.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onCoverClickListener) {
                    onCoverClickListener.onCoverLongClick();
                }
                return true;
            }
        });
    }

    private void init() {
        createBodyView();
    }


    public void destroy() {
        if (null != wm) {
            wm.removeView(layout);
            destroyDrawingCache();
            clearAnimation();
            clearFocus();
        }
    }

    private void createBodyView() {
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.RGBA_8888; // 设置图片
        // 格式，效果为背景透明
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        params.width = windowSize.x;
        params.height = windowSize.y;
        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.x = 0;
        params.y = 0;
        layout.setVisibility(VISIBLE);
        wm.addView(layout, params);
//        isAdded = true;
    }

    public interface OnCoverViewClickListener {
        void onCoverClick();

        void onCoverLongClick();
    }
}
