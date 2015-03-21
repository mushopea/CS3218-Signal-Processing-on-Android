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


public class CSurfaceViewGraph extends SurfaceView implements SurfaceHolder.Callback  {
    private Context          drawContext;
    public  DrawThread       drawThread;
    private SurfaceHolder    drawSurfaceHolder;
    private Boolean          threadExists = false;
    public static volatile   Boolean drawFlag = false;

    private static final Handler handler = new Handler(){

        public void handleMessage(Message paramMessage)
        {
        }
    };


    public void setDrawSurfaceHolder()
    {
        drawSurfaceHolder = getHolder();
        drawSurfaceHolder.addCallback(this);
    }

    public CSurfaceViewGraph(Context ctx, AttributeSet attributeSet)
    {
        super(ctx, attributeSet);

        drawContext = ctx;

        setDrawSurfaceHolder();

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
        drawFlag = Boolean.valueOf(true);

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
        public int             FFT_Len      = 512;
        private int            soundCanvasHeight = 0;
        private int			   soundCanvasWidth  = 0;
        private Paint          soundLinePaint;
        private Paint		   soundLinePaint2;
        private Paint          soundLinePaint3;
        private SurfaceHolder  soundSurfaceHolder;
        private int            drawScale = 5;


        public DrawThread(SurfaceHolder paramContext, Context paramHandler, Handler arg4)
        {
            soundSurfaceHolder = paramContext;

            soundLinePaint     = new Paint();
            soundLinePaint.setARGB(255, 0, 0, 255);
            soundLinePaint.setStrokeWidth(3);

            soundLinePaint2     = new Paint();
            soundLinePaint2.setAntiAlias(true);
            soundLinePaint2.setARGB(255, 255, 0, 0);
            soundLinePaint2.setStrokeWidth(4);

            soundLinePaint3     = new Paint();
            soundLinePaint3.setAntiAlias(true);
            soundLinePaint3.setARGB(255, 0, 255, 255);
            soundLinePaint3.setStrokeWidth(3);

            soundBuffer          = new short[1024];

            soundBackgroundImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        }



        public void doDraw(Canvas canvas)
        {

            soundCanvasHeight  = canvas.getHeight();
            soundCanvasWidth   = canvas.getWidth();

            int height         = soundCanvasHeight;

            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Style.FILL);
            canvas.drawPaint(paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(20);

            canvas.drawText("Composing a Signal from Sinusoids", 20, 20, paint);

            int xStart = 0;

            while (xStart < FFT_Len-1) {

                int yStart = -soundBuffer[xStart] * drawScale;
                int yStop  = -soundBuffer[xStart+1] * drawScale;

                int yStart1 = yStart + height/4;
                int yStop1  = yStop  + height/4;

                canvas.drawLine(xStart, yStart1, xStart +1, yStop1, soundLinePaint2);

                if (xStart %100 == 0) {
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(20);
                }

                xStart++;
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
