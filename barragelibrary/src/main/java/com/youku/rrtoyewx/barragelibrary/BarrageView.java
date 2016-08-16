package com.youku.rrtoyewx.barragelibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.CheckResult;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rrtoyewx on 16/7/24.
 * Managed to draw all the barrage,
 * and can dynamically adjust the number of times each add barrage,
 * and each addition interval between barrage
 *
 * @see #autoAdjustAddCountFlag
 */
public class BarrageView extends View {
    private static final String TAG = BarrageView.class.getSimpleName();

    //high level > 500 , medium level 100 ~ 500, low < 100
    private static final int HIGH_LEVEL = 500; //>500
    private static final int MEDIUM_LEVEL = 100; //100~500
    //addCount
    private static final int ADD_COUNT_WHEN_HIGH_LEVEL = 10;
    private static final int ADD_COUNT_WHEN_MEDIUM_LEVEL = 5;
    private static final int ADD_COUNT_WHEN_LOW_LEVEL = 3;
    //addPerTime
    private static final double ADD_PER_TIME_WHEN_HIGH_LEVEL = 0.2;
    private static final double ADD_PER_TIME_WHEN_MEDIUM_LEVEL = 0.5;
    private static final double ADD_PER_TIME_WHEN_LOW_LEVEL = 1.0;

    //default value
    private static final int DEFAULT_CHANEL_COUNTS = 10;
    private static final int DEFAULT_ADD_COUNT = 1;
    private static final int DEFAULT_ADD_TIME_DISTANCE = 1000;
    private static final boolean DEFAULT_AUTO_ADJUST_ADD_COUNT = false;
    private static final int DEFAULT_MAX_COUNT_IN_LINE = 100;

    private int chanelCounts;
    private int maxCountsInChanel;
    private int chanelTotalHeight;
    private int chanelHeight;
    private int[] yAxesValues;

    //每次取的间隔时间
    private long addPerTime;
    private long startAddPerTime;
    //每次取的个数
    private int addItemCount;
    private int startAddItemCount;
    //是否自动调整取的个数以及每次取的时间
    private boolean autoAdjustAddCountFlag;
    //上次取弹幕的时间
    private long perAddItemTime;
    //等待弹幕个数的级别
    private CountLevel countLevel;
    //热度：控制显示弹幕的行
    private boolean hotDegreeFlag;


    private DrawStatus drawStatus;
    private boolean autoPauseFlag;


    /**
     * DrawStatues
     * <p>
     * ~~~~~~~~~start()            pause()
     * START ----------- RUNNING ------------PAUSE
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ resume()
     * <p>
     * ~~~~~~~ stop()
     * START --------- STOP
     * ~~~~~~~reset()
     * <p>
     * ~~~~~~stop()
     * PAUSE ------- STOP
     * <p>
     * ~~~~~~~stop()
     * RUNNING-------STOP
     *
     * @see #start()
     * @see #stop()
     * @see #pause()
     * @see #resume()
     * @see #reset()
     */
    private enum DrawStatus {
        START,
        RUNNING,
        PAUSE,
        STOP
    }

    public enum CountLevel {
        HIGH,
        MEDIUM,
        LOW
    }

    private Map<Integer, List<BaseBarrageItem>> runningItemMap;
    private Deque<BaseBarrageItem> waitingItems;

    {
        chanelCounts = DEFAULT_CHANEL_COUNTS;
        maxCountsInChanel = DEFAULT_MAX_COUNT_IN_LINE;
        yAxesValues = new int[chanelCounts];

        addPerTime = DEFAULT_ADD_TIME_DISTANCE;
        addItemCount = DEFAULT_ADD_COUNT;
        autoAdjustAddCountFlag = DEFAULT_AUTO_ADJUST_ADD_COUNT;
        countLevel = CountLevel.LOW;

        waitingItems = new LinkedList<>();
        runningItemMap = new LinkedHashMap<>();

        drawStatus = DrawStatus.START;
        autoPauseFlag = false;
    }

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.barrage_view, defStyleAttr, 0);

        chanelCounts = typedArray.getInteger(R.styleable.barrage_view_chanel_counts, DEFAULT_CHANEL_COUNTS);
        addPerTime = typedArray.getInteger(R.styleable.barrage_view_chanel_add_time, DEFAULT_ADD_TIME_DISTANCE);
        autoAdjustAddCountFlag = typedArray.getBoolean(R.styleable.barrage_view_auto_adjust_add_count, DEFAULT_AUTO_ADJUST_ADD_COUNT);
        addItemCount = typedArray.getInt(R.styleable.barrage_view_chanel_add_count, DEFAULT_ADD_COUNT);
        maxCountsInChanel = typedArray.getInt(R.styleable.barrage_view_max_barrage_counts_in_line, DEFAULT_MAX_COUNT_IN_LINE);

        Log.d(TAG, "chanelCounts:" + chanelCounts + ",addPerTime:" + addPerTime + ",autoAdjustAddCountFlag:" + autoAdjustAddCountFlag
                + ",addItemCount" + addItemCount + ",maxCountsInChanel" + maxCountsInChanel);

        typedArray.recycle();

        init();
    }

    private void init() {
        for (int i = 0; i < chanelCounts; i++) {
            runningItemMap.put(i, new LinkedList<BaseBarrageItem>());
        }

        startAddPerTime = addPerTime;
        startAddItemCount = addItemCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        calculateValue(heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h != oldh) {
            calculateValue(h);
        }
    }

    private void calculateValue(int height) {
        chanelTotalHeight = (height - getPaddingTop() - getPaddingBottom());
        chanelHeight = chanelTotalHeight / chanelCounts;
        Log.d(TAG, height + "height");
        for (int i = 0; i < chanelCounts; i++) {
            yAxesValues[i] = chanelTotalHeight / chanelCounts * (i + 1) - chanelHeight / 2;
            Log.d(TAG, yAxesValues[i] + "yAxesValues[" + i + "]");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRunningItem(canvas);
        if (drawStatus == DrawStatus.RUNNING) {
            getItemFromWaiting(canvas);
            invalidate();
            autoPauseIfEmpty();
        }
        super.onDraw(canvas);
    }

    private void drawRunningItem(Canvas canvas) {
        for (int i = 0; i < chanelCounts; i++) {
            List<BaseBarrageItem> barrageItemList = runningItemMap.get(i);
            Iterator<BaseBarrageItem> iterator = barrageItemList.iterator();
            while (iterator.hasNext()) {
                BaseBarrageItem item = iterator.next();
                if (!item.isDrawEnd()) {
                    item.draw(canvas);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    private void getItemFromWaiting(Canvas canvas) {
        calculateCurSizeLevel();
        if (checkInTime()) {
            perAddItemTime = System.currentTimeMillis();
            addItemCount = autoAdjustAddCountFlag ? dynamicCalculateAddCount() : addItemCount;
            Log.d(TAG, "addItemCount:" + addItemCount + "addPerTime:" + addPerTime);

            for (int i = 0; i < addItemCount; i++) {
                BaseBarrageItem item = waitingItems.pollFirst();
                if (item != null) {
                    int chanelNumber = calculateNextItemChanel(item);
                    Log.d(TAG, "addChanelNumber:" + chanelNumber);
                    if (chanelNumber != -1) {
                        item.setStartInfo(getWidth() - getPaddingRight(), yAxesValues[chanelNumber], getPaddingLeft(), yAxesValues[chanelNumber]);
                        item.draw(canvas);
                        runningItemMap.get(chanelNumber).add(item);
                    } else {
                        waitingItems.offerFirst(item);
                    }
                }
            }
        }
    }

    private void calculateCurSizeLevel() {
        countLevel = waitingItems.size() >= HIGH_LEVEL ? CountLevel.HIGH :
                waitingItems.size() >= MEDIUM_LEVEL ? CountLevel.MEDIUM : CountLevel.LOW;
    }

    private int dynamicCalculateAddCount() {
        switch (countLevel) {
            case HIGH:
                return ADD_COUNT_WHEN_HIGH_LEVEL;
            case MEDIUM:
                return ADD_COUNT_WHEN_MEDIUM_LEVEL;
            default:
            case LOW:
                return ADD_COUNT_WHEN_LOW_LEVEL;
        }
    }

    private double dynamicCalculateAddPerTime() {
        switch (countLevel) {
            case HIGH:
                return startAddPerTime * ADD_PER_TIME_WHEN_HIGH_LEVEL;
            case MEDIUM:
                return startAddPerTime * ADD_PER_TIME_WHEN_MEDIUM_LEVEL;
            default:
            case LOW:
                return startAddPerTime * ADD_PER_TIME_WHEN_LOW_LEVEL;
        }
    }

    @CheckResult
    private boolean checkInTime() {
        addPerTime = autoAdjustAddCountFlag ? (long) dynamicCalculateAddPerTime() : startAddPerTime;
        return System.currentTimeMillis() - perAddItemTime >= addPerTime;
    }

    @CheckResult
    private int calculateNextItemChanel(BaseBarrageItem addItem) {
        List<BaseBarrageItem> barrageItemList = null;
        int curChanelCount = hotDegreeFlag ? chanelCounts : chanelCounts / 2;
        int randomNumber = (int) (Math.random() * curChanelCount);

        //empty list
        for (int chanelNumber = 0; chanelNumber < curChanelCount; chanelNumber++) {
            barrageItemList = runningItemMap.get((randomNumber + chanelNumber) % curChanelCount);
            if (barrageItemList.isEmpty()) {
                Log.d(TAG, "empty list " + (chanelNumber + randomNumber) % curChanelCount);
                return (chanelNumber + randomNumber) % curChanelCount;
            }
        }

        //list size < maxCountsInChanel
        for (int chanelNumber = 0; chanelNumber < curChanelCount; chanelNumber++) {
            barrageItemList = runningItemMap.get((chanelNumber + randomNumber) % curChanelCount);

            if (barrageItemList.size() == maxCountsInChanel) {
                continue;
            }

            BaseBarrageItem item = barrageItemList.get(barrageItemList.size() - 1);
            Log.d(TAG, "before:" + (chanelNumber + randomNumber) % curChanelCount);
            if (!item.isOverlay(addItem, countLevel)) {
                return (chanelNumber + randomNumber) % curChanelCount;
            }
        }

        return -1;
    }


    private void autoPauseIfEmpty() {
        if (!waitingItems.isEmpty()) {
            return;
        }

        for (int i = 0; i < chanelCounts; i++) {
            List<BaseBarrageItem> baseBarrageItems = runningItemMap.get(i);
            if (!baseBarrageItems.isEmpty()) {
                return;
            }
        }

        pause();
        autoPauseFlag = true;
    }


    public void addItem(BaseBarrageItem item) {
        addItem(item, true);
    }

    public void addItem(BaseBarrageItem item, boolean autoStart) {
        waitingItems.add(item);

        if (autoStart && waitingItems.size() == 1 && autoPauseFlag) {
            drawStatus = DrawStatus.RUNNING;
            autoPauseFlag = false;
            postInvalidate();
        }
    }

    public void addItemInHead(BaseBarrageItem item) {
        addItemInHead(item, true);
    }

    public void addItemInHead(BaseBarrageItem item, boolean autoStart) {
        waitingItems.offerFirst(item);

        if (autoStart && waitingItems.size() == 1 && autoPauseFlag) {
            drawStatus = DrawStatus.RUNNING;
            autoPauseFlag = false;
            postInvalidate();
        }
    }

    public void addItemList(List<BaseBarrageItem> items) {
        addItemList(items, true);
    }

    public void addItemList(List<BaseBarrageItem> items, boolean autoStart) {
        waitingItems.addAll(items);

        if (autoStart && waitingItems.size() == items.size() && autoPauseFlag) {
            drawStatus = DrawStatus.RUNNING;
            autoPauseFlag = false;
            postInvalidate();
        }
    }

    public void addItemListInHead(List<BaseBarrageItem> items) {
        addItemListInHead(items, true);
    }

    public void addItemListInHead(List<BaseBarrageItem> items, boolean autoStart) {
        for (BaseBarrageItem item : items) {
            waitingItems.offerFirst(item);
        }

        if (autoStart && waitingItems.size() == items.size() && autoPauseFlag) {
            drawStatus = DrawStatus.RUNNING;
            autoPauseFlag = false;
            postInvalidate();
        }
    }

    public void setHotDegreeFlag(boolean hotDegreeFlag) {
        this.hotDegreeFlag = hotDegreeFlag;
    }

    public void start() {
        if (drawStatus == DrawStatus.START) {
            drawStatus = DrawStatus.RUNNING;
            postInvalidate();
        }
    }

    public void stop() {
        drawStatus = DrawStatus.STOP;

        waitingItems.clear();
        for (int i = 0; i < chanelCounts; i++) {
            runningItemMap.get(i).clear();
        }
        postInvalidate();
    }

    public void reset() {
        drawStatus = DrawStatus.START;
        autoPauseFlag = false;
        addItemCount = startAddItemCount;
        addPerTime = startAddPerTime;
    }

    public void pause() {
        if (drawStatus == DrawStatus.RUNNING) {
            drawStatus = DrawStatus.PAUSE;
        }
    }

    public void resume() {
        if (drawStatus == DrawStatus.PAUSE) {
            drawStatus = DrawStatus.RUNNING;

            postInvalidate();
        }
    }
}