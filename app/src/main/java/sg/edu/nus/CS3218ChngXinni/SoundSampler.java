package sg.edu.nus.CS3218ChngXinni;

import android.media.AudioRecord;
import android.util.Log;


public class SoundSampler {

    private static final int  FS = 16000;     // sampling frequency
    public  AudioRecord       audioRecord;
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private Thread            recordingThread;
    public boolean doRecord = true;

    public SoundSampler(SoundActivity mAct) throws Exception
    {
        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            audioRecord = new AudioRecord(1, FS, nChannels, audioEncoding, AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding));
        }
        catch (Exception e) {
            Log.d("Error in SoundSampler(SoundActivity mAct) ", e.getMessage());
            throw new Exception();
        }

        return;

    }
    public void destroyAudioRecord() {

        try {
            if (recordingThread != null) {
                recordingThread.join();
            }
        } catch (Exception e) {

        }

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }

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

        SoundActivity.bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
        SoundActivity.buffer = new short[SoundActivity.bufferSize];

        audioRecord.startRecording();

        recordingThread = new Thread()
        {
            public void run()
            {
                while (doRecord)
                {

                    audioRecord.read(SoundActivity.buffer, 0, SoundActivity.bufferSize);
                    SoundActivity.surfaceView.drawThread.setBuffer(SoundActivity.buffer);

                }
            }
        };
        recordingThread.start();

        return;

    }


}


