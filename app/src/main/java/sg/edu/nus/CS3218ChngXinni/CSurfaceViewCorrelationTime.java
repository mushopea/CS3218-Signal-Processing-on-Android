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

import java.util.Arrays;


public class CSurfaceViewCorrelationTime extends SurfaceView implements SurfaceHolder.Callback  {
    private Context drawContext;
    public  DrawThread       drawThread;
    private SurfaceHolder    drawSurfaceHolder;
    private Boolean          threadExists = false;
    public static volatile   Boolean drawFlag = false;
    private static int rectPos = 0;


    private static final Handler handler = new Handler(){
        public void handleMessage(Message paramMessage)
        {
        }
    };

    public CSurfaceViewCorrelationTime(Context ctx, AttributeSet attributeSet)
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

    class DrawThread extends Thread {
        private Bitmap soundBackgroundImage;
        private SurfaceHolder soundSurfaceHolder;
        private Paint redLine, blueLine;
        private double[] redPoints, bluePoints, flippedKernel, redPointsPadded,
                correlatedArray;
        private int drawScale = 32;
        private static final int FFT_Len = 512;
        private int            soundCanvasHeight = 0;
        private int			   soundCanvasWidth  = 0;
        private double         mxIntensity;

        public DrawThread(SurfaceHolder paramContext, Context paramHandler, Handler arg4) {
            soundSurfaceHolder = paramContext;
            soundBackgroundImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

            redLine = new Paint();
            redLine.setARGB(255, 255, 0, 0);
            redLine.setStrokeWidth(5);
            redLine.setAntiAlias(true);

            blueLine = new Paint();
            blueLine.setARGB(255, 0, 0, 255);
            blueLine.setStrokeWidth(5);
        }

        public void doDraw(Canvas canvas) {
            soundCanvasHeight = canvas.getHeight();
            soundCanvasWidth = canvas.getWidth();

            int height = soundCanvasHeight;
            int width = soundCanvasWidth;
            int shift = 0;

            Paint fillPaint = new Paint();
            fillPaint.setColor(Color.YELLOW);
            fillPaint.setStyle(Style.FILL);
            canvas.drawPaint(fillPaint);

            // * * * * * * * * * * * * *
            //  red line
            // * * * * * * * * * * * * *
            float ystart, ystop;
            redPoints = new double[300];
            for (int i = 0; i < redPoints.length; i++) {
                redPoints[i] = 0; // fill with 0
            }
            for (int i = redPoints.length / 2 - 100; i < redPoints.length / 2 + 100 + 1; i++) {
                redPoints[i] = 2; // fill with 2
            }
            for (int i = 0; i < redPoints.length - 1; i++) {
                ystart = (float) redPoints[i] * -drawScale + height / 8;
                ystop = (float) redPoints[i + 1] * -drawScale + height / 8;

                canvas.drawLine(i + shift, ystart, i + 1 + shift, ystop, redLine); // draw red line
            }

            // * * * * * * * * * * * * *
            //  bloo line
            // * * * * * * * * * * * * *
            float ystart2, ystop2;
            bluePoints = new double[200];
            for (int i = 0; i < bluePoints.length; i++) {
                bluePoints[i] = 0; // fill with 0
            }
            bluePoints[50] = 1;
            for (int i = 50; i < 151; i++) {
                bluePoints[i] = 1 - (((float) i - 50) / 100); // fill with 2
            }
            for (int i = 0; i < bluePoints.length - 1; i++) {
                ystart2 = (float) bluePoints[i] * -drawScale + height / 6;
                ystop2 = (float) bluePoints[i + 1] * -drawScale + height / 6;

                canvas.drawLine(i + shift, ystart2, i + 1 + shift, ystop2, blueLine);
            }


            // * * * * * * * * * * * * *
            //  red line 2
            // * * * * * * * * * * * * *
            float ystart3, ystop3;
            for (int i = 0; i < redPoints.length - 1; i++) {
                ystart3 = (float) redPoints[i] * -drawScale + height / 3;
                ystop3 = (float) redPoints[i + 1] * -drawScale + height / 3;

                canvas.drawLine(i + shift, ystart3, i + 1 + shift, ystop3, redLine); // draw red line
            }

            // * * * * * * * * * * * * *
            //  bloo line 2
            // * * * * * * * * * * * * *
            float yStart4, ystop4;
            for (int i = 0; i < bluePoints.length - 1; i++) {
                yStart4 = (float) bluePoints[i] * -drawScale + height / 3;
                ystop4 = (float) bluePoints[i + 1] * -drawScale + height / 3;

                canvas.drawLine(rectPos + i + shift, yStart4, rectPos + i + 1 + shift, ystop4, blueLine);
            }

            // * * * * * * * * * * * * *
            //  flipped kernel
            // * * * * * * * * * * * * *
            flippedKernel = new double[200];
            for (int i = 0; i < 200; i++) {
                flippedKernel[i] = bluePoints[200 - i - 1];
            }

            // * * * * * * * * * * * * *
            //  correlated array
            // * * * * * * * * * * * * *
            // define correlation result
            correlatedArray = new double[width];
            Arrays.fill(correlatedArray, 0);

            // pad the signal with zeroes so as to read in the entire signal
            redPointsPadded = new double[width];
            Arrays.fill(redPointsPadded, 0);
            for (int i = 0; i < redPoints.length; i++) {
                redPointsPadded[i] = redPoints[i];
            }

            for(int t = 0; t < width; t++) {
                correlatedArray[t] = 0;
                for (int i = 0; i < 200; i++) {
                    if (t - i < 0) {
                    } else {
                        correlatedArray[t] += redPointsPadded[t - i] * bluePoints[200-i-1];
                    }
                }
            }

            for (int i = 0; i < rectPos; i++) {
                canvas.drawLine(i, (float) correlatedArray[i] * -1 + height / 2, i + 1, (float) correlatedArray[i + 1] * -1 + height / 2, redLine);
            }
        }


        public void setSurfaceSize(int canvasWidth, int canvasHeight) {
            synchronized (soundSurfaceHolder) {
                soundBackgroundImage = Bitmap.createScaledBitmap(
                        soundBackgroundImage, canvasWidth, canvasHeight, true);
            }
        }

        @Override
        public void run() {
            while (drawFlag) {
                Canvas localCanvas = null;
                int width;
                try {
                    localCanvas = soundSurfaceHolder.lockCanvas();
                    synchronized (soundSurfaceHolder) {
                        if (localCanvas != null) {
                            width = localCanvas.getWidth();
                            doDraw(localCanvas);
                            rectPos += 1;
                            if (rectPos + bluePoints.length >= 800)
                                rectPos = 200;
                        }
                    }
                } finally {
                    if (localCanvas != null)
                        soundSurfaceHolder.unlockCanvasAndPost(localCanvas);
                }
            }
        }
    }
}
