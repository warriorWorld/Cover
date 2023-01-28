package com.harbinger.cover.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.harbinger.cover.R;
import com.harbinger.cover.config.ShareKeys;
import com.harbinger.cover.utils.CommonUtil;
import com.harbinger.cover.utils.SharedPreferencesUtils;
import com.harbinger.cover.utils.VibratorUtil;


/**
 * Created by acorn on 2022/10/31.
 */
public class CoverView extends View {
    private final String TAG = "CoverView";
    private Context context;
    private WindowManager wm;
    private Point windowSize = new Point();
    private Point originalSize;
    private View layout;
    private DragView cover;
    private WindowManager.LayoutParams params;
    private Handler mH = new Handler(Looper.getMainLooper());
    private OnCoverViewClickListener onCoverClickListener;
    //    //drag
    private float downX;
    private float downY;
    private int lastMotion, lastLeft, lastRight, lastTop, lastBottom, screenWidth, screenHeight;
    private float xDistance, yDistance;
    private final int clickThreshold = 1;
    private boolean dragMode = true;

    public CoverView(Context context, View layout, OnCoverViewClickListener onCoverClickListener) {
        super(context);
        this.context = context;
        this.onCoverClickListener = onCoverClickListener;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        wm.getDefaultDisplay().getSize(windowSize);
        originalSize = new Point(windowSize.x,
                CommonUtil.dip2px(context, 50));
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
//                if (null != onCoverClickListener) {
//                    onCoverClickListener.onCoverClick();
//                }
                toggleDragMode();
            }
        });
        cover.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (cover.isDraging()&&dragMode){
//                    return false;
//                }
//                toggleDragMode();
                return true;
            }
        });
    }

    private void init() {
        createBodyView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    public void destroy() {
        if (null != wm) {
            wm.removeView(layout);
            destroyDrawingCache();
            clearAnimation();
            clearFocus();
        }
    }

    //    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        if (changed) {
//            if ((lastMotion == MotionEvent.ACTION_UP || lastMotion == MotionEvent.ACTION_CANCEL)) {
//                this.layout(lastLeft, lastTop, lastRight, lastBottom);
//                return;
//            }
//            lastLeft = left;
//            lastTop = top;
//            lastRight = right;
//            lastBottom = bottom;
//        }
////        Logger.d(TAG+":"+changed+","+left+","+top+","+right+","+bottom);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
////        Logger.d(TAG+":onTouchEvent");
//        super.onTouchEvent(event);
//        if (this.isEnabled()) {
//            lastMotion = event.getAction();
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    VibratorUtil.Vibrate(context, 30);
//                    downX = event.getX();
//                    downY = event.getY();
//                    xDistance = 0;
//                    yDistance = 0;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    xDistance = event.getX() - downX;
//                    yDistance = event.getY() - downY;
//                    Log.d(TAG, "xDistance:" + xDistance + "      yDistance:" + yDistance);
//                    if (xDistance != 0 && yDistance != 0) {
//                        int l = (int) (getLeft() + xDistance);
//                        int r = (int) (getRight() + xDistance);
//                        int t = (int) (getTop() + yDistance);
//                        int b = (int) (getBottom() + yDistance);
//                        if (l < 0) {
//                            l = 0;
//                            r = (int) (getRight() - getLeft());
//                        }
//                        if (r > screenWidth) {
//                            r = screenWidth;
//                            l = (int) (getLeft() - getRight() + screenWidth);
//                        }
//                        if (t < 0) {
//                            t = 0;
//                            b = (int) (getBottom() - getTop());
//                        }
//                        if (b > screenHeight) {
//                            b = screenHeight;
//                            t = (int) (getTop() - getBottom() + screenHeight);
//                        }
//                        this.layout(l, t, r, b);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if (Math.abs(xDistance) <= clickThreshold && Math.abs(yDistance) <= clickThreshold) {
//                        performClick();
//                    }
//                    setPressed(false);
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    setPressed(false);
//                    break;
//            }
//            return true;
//        }
//        return false;
//    }
    private void toggleDragMode() {
        if (dragMode) {
            params.width = windowSize.x;
            params.height = windowSize.y;
            wm.updateViewLayout(layout, params);
        } else {
            params.width = originalSize.x;
            params.height = originalSize.y;
            wm.updateViewLayout(layout, params);
            toPosition();
        }
        dragMode = !dragMode;
    }

    private void toPosition() {
        int bottom = SharedPreferencesUtils.getIntSharedPreferencesData(context, ShareKeys.LAST_DRAGVIEW_BOTTOM);

        params.y = bottom;
        wm.updateViewLayout(layout, params);
    }

    public void toggleOrientation() {
        params.width = originalSize.x;
        params.height = originalSize.y;

        wm.updateViewLayout(layout, params);
    }

    private void createBodyView() {
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.RGBA_8888; // 设置图片
        // 格式，效果为背景透明
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        params.width = windowSize.x;
        params.height = originalSize.y;
//        params.width = windowSize.x;
//        params.height = windowSize.y;
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
