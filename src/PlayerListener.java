import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;

public class PlayerListener extends Thread {
    MediaPlayer player;
    String playerState;
    JProgressBar bar;
    Duration totalTime;
    public PlayerListener(MediaPlayer player, JProgressBar bar){
        this.player = player;
        this.playerState = "Stop";
        this.bar = bar;
        this.totalTime = player.getTotalDuration();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            if(this.playerState.equals("Playing")){
                try {
                    Thread.sleep(10);
                    Duration d = player.getCurrentTime();
                    int m = (int) d.toMinutes();
                    int s = (int) d.toSeconds()%60;
                    int mm = (int) d.toMillis()%1000/10;
                    String time_str = getTimeString(m, s, mm);
                    int progress = (int)Math.round(d.toMillis()/totalTime.toMillis()*100);
                    this.bar.setString(time_str);
                    this.bar.setValue(progress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(this.playerState.equals("Pause")){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getTimeString(int m, int s, int mm){
        String sm = ""+m;
        if(m<10){
            sm = "0"+sm;
        }
        String ss = ""+s;
        if(s<10){
            ss = "0"+ss;
        }
        String smm = ""+mm;
        if(mm<10){
            smm = "0"+smm;
        }
        return sm+":"+ss+":"+smm;
    }

    public void setPlayerState(String playerState) {
        this.playerState = playerState;
    }
}
