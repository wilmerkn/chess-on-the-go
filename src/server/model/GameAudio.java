package server.model;
import javax.sound.sampled.*;
import java.io.File;


public class GameAudio extends Thread {

    //Clip for the background music
    private Clip backgroundMusicClip;
    //boolean used to start / stop the thread
    private boolean isRunning = true;


    //method used to play sound effects
    public void playSound(String soundFile, float volumeControl) {
        try {
            File file = new File(soundFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(volumeControl);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //play background music
    public void run(){
        try {
            File file = new File("src\\AudioFiles\\mixkit-ambient-251.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.toURI().toURL());
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);
            FloatControl floatControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(-25.0f);
        }catch (Exception e) {
            e.printStackTrace();
        }

        while(isRunning) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip.start();
        }
    }
}
