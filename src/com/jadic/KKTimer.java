/**
 * @author Jadic
 * @created 2012-5-8 
 */
package com.jadic;

import java.util.Timer;
import java.util.TimerTask;

public class KKTimer extends TimerTask {
    private Timer timer = null;
    private IKKTimer kkTimer = null;
    private long delay = 0L;//定时器开始后执行任务延时时间
    private long period = 1000L;//定时器定时执行任务的间隔
    private boolean isTaskStarted = false;

    public KKTimer(IKKTimer kkTimer) {
    	super();
        this.kkTimer = kkTimer;
        this.timer = new Timer("KKTimer", false);
    }

    public void start() {
        this.timer.scheduleAtFixedRate(this, this.delay, this.period);
        this.isTaskStarted = true;
    }

    public void stop() {
        this.timer.cancel();
        this.isTaskStarted = false;
    }

    @Override
    public void run() {
        this.kkTimer.doOnTimer();
    }

    public boolean isIsTaskStarted() {
        return isTaskStarted;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public IKKTimer getKkTimer() {
        return kkTimer;
    }

    public void setKkTimer(IKKTimer kkTimer) {
        this.kkTimer = kkTimer;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
