import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseCtrl implements MouseListener, MouseMotionListener {
    MediaPlayer player;
    String ctrlName;
    static String clickButton = "play";
    static boolean allowDraw = false;
    MyPanel panel;
    public MouseCtrl(MediaPlayer player, String ctrlName){
        this.player = player;
        this.ctrlName = ctrlName;
    }

    public MouseCtrl(MediaPlayer player ,String ctrlName, MyPanel panel){
        this.panel = panel;
        this.player = player;
        this.ctrlName = ctrlName;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(this.ctrlName.equals("btnStart")){
            player.play();
            MouseCtrl.clickButton = "play";
        }else if(this.ctrlName.equals("btnStop")){
            player.stop();
            player.seek(Duration.ZERO);
            MouseCtrl.clickButton = "stop";
        }else if(this.ctrlName.equals("btnPause")){
            player.pause();
            MouseCtrl.clickButton = "pause";
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(this.ctrlName.equals("vPanel")){
            if(allowDraw){
                JFXDPanel panel = (JFXDPanel) e.getComponent();
                panel.w = 0;
                panel.h = 0;
                int x = e.getPoint().x;
                int y = e.getPoint().y;
                panel.x = x;
                panel.y = y;
                panel.paint(panel.getGraphics());
            }
        }else if(this.ctrlName.equals("progressBar")){
            if(MouseCtrl.clickButton.equals("play")){
                this.player.pause();
            }
            JProgressBar bar = (JProgressBar)e.getComponent();
            int x = e.getPoint().x;
            int width = bar.getWidth();
            if(x<0){
                x = 0;
            }else if(x>width){
                x = width;
            }
            double total = this.player.getTotalDuration().toMillis();
            double seekTime = total*x/width;
            this.player.seek(Duration.millis(seekTime));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(this.ctrlName.equals("progressBar")){
            if(MouseCtrl.clickButton.equals("play")){
                this.player.play();
            }
        }else if(this.ctrlName.equals("vPanel")){
            JFXDPanel vPanel = (JFXDPanel) e.getComponent();
            int x = vPanel.x;
            int y = vPanel.y;
            int w = vPanel.w;
            int h = vPanel.h;
            if(w>0 && h>0){
                this.panel.l_x.setText("x:"+x);
                this.panel.l_y.setText("y:"+y);
                this.panel.l_w.setText("w:"+w);
                this.panel.l_h.setText("h:"+h);
            }else{
                vPanel.x = 0;
                vPanel.y = 0;
                vPanel.w = 0;
                vPanel.h = 0;
                this.panel.l_x.setText("x:"+0);
                this.panel.l_y.setText("y:"+0);
                this.panel.l_w.setText("w:"+0);
                this.panel.l_h.setText("h:"+0);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(this.ctrlName.equals("vPanel")){
            if(allowDraw){
                JFXDPanel panel = (JFXDPanel) e.getComponent();
                int x = e.getPoint().x;
                int y = e.getPoint().y;
                int w = x-panel.x;
                int h = y-panel.y;
                if(w<0 || h<0){
                    w = 0;
                    h = 0;
                }
                panel.w = w;
                panel.h = h;
                panel.paint(panel.getGraphics());
            }
        }else if(this.ctrlName.equals("progressBar")){
            JProgressBar bar = (JProgressBar)e.getComponent();
            int x = e.getPoint().x;
            int width = bar.getWidth();
            if(x<0){
                x = 0;
            }else if(x>width){
                x = width;
            }
            double total = this.player.getTotalDuration().toMillis();
            double seekTime = total*x/width;
            this.player.seek(Duration.millis(seekTime));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
