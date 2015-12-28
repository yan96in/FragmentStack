package com.mr.wang.frametest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * User: chengwangyong(chengwangyong@vcinema.com)
 * Date: 2015-12-06
 * Time: 20:25
 */
public class StackManager {
    private FragmentActivity context;
    private final FragmentStack stack;
    private long CLICK_SPACE=2000;
    private long currentTime;

    /**
     * 设置点击间隔时间 防止重复点击 默认2s
     * @param CLICK_SPACE 重复点击时间
     */
    public void setCLICK_SPACE(long CLICK_SPACE) {
        this.CLICK_SPACE = CLICK_SPACE;
    }

    public StackManager(FragmentActivity context) {
        this.context = context;
        stack = new FragmentStack();
    }

    /**
     * 设置底层的fragment
     */
    public void setFragment(Fragment mTargetFragment) {
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.framLayoutId, mTargetFragment, mTargetFragment.getClass().getName())
                .commit();
    }



    /**
     * 跳转到指定的fragment
     */
    private void popFragment(Fragment from, Fragment to) {
        if (System.currentTimeMillis()-currentTime>CLICK_SPACE){
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction
                    .setCustomAnimations(R.anim.next_in, R.anim.next_out, R.anim.pop_enter, R.anim.pop_exit)//必须在add、remove、replace调用之前被设置，否则不起作用。
                    .add(R.id.framLayoutId, to, to.getClass().getName())
                    .setCustomAnimations(R.anim.next_in, R.anim.next_out, R.anim.pop_enter, R.anim.pop_exit)
                    .hide(from)
                    .addToBackStack(to.getClass().getName())
                    .commit();
            currentTime=System.currentTimeMillis();
        }
    }

    public void popFragment(BaseFragment from, BaseFragment to, Bundle bundle,int stackMode){
        switch (stackMode){
            case FragmentStack.SINGLETOP:
                stack.putSingleTop(to);
                break;
            case FragmentStack.SINGLETASK:
                stack.putSingleTask(to);
                break;
            case FragmentStack.SINGLEINSTANCE:
                stack.putSingleInstance(to);
                break;
            default:
                stack.putStandard(to);
                break;
        }
        to.setArguments(bundle);
        popFragment(from, to);
    }



    /**
     * 跳转到指定的fragment 带参形式
     */
    public void popFragment(BaseFragment from, BaseFragment to, Bundle bundle){
        popFragment(from, to,bundle,FragmentStack.STANDARD);
    }


    public void popFragment(Fragment to) {
        FragmentTransaction transaction =  context.getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()) {
            transaction
                    .setCustomAnimations(R.anim.dialog_in, R.anim.dialog_out)
                    .add(R.id.framLayoutId, to, to.getClass().getName())
                    .addToBackStack(to.getClass().getName())
                    .commit();
        }
    }

    public void closeFragment(Fragment mTargetFragment) {
        FragmentTransaction transaction =  context.getSupportFragmentManager().beginTransaction();
        transaction.remove(mTargetFragment).commit();
    }

    public void closeFragment(String name){
        Fragment fragmentByTag =  context.getSupportFragmentManager().findFragmentByTag(name);
        if (fragmentByTag!=null){
            closeFragment(fragmentByTag);
            context.getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void close() {
        context.getSupportFragmentManager().popBackStack();
    }

    public void closeAllFragment() {
        int backStackCount =  context.getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            int backStackId =  context.getSupportFragmentManager().getBackStackEntryAt(i).getId();
            context.getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onBackPressed(){
        stack.onBackPressed();
    }
}