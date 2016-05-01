package com.dreamworks.musicwanted.view;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class HomePlayView extends FrameLayout {

    private View leftLayout;
    private View rightLayout;
    private View homeLayout;

    private int leftLayoutWidth = 0;
    private int rightLayoutWidth = 0;
    private int homeLayoutWidth = 0;
    private int layoutBottom = 0;

    private ViewDragHelper viewDragHelper;
    private HomePlayViewCallBack callBack = new HomePlayViewCallBack();
    private HomePlayChildScrollListener homePlayChildScrollListener;
    private HomePlayScrollListener homePlayScrollListener;
    private HomePlayScrollStateChangedListener homePlayScrollStateChangedListener;

    public final static int STATE_SHOW = -100;
    public final static int STATE_DISMISSING = -200;

    /**
     * leftLayout显示时候的状态
     */
    public final static int STATE_LEFT = -301;
    /**
     * rightLayout显示时候的状态
     */
    public final static int STATE_RIGHT = -302;
    /**
     * leftLayout、rightLayout都不显示的时候的状态
     */
    public final static int STATE_HOME = -300;

    /**
     * HomePlayView整体只有三种状态，其三个子ViewGroup各有两个状态。
     * HomePlayView状态说明：STATE_LEFT，leftLayout正在显示
     * ，此时，homeLayout与rightLayout都是不显示的
     * ；STATE_RIGHT，rightLayout正在显示，此时，homeLayout与leftLayout都是不显示的
     * ；STATE_HOME，homeLayout正在显示，此时，leftLayout与rightLayout都是不显示的。
     */
    private int homePlayViewState = STATE_HOME;
    private int leftLayoutState = STATE_DISMISSING;
    private int rightLayoutState = STATE_DISMISSING;
    private int homeLayoutState = STATE_SHOW;

    public HomePlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHomePlayView(context, attrs, defStyle);
    }

    public HomePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHomePlayView(context, attrs, -1);
    }

    public HomePlayView(Context context) {
        super(context);
        initHomePlayView(context, null, -1);
    }

    private void initHomePlayView(Context context, AttributeSet attrs, int defStyle) {
        viewDragHelper = ViewDragHelper.create(this, callBack);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 保证homeLayout能被left与right覆盖
        homeLayout = getChildAt(0);
        leftLayout = getChildAt(1);
        rightLayout = getChildAt(2);
        if (homeLayout == null || leftLayout == null || rightLayout == null) {
            throw new NullPointerException();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        homeLayoutWidth = homeLayout.getMeasuredWidth();
        // System.out.println("homeLayoutWidth = " + homeLayoutWidth);
        rightLayoutWidth = rightLayout.getMeasuredWidth();
        // System.out.println("rightLayoutWidth = " + rightLayoutWidth);
        leftLayoutWidth = leftLayout.getMeasuredWidth();
        // System.out.println("leftLayoutWidth = " + leftLayoutWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutBottom = bottom;
        homeLayout.layout(0, 0, homeLayoutWidth, bottom);
        leftLayout.layout(-leftLayoutWidth, 0, 0, bottom);
        rightLayout.layout(homeLayoutWidth, 0, homeLayoutWidth + rightLayoutWidth, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float deltaX = moveX - downX;// x方向滑动的距离
                float deltaY = moveY - downY;// y方向滑动的距离
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // 滑动的方向偏向于水平方向,此时不应该让listview滑动(接收事件)
                    requestDisallowInterceptTouchEvent(true);
                }
                // 请求父view不拦截事件
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                this.performClick();
                break;
        }

        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    class HomePlayViewCallBack extends ViewDragHelper.Callback {

        private View scrollView = null;

        @Override
        public boolean tryCaptureView(View view, int arg1) {
            return view == leftLayout || view == rightLayout || view == homeLayout;
        }

        @Override
        public void onViewDragStateChanged(int state) {

            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:// 1
                    // 开始滑动
                    // System.out.println(" 开始滑动 " + " --- STATE_DRAGGING --- ");
                    break;
                case ViewDragHelper.STATE_IDLE:// 0
                    // 停止滑动
                    // System.out.println(" 停止滑动 " + " --- STATE_IDLE --- ");
                    break;
                case ViewDragHelper.STATE_SETTLING:// 2
                    // smooth
                    // System.out.println(" smooth " + " --- STATE_SETTLING --- ");
                    break;
                default:
                    break;
            }

            if (homePlayScrollStateChangedListener != null) {
                homePlayScrollStateChangedListener.onLayoutScrollStateChanged(scrollView, state);
            }
            super.onViewDragStateChanged(state);
        }

        private float offest = 0.0f;

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // super.onViewPositionChanged(changedView, left, top, dx, dy);
            View view = null;
            // 如果手指控制的是homeLayout，这个view是不移动的，一直在中间，底层。那么就判断，是左滑还是右滑，来让leftLayout与rightLayout来显示。
            if (changedView == homeLayout) {
                homeLayout.layout(0, 0, homeLayoutWidth, layoutBottom);
                // dx 是向左移为正，向右移为负
                if (dx > 0) {
                    if (rightLayout.getLeft() < homeLayoutWidth) {
                        offest = 1.0F - ((rightLayout.getLeft() * 1.0F + dx) / rightLayoutWidth);
                        rightLayout.layout(rightLayout.getLeft() + dx, 0, rightLayout.getRight() + dx, rightLayout.getBottom());
                        if (homePlayChildScrollListener != null) {
                            homePlayChildScrollListener.onRightLayoutScrollToRight(rightLayout, dx, getRealOffest(offest));
                        }
                        view = rightLayout;
                    } else {
                        offest = (leftLayout.getRight() * 1.0F + dx) / leftLayoutWidth;
                        leftLayout.layout(leftLayout.getLeft() + dx, 0, leftLayout.getRight() + dx, leftLayout.getBottom());
                        if (homePlayChildScrollListener != null) {
                            homePlayChildScrollListener.onLeftLayoutScrollToRight(leftLayout, dx, getRealOffest(offest));
                        }
                        view = leftLayout;
                    }

                } else if (dx < 0) {
                    if (leftLayout.getLeft() > -leftLayoutWidth) {
                        offest = (leftLayout.getRight() * 1.0F + dx) / leftLayoutWidth;
                        leftLayout.layout(leftLayout.getLeft() + dx, 0, leftLayout.getRight() + dx, leftLayout.getBottom());
                        if (homePlayChildScrollListener != null) {
                            homePlayChildScrollListener.onLeftLayoutScrollToLeft(leftLayout, dx, getRealOffest(offest));
                        }
                        view = leftLayout;
                    } else {
                        offest = 1.0F - ((rightLayout.getLeft() * 1.0F + dx) / rightLayoutWidth);
                        rightLayout.layout(rightLayout.getLeft() + dx, 0, rightLayout.getRight() + dx, rightLayout.getBottom());
                        if (homePlayChildScrollListener != null) {
                            homePlayChildScrollListener.onRightLayoutScrollToLeft(rightLayout, dx, getRealOffest(offest));
                        }
                        view = rightLayout;
                    }
                }

            } else if (changedView == leftLayout) {
                // 如果是leftLayout，因为本来就是要滑动的，已经在clampViewPositionHorizontal方法处理过了，那么就处理对应的事件回调
                view = changedView;
                offest = (leftLayout.getRight() * 1.0F) / leftLayoutWidth;
                if (homePlayChildScrollListener != null) {
                    if (dx < 0) {
                        homePlayChildScrollListener.onLeftLayoutScrollToLeft(leftLayout, dx, getRealOffest(offest));
                    } else if (dx > 0) {
                        homePlayChildScrollListener.onLeftLayoutScrollToRight(leftLayout, dx, getRealOffest(offest));
                    }
                }
            } else if (changedView == rightLayout) {
                // 如果是rightLayout，因为本来就是要滑动的，已经在clampViewPositionHorizontal方法处理过了，那么就处理对应的事件回调
                view = changedView;
                offest = 1.0F - (rightLayout.getLeft() * 1.0F / rightLayoutWidth);
                if (homePlayChildScrollListener != null) {
                    if (dx < 0) {
                        homePlayChildScrollListener.onRightLayoutScrollToLeft(rightLayout, dx, getRealOffest(offest));
                    } else if (dx > 0) {
                        homePlayChildScrollListener.onRightLayoutScrollToRight(rightLayout, dx, getRealOffest(offest));
                    }
                }
            }

            if (homePlayScrollListener != null) {
                homePlayScrollListener.onLayoutScroll(homeLayout, view, getRealOffest(offest));
            }

            // 不重绘一次，会出现闪动的情况，防止在边界处，闪动。
            // if (changedView == rightLayout) {
            // if (rightLayout.getLeft() <= 0) {
            // rightLayout.layout(0, 0, rightLayoutWidth,
            // rightLayout.getBottom());
            // }
            // if (rightLayout.getLeft() >= homeLayoutWidth) {
            // rightLayout.layout(homeLayoutWidth, 0, homeLayoutWidth +
            // rightLayoutWidth, rightLayout.getBottom());
            // }
            // } else if (changedView == leftLayout) {
            // if (leftLayout.getLeft() >= 0) {
            // leftLayout.layout(0, 0, leftLayoutWidth, leftLayout.getBottom());
            // }
            // if (leftLayout.getLeft() <= -leftLayoutWidth) {
            // leftLayout.layout(-leftLayoutWidth, 0, 0,
            // leftLayout.getBottom());
            // }
            // }

            if (changedView == rightLayout) {
                if (rightLayout.getLeft() <= -homeLayoutWidth / 3) {
                    rightLayout.layout(-homeLayoutWidth / 3, 0, rightLayoutWidth - homeLayoutWidth / 3, rightLayout.getBottom());
                }
                if (rightLayout.getLeft() >= homeLayoutWidth) {
                    rightLayout.layout(homeLayoutWidth, 0, homeLayoutWidth + rightLayoutWidth, rightLayout.getBottom());
                }
            } else if (changedView == leftLayout) {
                if (leftLayout.getLeft() >= leftLayoutWidth / 3) {
                    leftLayout.layout(leftLayoutWidth / 3, 0, leftLayoutWidth + leftLayoutWidth / 3, leftLayout.getBottom());
                }
                if (leftLayout.getLeft() <= -leftLayoutWidth) {
                    leftLayout.layout(-leftLayoutWidth, 0, 0, leftLayout.getBottom());
                }
            }

            scrollView = view;
        }

        /**
         * offest 是可能小于0，大于1的。
         *
         * @param offest 原始的offest
         * @return 格式化后的offest
         */
        private float getRealOffest(float offest) {
            if (offest > 1.0f) {
                offest = 1.0f;
            }
            if (offest < 0.0f) {
                offest = 0.0f;
            }
            return offest;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            // System.out.println("xvel = " + xvel);
            // 一共三种状态，根据不同状态处理松手的情况
            switch (homePlayViewState) {
                // 当leftLayout显示的时候，rightLayout是在右面的，homeLayout在leftLayout的下面
                // 这时，根据操作的方向，让leftLayout显示或消失就可以了
                case STATE_LEFT:
                    // System.out.println("homePlayViewState = STATE_LEFT");
                    if (xvel <= -500F || leftLayout.getLeft() < -leftLayoutWidth / 2) {
                        leftLayoutDismissing();
                    } else {
                        leftLayoutShow();
                    }
                    break;

                case STATE_RIGHT:
                    // System.out.println("homePlayViewState = STATE_RIGHT");
                    // 当rightLayout显示的时候，leftLayout是在左面的，homeLayout在leftLayout的下面
                    // 这时，根据操作的方向，让rightLayout显示或消失就可以了
                    if (xvel >= 500F || rightLayout.getLeft() > homeLayoutWidth / 2) {
                        rightLayoutDismissing();
                    } else {
                        rightLayoutShow();
                    }
                    break;

                case STATE_HOME:
                    // System.out.println("homePlayViewState = STATE_HOME");
                    // System.out.println("leftLayout.getLeft() = " +
                    // leftLayout.getLeft());
                    // System.out.println("rightLayout.getLeft() = " +
                    // rightLayout.getLeft());
                    // 当homeLayout显示的时候，leftLayout是在左面的，rightLayout是在右面的
                    // 这时，根据操作的方向，让leftLayout、rightLayout显示就可以了，需要注意的是，因为是利用homeLayout的触摸事件来控制leftLayout与rightLayout的显示
                    // 需要判断两个layout的相对位置，防止判断错误。
                    // 原因：如果先向左滑，rightLayout出现了，然后突然向右快速滑动并松手，那么就会误判打开leftLayout，因此每次显示layout的时候要判断，当前是否有layout已经在homeLayout上显示了
                    if ((xvel >= 500F || leftLayout.getLeft() > -leftLayoutWidth / 2) && rightLayout.getLeft() >= homeLayoutWidth) {
                        // System.out.println("leftLayoutShow()");
                        leftLayoutShow();
                    } else if ((xvel <= -500F || rightLayout.getLeft() < homeLayoutWidth / 2) && leftLayout.getLeft() <= -leftLayoutWidth) {
                        // System.out.println("rightLayoutShow()");
                        rightLayoutShow();
                    } else {

                        if (leftLayout.getLeft() > -leftLayoutWidth) {
                            // System.out.println("leftLayoutDismissing()");
                            leftLayoutDismissing();
                        } else if (rightLayout.getLeft() < homeLayoutWidth) {
                            // System.out.println("rightLayoutDismissing()");
                            rightLayoutDismissing();
                        }
                    }
                    break;
                default:
                    break;
            }

        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // if (child == rightLayout) {
            // if (left <= 0) {
            // left = 0;
            // }
            // if (left >= homeLayoutWidth) {
            // left = homeLayoutWidth;
            // }
            // } else if (child == leftLayout) {
            // if (left >= 0) {
            // left = 0;
            // }
            // if (left <= -leftLayoutWidth) {
            // left = -leftLayoutWidth;
            // }
            // }

            if (child == rightLayout) {
                if (left <= -homeLayoutWidth / 3) {
                    left = -homeLayoutWidth / 3;
                }
                if (left >= homeLayoutWidth) {
                    left = homeLayoutWidth;
                }
            } else if (child == leftLayout) {
                if (left >= leftLayoutWidth / 3) {
                    left = leftLayoutWidth / 3;
                }
                if (left <= -leftLayoutWidth) {
                    left = -leftLayoutWidth;
                }
            }

            return left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return homeLayoutWidth;
        }

    }

    /**
     * 显示leftLayout
     */
    public void leftLayoutShow() {
        viewDragHelper.smoothSlideViewTo(leftLayout, 0, 0);
        ViewCompat.postInvalidateOnAnimation(HomePlayView.this);
        leftLayoutState = STATE_SHOW;
        homeLayoutState = STATE_DISMISSING;
        homePlayViewState = STATE_LEFT;
    }

    /**
     * leftLayout消失
     */
    public void leftLayoutDismissing() {
        viewDragHelper.smoothSlideViewTo(leftLayout, homeLayout.getLeft() - leftLayoutWidth, 0);
        ViewCompat.postInvalidateOnAnimation(HomePlayView.this);
        leftLayoutState = STATE_DISMISSING;
        homeLayoutState = STATE_SHOW;
        homePlayViewState = STATE_HOME;
    }

    /**
     * 显示rightLayout
     */
    public void rightLayoutShow() {
        viewDragHelper.smoothSlideViewTo(rightLayout, 0, 0);
        ViewCompat.postInvalidateOnAnimation(HomePlayView.this);
        rightLayoutState = STATE_SHOW;
        homeLayoutState = STATE_DISMISSING;
        homePlayViewState = STATE_RIGHT;
    }

    /**
     * rightLayout消失
     */
    public void rightLayoutDismissing() {
        viewDragHelper.smoothSlideViewTo(rightLayout, homeLayout.getRight(), 0);
        ViewCompat.postInvalidateOnAnimation(HomePlayView.this);
        rightLayoutState = STATE_DISMISSING;
        homeLayoutState = STATE_SHOW;
        homePlayViewState = STATE_HOME;
    }

    @Override
    public void computeScroll() {
        // super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(HomePlayView.this);
        }
    }

    public int getHomePlayViewState() {
        return homePlayViewState;
    }

    public int getHomeLayoutState() {
        return homeLayoutState;
    }

    public int getLeftLayoutState() {
        return leftLayoutState;
    }

    public int getRightLayoutState() {
        return rightLayoutState;
    }

    public View getLeftLayout() {
        return leftLayout;
    }

    public View getRightLayout() {
        return rightLayout;
    }

    public View getHomeLayout() {
        return homeLayout;
    }

    public HomePlayChildScrollListener getOnHomePlayChildScrollListener() {
        return homePlayChildScrollListener;
    }

    public void setOnHomePlayChildScrollListener(HomePlayChildScrollListener listener) {
        this.homePlayChildScrollListener = listener;
    }

    public HomePlayScrollListener getHomePlayScrollListener() {
        return homePlayScrollListener;
    }

    public void setOnHomePlayScrollListener(HomePlayScrollListener homePlayScrollListener) {
        this.homePlayScrollListener = homePlayScrollListener;
    }

    public HomePlayScrollStateChangedListener getHomePlayScrollStateChangedListener() {
        return homePlayScrollStateChangedListener;
    }

    public void setOnHomePlayScrollStateChangedListener(HomePlayScrollStateChangedListener homePlayScrollStateChangedListener) {
        this.homePlayScrollStateChangedListener = homePlayScrollStateChangedListener;
    }

    /**
     * 监听HomePlayView子View的滑动情况
     *
     * @author yunhe
     */
    public interface HomePlayChildScrollListener {
        /**
         * 当leftLayout向左滑动的时候调用
         *
         * @param layout leftLayout
         * @param dx     位移量
         * @param offest 移动百分比，全部覆盖homeLayout为1，没有覆盖为0
         */
        void onLeftLayoutScrollToLeft(View layout, int dx, float offest);

        /**
         * 当leftLayout向右滑动的时候调用
         *
         * @param layout leftLayout
         * @param dx     位移量
         * @param offest 移动百分比，全部覆盖homeLayout为1，没有覆盖为0
         */
        void onLeftLayoutScrollToRight(View layout, int dx, float offest);

        /**
         * @param layout 当rightLayout向左滑动的时候调用
         * @param dx     位移量
         * @param offest 移动百分比，全部覆盖homeLayout为1，没有覆盖为0
         */
        void onRightLayoutScrollToLeft(View layout, int dx, float offest);

        /**
         * @param layout 当rightLayout向右滑动的时候调用
         * @param dx     位移量
         * @param offest 移动百分比，全部覆盖homeLayout为1，没有覆盖为0
         */
        void onRightLayoutScrollToRight(View layout, int dx, float offest);
    }

    /**
     * HomePlayView滑动状态变化监听
     *
     * @author yunhe
     */
    public interface HomePlayScrollStateChangedListener {
        /**
         * 当滑动状态变化时
         *
         * @param layout 滑动状态变化的layout
         * @param state  改变后的状态对应layout的状态
         */
        void onLayoutScrollStateChanged(View layout, int state);

    }

    /**
     * 监听HomePlayView滑动情况
     *
     * @author yunhe
     */
    public interface HomePlayScrollListener {
        /**
         * 当有layout滑动的时候
         *
         * @param homeLayout homeLayout
         * @param scrollView 正在滑动的view
         * @param offest     移动百分比，全部覆盖homeLayout为1，没有覆盖为0
         */
        void onLayoutScroll(View homeLayout, View scrollView, float offest);
    }

}
