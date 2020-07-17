import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MyPanel{
    MediaPlayer player;
    MediaView viewer;
    public static void main(String[] args) throws Exception{
        MyPanel panel = new MyPanel();
        panel.initPanel();
    }

    public void initPanel() throws Exception{
        JFrame frame = new JFrame();
        frame.setTitle("视频标注");
        frame.setLocation(500, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        String media_path = "resource/tmp.mp4";
        Map ret = getVideo(media_path);
        MultimediaInfo info = (MultimediaInfo) ret.get("info");
        this.player = (MediaPlayer) ret.get("player");
        JFXDPanel vPanel = (JFXDPanel) ret.get("panel");
        vPanel.fileName = media_path;
        vPanel.category = "软文";
        int vw = info.getVideo().getSize().getWidth();
        int vh = info.getVideo().getSize().getHeight();
        vPanel.setSize(vw, vh);
        JPanel ctrlPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));
        ctrlPane.setPreferredSize(new Dimension(vw+100, 60));
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setString("00:00:00");
        JButton btnStart = new JButton("播放");
        JButton btnPause = new JButton("暂停");
        JButton btnStop = new JButton("停止");
        btnStart.addMouseListener(new MouseCtrl(player, "btnStart"));
        btnPause.addMouseListener(new MouseCtrl(player, "btnPause"));
        btnStop.addMouseListener(new MouseCtrl(player, "btnStop"));
        progressBar.setPreferredSize(new Dimension(vw-20+100, 12));
        progressBar.addMouseMotionListener(new MouseCtrl(player, "progressBar"));
        progressBar.addMouseListener(new MouseCtrl(player, "progressBar"));
        ctrlPane.add(progressBar);
        ctrlPane.add(btnStart);
        ctrlPane.add(btnPause);
//        ctrlPane.add(btnStop);
        JPanel recordPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));
        recordPane.setPreferredSize(new Dimension(100, vh));
        JRadioButton rbStart = new JRadioButton("开始时间", false);
        JRadioButton rbEnd = new JRadioButton("结束时间", false);
        rbEnd.setEnabled(false);
        JComboBox cbClasses = new JComboBox();
        cbClasses.addItem("软文");
        cbClasses.addItem("二维码");
        cbClasses.addItem("公众号");
        cbClasses.addItem("电话");
        cbClasses.addItem("求关注");
        JButton btnRec = new JButton("记录标注");
        btnRec.setEnabled(false);
        btnRec.setPreferredSize(new Dimension(90, 30));
        JButton btnNext = new JButton("下一个");
        JLabel l_start = new JLabel("00:00:00");
        l_start.setPreferredSize(new Dimension(90,15));
        JLabel l_end = new JLabel("00:00:00");
        l_end.setPreferredSize(new Dimension(90,15));
        JLabel l_x = new JLabel("x:0");
        JLabel l_y = new JLabel("y:0");
        JLabel l_w = new JLabel("w:0");
        JLabel l_h = new JLabel("h:0");
        rbStart.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean selected = rbStart.isSelected();
                if(selected){
                    long startMillis = (long) player.getCurrentTime().toMillis();
                    MouseCtrl.allowDraw = true;
                    rbEnd.setEnabled(true);
                }else{
                    vPanel.w = 0;
                    vPanel.h = 0;
                    vPanel.paint(vPanel.getGraphics());
                    MouseCtrl.allowDraw = false;
                    rbEnd.setSelected(false);
                    rbEnd.setEnabled(false);
                    btnRec.setEnabled(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        rbEnd.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(rbEnd.isEnabled()){
                    boolean selected = rbEnd.isSelected();
                    if(selected){
                        MouseCtrl.allowDraw = false;
                        btnRec.setEnabled(true);
                    }else{
                        MouseCtrl.allowDraw = true;
                        btnRec.setEnabled(false);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        recordPane.add(rbStart);
        recordPane.add(rbEnd);
        recordPane.add(cbClasses);
        recordPane.add(btnRec);
        recordPane.add(btnNext);
        recordPane.add(l_start);
        recordPane.add(l_end);
        recordPane.add(l_x);
        recordPane.add(l_y);
        recordPane.add(l_w);
        recordPane.add(l_h);
        frame.add(vPanel, BorderLayout.CENTER);
        frame.add(ctrlPane, BorderLayout.SOUTH);
        frame.add(recordPane, BorderLayout.EAST);
        frame.setSize(vw+100, vh+60);
        frame.setVisible(true);
        vPanel.addMouseListener(new MouseCtrl(player, "vPanel"));
        vPanel.addMouseMotionListener(new MouseCtrl(player, "vPanel"));
        PlayerListener playerListener = new PlayerListener(player, progressBar);
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                player.stop();
                player.seek(Duration.ZERO);
                MouseCtrl.clickButton = "stop";
            }
        });
        playerListener.setPlayerState("Playing");
        playerListener.start();
        player.play();
        btnStart.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyAction(e, player);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        btnPause.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyAction(e, player);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        vPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyAction(e, player);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void keyAction(KeyEvent e, MediaPlayer player){
        int code = e.getKeyCode();
        if(code != 37 && code != 39){
            return;
        }
        code = code-38;
        int total = (int) player.getTotalDuration().toMillis();
        int current = (int) player.getCurrentTime().toMillis();
        int step = 60;
        current += code*step;
        if(current<0){
            current = 0;
        }else if(current>total){
            current = total-1;
        }
        player.seek(Duration.millis(current));
    }

    private Map getVideo(String path) throws Exception{
        final JFXDPanel VFXPanel = new JFXDPanel();
        File video_source = new File(path);
        Encoder encoder = new Encoder();
        MultimediaInfo info = encoder.getInfo(video_source);
        Media m = new Media(video_source.toURI().toString());
        MediaPlayer player = new MediaPlayer(m);
        MediaView viewer = new MediaView(player);
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        // center video position
//        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
//        viewer.setX((screen.getWidth() - videoPanel.getWidth()) / 2);
//        viewer.setY((screen.getHeight() - videoPanel.getHeight()) / 2);

        // resize video based on screen size
//        DoubleProperty width = viewer.fitWidthProperty();
//        DoubleProperty height = viewer.fitHeightProperty();
//        width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
//        height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));
//        viewer.setPreserveRatio(true);

        // add video to stackpane
        root.getChildren().add(viewer);
        VFXPanel.setScene(scene);
        Map ret = new HashMap();
        ret.put("info", info);
        ret.put("player", player);
        ret.put("panel", VFXPanel);
        ret.put("viewer", viewer);
        return ret;
    }
}
