package sg.edu.nus.CS3218ChngXinni;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class CSurfaceViewLiveFFT extends SurfaceView implements SurfaceHolder.Callback {

    private Context drawContext;
    public DrawThread drawThread;
    private SurfaceHolder drawSurfaceHolder;
    private Boolean threadExists = false;

    public static volatile Boolean drawFlag = false;


    private static final Handler handler = new Handler() {

        public void handleMessage(Message paramMessage) {
        }

    };

    public CSurfaceViewLiveFFT(Context ctx, AttributeSet attributeSet)
    {
        super(ctx, attributeSet);

        drawContext = ctx;

        init();

    }

    public void init()
    {

        if (!threadExists) {

            drawSurfaceHolder = getHolder();
            drawSurfaceHolder.addCallback(this);

            drawThread = new DrawThread(drawSurfaceHolder, drawContext, handler);

            drawThread.setName("" +System.currentTimeMillis());
            drawThread.start();
        }

        threadExists = Boolean.valueOf(true);

        drawFlag     = Boolean.valueOf(true);

        return;

    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
    {
        drawThread.setSurfaceSize(paramInt2, paramInt3);
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {

        init();

    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
        while (true)
        {
            if (!drawFlag)
                return;
            try
            {
                drawFlag = Boolean.valueOf(false);
                drawThread.join();

            }
            catch (InterruptedException localInterruptedException)
            {
            }
        }

    }

    class DrawThread extends Thread
    {
        private Bitmap         soundBackgroundImage;
        private short[]        soundBuffer;
        private int[]          soundSegmented;
        private double[]       soundFFT;
        private double[]       soundFFTMag;
        private double[]       soundFFTTemp;
        public  Boolean        FFTComputed  = Boolean.valueOf(false);
        public  Boolean        soundCapture = Boolean.valueOf(false);
        public int             FFT_Len      = 1024;
        public  int            segmentIndex = -1;
        private int            soundCanvasHeight = 0;
        private int			   soundCanvasWidth  = 0;
        private Paint          soundLinePaint;
        private Paint		   soundLinePaint2;
        private Paint          soundLinePaint3;
        private SurfaceHolder  soundSurfaceHolder;
        private int            drawScale = 20;
        private double         mxIntensity;



        public DrawThread(SurfaceHolder paramContext, Context paramHandler, Handler arg4)
        {
            soundSurfaceHolder = paramContext;

            soundLinePaint     = new Paint();
            soundLinePaint.setARGB(255, 0, 0, 255); // blue = soundlinepaint
            soundLinePaint.setStrokeWidth(3);

            soundLinePaint2     = new Paint();
            soundLinePaint2.setAntiAlias(true);
            soundLinePaint2.setARGB(255, 255, 0, 0); // red = soundlinepaint2
            soundLinePaint2.setStrokeWidth(4);

            soundLinePaint3     = new Paint();
            soundLinePaint3.setAntiAlias(true);
            soundLinePaint3.setARGB(255, 0, 255, 255); // cyan
            soundLinePaint3.setStrokeWidth(3);

            soundBuffer          = new short[2048];

            soundBackgroundImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

            soundSegmented     = new int[FFT_Len];
            soundFFT           = new double[FFT_Len*2];
            soundFFTMag        = new double[FFT_Len];
            soundFFTTemp       = new double[FFT_Len*2];

        }

        public void doDraw(Canvas canvas) {

            soundCanvasHeight = canvas.getHeight();
            soundCanvasWidth = canvas.getWidth();

            int height = soundCanvasHeight;
            int width = soundCanvasWidth;

            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            canvas.drawText("'Capture Sound' to see Live FFT", 250, 20, paint);

            if (!soundCapture) { // just show the sound only, no FFT
                int xStart = 0;

                while (xStart < width - 1) {

                    int yStart = soundBuffer[xStart] / height * drawScale;
                    int yStop = soundBuffer[xStart + 1] / height * drawScale;

                    int yStart1 = yStart + height / 4;
                    int yStop1 = yStop + height / 4;

                    canvas.drawLine(xStart, yStart1, xStart + 1, yStop1, soundLinePaint2);

                    if (xStart % 100 == 0) {
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(20);
                        canvas.drawText(Integer.toString(xStart), xStart, height / 2, paint);
                        canvas.drawText(Integer.toString(yStop), xStart, yStop1, paint);
                    }

                    xStart++;
                }

            } else { // sound capture code here (perform live fft)
                int xStart = 0;

                while (xStart < width - 1) {

                    int yStart = soundBuffer[xStart] / height * drawScale;
                    int yStop = soundBuffer[xStart + 1] / height * drawScale;

                    int yStart1 = yStart + height / 4;
                    int yStop1 = yStop + height / 4;

                    canvas.drawLine(xStart, yStart1, xStart + 1, yStop1, soundLinePaint2);

                    if (xStart % 100 == 0) {
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(20);
                        canvas.drawText(Integer.toString(xStart), xStart, height / 2, paint);
                        canvas.drawText(Integer.toString(yStop), xStart, yStop1, paint);
                    }

                    xStart++;
                }

                segmentIndex = -1;
                FFTComputed = Boolean.valueOf(false);


                if (!FFTComputed) {

                    segmentIndex = 0;
                    while (segmentIndex < FFT_Len) {
                        soundSegmented[segmentIndex] = soundBuffer[segmentIndex];
                        soundFFT[2 * segmentIndex] = (double) soundSegmented[segmentIndex];
                        soundFFT[2 * segmentIndex + 1] = 0.0;
                        segmentIndex++;
                    }

                    // fft
                    DoubleFFT_1D fft = new DoubleFFT_1D(FFT_Len);
                    fft.complexForward(soundFFT);
                    FFTComputed = Boolean.valueOf(true);

                    // perform fftshift here
                    for (int i = 0; i < FFT_Len; i++) {
                        soundFFTTemp[i] = soundFFT[i + FFT_Len];
                        soundFFTTemp[i + FFT_Len] = soundFFT[i];
                    }
                    for (int i = 0; i < FFT_Len * 2; i++) {
                        soundFFT[i] = soundFFTTemp[i];
                    }

                    double mx = -99999;
                    for (int i = 0; i < FFT_Len; i++) {
                        double re = soundFFT[2 * i];
                        double im = soundFFT[2 * i + 1];
                        soundFFTMag[i] = Math.log(re * re + im * im + 0.001);
                        if (soundFFTMag[i] > mx) mx = soundFFTMag[i];
                    }

                    // normalize
                    for (int i = 0; i < FFT_Len; i++) {
                        soundFFTMag[i] = height * 7 / 8 - soundFFTMag[i] / mx * 500;
                    }

                    mxIntensity = mx;

                    FFTComputed = Boolean.valueOf(true);

                }

                // print the maximum intensity
                paint.setColor(Color.BLACK);
                paint.setTextSize(30);
                canvas.drawText("max Intensity = " + String.valueOf(mxIntensity), 100, height - 30, paint);

                // display the fft results
                int xStepSz = 1;
                // draw the vertical axis (at DC location)
                canvas.drawLine(FFT_Len / 2, height, FFT_Len / 2, 0, soundLinePaint3);
                for (int i = 0; i < FFT_Len - 1; i += xStepSz) {
                    canvas.drawLine(i / xStepSz, (int) soundFFTMag[i], i / xStepSz + 1, (int) soundFFTMag[i + 1], soundLinePaint);

                    if ((i - 12) % 50 == 0) {
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(20);
                        canvas.drawText(Integer.toString(i - FFT_Len / 2), i, height * 7 / 8, paint);
                    }
                }
            }
        }



        public void setBuffer(short[] paramArrayOfShort)
        {
            synchronized (soundBuffer)
            {
                soundBuffer = paramArrayOfShort;
                return;
            }
        }


        public void setSurfaceSize(int canvasWidth, int canvasHeight)
        {
            synchronized (soundSurfaceHolder)
            {
                soundBackgroundImage = Bitmap.createScaledBitmap(soundBackgroundImage, canvasWidth, canvasHeight, true);
                return;
            }
        }


        public void run()
        {

            while (drawFlag)
            {

                Canvas localCanvas = null;
                try
                {
                    localCanvas = soundSurfaceHolder.lockCanvas(null);
                    synchronized (soundSurfaceHolder)
                    {
                        if (localCanvas != null)
                            doDraw(localCanvas);

                    }
                }
                finally
                {
                    if (localCanvas != null)
                        soundSurfaceHolder.unlockCanvasAndPost(localCanvas);
                }
            }
        }


    }


    }