package com.android.worktimemanager.Class;

import android.view.MotionEvent;
import android.view.View;

public abstract class OnSwipeListener implements View.OnTouchListener
{
    final int MOVE_HAND = 200;
    float startX,startY;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            startX = motionEvent.getRawX();
            startY = motionEvent.getRawY();
        }
        else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            float deltaX = startX - motionEvent.getRawX();
            float deltaY = startY - motionEvent.getRawY();
            if(Math.abs(deltaX)>Math.abs(deltaY))
            {
                if(deltaX > MOVE_HAND)
                    rightSwipe();
                else if(deltaX < -MOVE_HAND)
                    leftSwipe();
                updateState();
            }
        }
        return true;
    }
    protected abstract void updateState();
    protected abstract void leftSwipe();
    protected abstract void rightSwipe();

}