package sg.edu.nus.CS3218ChngXinni;

import android.media.AudioRecord;
import android.util.Log;

/**
 * Created by ngtk on 22/1/15.
 */
public class SoundSamplerSpectrogram {


    private static final int  FS = 22050;     // sampling frequency
    public  AudioRecord       audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private SpectrogramActivity   spectrogramActivity;
    private Thread            recordingThread;


    public SoundSamplerSpectrogram(SpectrogramActivity mAct) throws Exception
    {

        spectrogramActivity = mAct;

        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));

        }
        catch (Exception e) {
            Log.d("Error in SoundSamplerSpectrogram(SpectrogramActivity mAct) ", e.getMessage());
            throw new Exception();
        }


        return;


    }



    public void init() throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
            Log.e("mwo", "blabla is " + AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in Init() ", e.getMessage());
            throw new Exception();
        }


        SpectrogramActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        SpectrogramActivity.buffer = new short[SpectrogramActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {

                    audioRecord.read(SpectrogramActivity.buffer, 0, SpectrogramActivity.bufferSize);
                    spectrogramActivity.surfaceView.drawThread.setBuffer(SpectrogramActivity.buffer);
                    //Log.e("meow", "buffer size is " + SpectrogramActivity.bufferSize + " and value is " + SpectrogramActivity.buffer[SpectrogramActivity.bufferSize-1]);
                }
            }
        };
        recordingThread.start();

        return;

    }


}