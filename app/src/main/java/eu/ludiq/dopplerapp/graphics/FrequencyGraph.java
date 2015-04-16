package eu.ludiq.dopplerapp.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import eu.ludiq.dopplerapp.fft.Frequency;

public class FrequencyGraph extends View {

    private Frequency[] frequencies;
    private Paint line;

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
        this.line = new Paint();
        this.line.setColor(0xFF000000);
    }

    public void setFrequencies(Frequency[] frequencies) {
        this.frequencies = frequencies;
    }

    public Frequency[] getFrequencies() {
        return frequencies;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth(), h = getHeight();

        canvas.drawLine(0, h, w, h, this.line);

        if (frequencies == null || frequencies.length == 0) return;
        int n = frequencies.length;

        double minFreq = Integer.MAX_VALUE, maxFreq = Integer.MIN_VALUE;
        double minMag = Integer.MAX_VALUE, maxMag = Integer.MIN_VALUE;

        for (Frequency f : frequencies) {
            minFreq = Math.min(minFreq, f.frequency);
            maxFreq = Math.max(maxFreq, f.frequency);

            minMag = Math.min(minMag, f.magnitude);
            maxMag = Math.max(maxMag, f.magnitude);
        }

        double diffFreq = maxFreq - minFreq;
        double diffMag = maxMag - minMag;

        for (Frequency f : frequencies) {
            double drawX = (f.frequency - minFreq) * w / diffFreq;
            double drawY = h - (f.magnitude - minMag) * h / diffMag;

            line.setARGB(255, (int) ((f.magnitude - minMag) * 255 / diffMag), 80, 0);
            canvas.drawLine((float) drawX, h, (float) drawX, (float) drawY, line);
        }
    }
}
