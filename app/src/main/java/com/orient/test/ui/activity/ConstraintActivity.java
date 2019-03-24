package com.orient.test.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.constraint.Group;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.orient.test.R;
import com.orient.test.utils.UiUtils;

public class ConstraintActivity extends AppCompatActivity {

    private FloatingActionButton mAdd;
    private FloatingActionButton mLike;
    private FloatingActionButton mWrite;
    private FloatingActionButton mTop;
    private Group likeGroup;
    private Group writeGroup;
    private Group topGroup;
    // 动画集合，用来控制动画的有序播放
    private AnimatorSet animatorSet;
    // 圆的半径
    private int radius;
    // FloatingActionButton宽度和高度，宽高一样
    private int width;

    public static void show(Context context) {
        Intent intent = new Intent(context, ConstraintActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint);

        initWidget();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 动态获取FloatingActionButton的宽
        mAdd.post(new Runnable() {
            @Override
            public void run() {
                width = mAdd.getMeasuredWidth();
            }
        });
        // 在xml文件里设置的半径
        radius = UiUtils.dp2px(this, 80);
    }

    private void initWidget() {
        mAdd = findViewById(R.id.fab_add);
        mLike = findViewById(R.id.fab_like);
        mTop = findViewById(R.id.fab_top);
        mWrite = findViewById(R.id.fab_write);
        likeGroup = findViewById(R.id.gp_like);
        writeGroup = findViewById(R.id.gp_write);
        topGroup = findViewById(R.id.gp_top);
        // 将三个弹出的FloatingActionButton隐藏
        setViewVisible(false);
    }

    private void setViewVisible(boolean isShow) {
        likeGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
        writeGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
        topGroup.setVisibility(isShow?View.VISIBLE:View.GONE);
    }

    private void initListener() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放动画的时候不可以点击
                if(animatorSet != null && animatorSet.isRunning())
                    return;

                // 判断播放显示还是隐藏动画
                if(likeGroup.getVisibility() != View.VISIBLE) {
                    animatorSet = new AnimatorSet();
                    ValueAnimator likeAnimator = getValueAnimator(mLike, false, likeGroup,0);
                    ValueAnimator writeAnimator = getValueAnimator(mWrite, false, writeGroup,45);
                    ValueAnimator topAnimator = getValueAnimator(mTop, false, topGroup,90);
                    animatorSet.playSequentially(likeAnimator, writeAnimator, topAnimator);
                    animatorSet.start();
                }else {
                    animatorSet = new AnimatorSet();
                    ValueAnimator likeAnimator = getValueAnimator(mLike, true, likeGroup,0);
                    ValueAnimator writeAnimator = getValueAnimator(mWrite, true, writeGroup,45);
                    ValueAnimator topAnimator = getValueAnimator(mTop, true, topGroup,90);
                    animatorSet.playSequentially(topAnimator, writeAnimator, likeAnimator);
                    animatorSet.start();
                }

            }
        });
    }

    /**
     * 获取ValueAnimator
     *
     * @param button FloatingActionButton
     * @param reverse 开始还是隐藏
     * @param group Group
     * @param angle angle 转动的角度
     * @return ValueAnimator
     */
    private ValueAnimator getValueAnimator(final FloatingActionButton button, final boolean reverse, final Group group, final int angle) {
        ValueAnimator animator;
        if (reverse)
            animator = ValueAnimator.ofFloat(1, 0);
        else
            animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) button.getLayoutParams();
                params.circleRadius = (int) (radius * v);
                params.circleAngle = 270f + angle * v;
                params.width = (int) (width * v);
                params.height = (int) (width * v);
                button.setLayoutParams(params);
            }
        });
        animator.addListener(new SimpleAnimation() {
            @Override
            public void onAnimationStart(Animator animation) {
                group.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(group == likeGroup && reverse){
                    setViewVisible(false);
                }
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    abstract class SimpleAnimation implements Animator.AnimatorListener{
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
