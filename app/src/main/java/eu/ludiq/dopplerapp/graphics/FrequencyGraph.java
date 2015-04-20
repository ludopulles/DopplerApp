package eu.ludiq.dopplerapp.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Comparator;

import eu.ludiq.dopplerapp.audio.Frequency;

public class FrequencyGraph extends View {

    private static final String TAG = "FrequencyGraph";
    private static final double DEFAULT_MAG = 10.0;

    /**
     * Based on:
     * <a href="http://www.cplusplus.com/reference/algorithm/lower_bound/">http://www.cplusplus.com/reference/algorithm/lower_bound/</a>
     *
     * @param array      the array to search in
     * @param value      the value to be searched
     * @param comparator compares
     * @param <T>        type of objects
     * @return the smallest i for which value >= array[i] holds
     */
    public static <T> int lowerBound(T[] array, T value, Comparator<T> comparator) {
        int first = 0, last = array.length, count = array.length, step, it;
        while (count > 0) {
            it = first;
            step = count / 2;
            it += step;
            if (comparator.compare(array[it], value) < 0) {
                first = ++it;
                count -= step + 1;
            } else {
                count = step;
            }
        }
        return first;
    }

    private Paint axes, line;

    private Frequency[] frequencies;
    private double minFreq = 0, maxFreq = 0, maxMag = DEFAULT_MAG, diffFreq;
    private int selectedIndex = -1;

    public FrequencyGraph(Context context) {
        super(context);
        init(context);
    }

    public FrequencyGraph(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public FrequencyGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.axes = new Paint();
        this.axes.setColor(0xFF000000);
        this.axes.setStrokeWidth(2f);
        this.line = new Paint();

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handleTouchEvent(event.getX());
                }
                return true;
            }
        });
    }

    private void handleTouchEvent(float x) {
        if (frequencies == null || frequencies.length == 0) {
            return;
        }
        // x = (f - minFreq) * w / diffFreq
        // f = minFreq + x * diffFreq / w
        double f = minFreq + x * diffFreq / getWidth();
        Frequency freq = new Frequency(f, 0.0);

        int index = lowerBound(this.frequencies, freq, new Comparator<Frequency>() {
            @Override
            public int compare(Frequency lhs, Frequency rhs) {
                // l.f < r.f => l.f - r.f < 0
                return (int) Math.signum(lhs.frequency - rhs.frequency);
            }
        });
        if (index > 0 && f - this.frequencies[index - 1].frequency < this.frequencies[index].frequency - f) {
            index--;
        }

//        Log.i(TAG, "pressed at " + x + "; freq = " + f);
//        Log.i(TAG, "frequency found at " + index + ", with value " + this.frequencies[index]);

        this.selectedIndex = index;
        invalidate();
    }

    public void setFrequencies(Frequency[] frequencies) {
        this.frequencies = frequencies;
        this.selectedIndex = -1;

        if (this.frequencies != null && this.frequencies.length > 0) {

            minFreq = Integer.MAX_VALUE;
            maxFreq = Integer.MIN_VALUE;

            maxMag = DEFAULT_MAG;
            int maxMagIndex = -1;

            for (int i = 0; i < this.frequencies.length; i++) {
                Frequency f = this.frequencies[i];

                minFreq = Math.min(minFreq, f.frequency);
                maxFreq = Math.max(maxFreq, f.frequency);

                if (f.magnitude > maxMag) {
                    maxMag = f.magnitude;
                    maxMagIndex = i;
                }
            }

            diffFreq = maxFreq - minFreq;
            this.selectedIndex = maxMagIndex;
        }
    }

    public Frequency[] getFrequencies() {
        return frequencies;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public Frequency getSelectedFrequency() {
        if (this.selectedIndex < 0 || this.frequencies == null || this.selectedIndex >= this.frequencies.length) {
            return null;
        }
        return this.frequencies[this.selectedIndex];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();

        if (this.frequencies != null && this.frequencies.length > 0) {
//            Log.d(TAG, "drawing with " + minFreq + ", " + diffFreq + ", " + maxMag);
            for (int i = 0; i < this.frequencies.length; i++) {
                Frequency f = this.frequencies[i];
                double drawX = (f.frequency - minFreq) * w / diffFreq;
                double drawY = h - f.magnitude * h / maxMag;

                if (i == selectedIndex) {
                    line.setColor(0xFF00CCEE);
                } else {
                    line.setARGB(255, (int) ((f.magnitude) * 255 / maxMag), 80, 0);
                }
                canvas.drawLine((float) drawX, h, (float) drawX, (float) drawY, line);
            }
        }
        canvas.drawLine(0, h, w, h, this.axes);
    }
}
