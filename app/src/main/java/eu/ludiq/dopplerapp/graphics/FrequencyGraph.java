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

        canvas.drawLine(0, 0, 0, h, this.line);
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
            double drawx = (f.frequency - minFreq) * w / diffFreq;
            double drawy = (f.magnitude - minMag) * h / diffMag;

            canvas.drawLine((float) drawx, h, (float) drawx, (float) drawy, line);
        }
    }
}