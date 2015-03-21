package sg.edu.nus.CS3218ChngXinni;

import android.media.AudioRecord;
import android.util.Log;


public class SoundSamplerSpectrogram {
    private static final int  FS = 16000;     // sampling frequency
    public AudioRecord audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private SpectrogramActivity   SpectrogramActivity;
    private Thread            recordingThread;



    public SoundSamplerSpectrogram(SpectrogramActivity mAct) throws Exception
    {

        SpectrogramActivity = mAct;

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
                    SpectrogramActivity.surfaceView.drawThread.setBuffer(SpectrogramActivity.buffer);

                }
            }
        };
        recordingThread.start();

        return;

    }


}
