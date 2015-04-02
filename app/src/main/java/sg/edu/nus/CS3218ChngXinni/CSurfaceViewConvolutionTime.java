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


public class CSurfaceViewConvolutionTime extends SurfaceView implements SurfaceHolder.Callback  {
    private Context drawContext;
    public  DrawThread       drawThread;
    private SurfaceHolder    drawSurfaceHolder;
    private Boolean          threadExists = false;
    public static volatile   Boolean drawFlag = false;

    private static final Handler handler = new Handler(){
        public void handleMessage(Message paramMessage)
        {
        }
    };

    public CSurfaceViewConvolutionTime(Context ctx, AttributeSet attributeSet)
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
        private double[] redPoints, bluePoints;
        private int drawScale = 32;
        private static final int FFT_Len = 512;
        private int            soundCanvasHeight = 0;
        private int			   soundCanvasWidth  = 0;

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
            for (int i = redPoints.length/2 - 100; i < redPoints.length/2 + 100 + 1; i++){
                redPoints[i] = 2; // fill with 2
            }
            for (int i = 0; i < redPoints.length - 1; i++) {
                ystart = (float) redPoints[i] * -drawScale + height/8;
                ystop = (float) redPoints [i+1] * -drawScale + height/8;

                canvas.drawLine(i, ystart, i+1, ystop, redLine); // draw red line
            }

            // * * * * * * * * * * * * *
            //  bloo line
            // * * * * * * * * * * * * *
            

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
                    localCanvas = soundSurfaceHolder.lockCanvas(null);
                    synchronized (soundSurfaceHolder) {
                        if (localCanvas != null) {
                            width = localCanvas.getWidth();
                            doDraw(localCanvas);
                            /*blueCurvePos += 1;
                            if (blueCurvePos + blueCurveLength >= width)
                                blueCurvePos = 0;*/
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
