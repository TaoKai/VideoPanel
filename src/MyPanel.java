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
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyPanel{
    MediaPlayer player;
    MediaView viewer;
    String media_path;
    String records_path;
    String progress_path;
    String[] videoNames;
    JLabel l_x;
    JLabel l_y;
    JLabel l_w;
    JLabel l_h;
    JLabel l_start;
    JLabel l_end;
    JFXDPanel vPanel;
    JProgressBar progressBar;
    JButton btnStart;
    JButton btnPause;
    JButton btnStop;
    JPanel recordPane;
    JRadioButton rbStart;
    JRadioButton rbEnd;
    JComboBox cbClasses;
    JButton btnRec;
    JButton btnNext;
    JFrame frame;
    JPanel ctrlPane;
    PlayerListener playerListener;
    int cur=0;
    public static void main(String[] args) throws Exception{
        MyPanel panel = new MyPanel();
        panel.initPanel();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private void resetVideo(MediaPlayer player, MultimediaInfo info){
        int vw = info.getVideo().getSize().getWidth();
        int vh = info.getVideo().getSize().getHeight();
        vPanel.setPreferredSize(new Dimension(vw, vh));
        ctrlPane.setPreferredSize(new Dimension(vw+100, 60));
        progressBar.setValue(0);
        progressBar.setString("00:00:00");
        progressBar.setPreferredSize(new Dimension(vw-20+100, 12));
        progressBar.addMouseMotionListener(new MouseCtrl(player, "progressBar"));
        progressBar.addMouseListener(new MouseCtrl(player, "progressBar"));
        btnStart.addMouseListener(new MouseCtrl(player, "btnStart"));
        btnPause.addMouseListener(new MouseCtrl(player, "btnPause"));
        btnStop.addMouseListener(new MouseCtrl(player, "btnStop"));
        this.recordPane.setPreferredSize(new Dimension(100, vh));
        this.rbStart.setSelected(false);
        this.rbEnd.setSelected(false);
        this.rbEnd.setEnabled(false);
        this.btnRec.setEnabled(false);
        vPanel.w = 0;
        vPanel.h = 0;
        vPanel.x = 0;
        vPanel.y = 0;
        vPanel.paint(vPanel.getGraphics());
        MouseCtrl.allowDraw = false;
        vPanel.start = 0;
        l_start.setText("00:00:00");
        vPanel.end = 0;
        l_end.setText("00:00:00");
        l_x.setText("x:0");
        l_y.setText("y:0");
        l_w.setText("w:0");
        l_h.setText("h:0");
        frame.setSize(vw+100, vh+60);
        playerListener.player = player;
        playerListener.totalTime = player.getTotalDuration();
    }

    public void initPanel() throws Exception{
        this.frame = new JFrame();
        frame.setTitle("视频标注");
        frame.setLocation(500, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        String basePath = System.getProperty("user.dir");
        this.media_path = basePath+"/resource/videos";
        this.videoNames = new File(this.media_path).list();
        this.progress_path = basePath+"/resource/.progress";
        BufferedReader br = new BufferedReader(new FileReader(this.progress_path));
        String pro = br.readLine().trim();
        br.close();
        for(String s:this.videoNames){
            if(!s.equals(pro)){
                this.cur += 1;
            }else{
                break;
            }
        }
        if(this.cur>=this.videoNames.length){
            this.cur = 0;
        }

        this.records_path = "resource/records.txt";
        Map ret = getVideo(this.media_path+"/"+this.videoNames[this.cur]);
        MultimediaInfo info = (MultimediaInfo) ret.get("info");
        this.player = (MediaPlayer) ret.get("player");
        this.viewer = (MediaView) ret.get("viewer");
        this.vPanel = (JFXDPanel) ret.get("panel");
        vPanel.fileName = media_path;
        vPanel.category = "软文";
        int vw = info.getVideo().getSize().getWidth();
        int vh = info.getVideo().getSize().getHeight();
        vPanel.setSize(vw, vh);
        this.ctrlPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));
        ctrlPane.setPreferredSize(new Dimension(vw+100, 60));
        this.progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setString("00:00:00");
        this.btnStart = new JButton("播放");
        this.btnPause = new JButton("暂停");
        this.btnStop = new JButton("停止");
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
        this.recordPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));
        this.recordPane.setPreferredSize(new Dimension(100, vh));
        this.rbStart = new JRadioButton("开始时间", false);
        this.rbEnd = new JRadioButton("结束时间", false);
        this.rbEnd.setEnabled(false);
        this.cbClasses = new JComboBox();
        cbClasses.addItem("二维码");
        cbClasses.addItem("求关注");
        cbClasses.addItem("各种号码");
        cbClasses.addItem("网址");
        this.btnRec = new JButton("记录标注");
        btnRec.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(btnRec.isEnabled()){
                    String cate = cbClasses.getSelectedItem().toString();
                    String rec = videoNames[cur]+" ["+vPanel.x+","+vPanel.y+","+vPanel.w+","+vPanel.h+"] ["+vPanel.start+" "+vPanel.end+"] "+cate+"\n";
                    try {
                        File f = new File(records_path);
                        FileOutputStream fos = new FileOutputStream(f, true);
                        fos.write(rec.getBytes());
                        fos.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    vPanel.w = 0;
                    vPanel.h = 0;
                    vPanel.x = 0;
                    vPanel.y = 0;
                    vPanel.paint(vPanel.getGraphics());
                    MouseCtrl.allowDraw = false;
                    rbEnd.setSelected(false);
                    rbEnd.setEnabled(false);
                    btnRec.setEnabled(false);
                    vPanel.start = 0;
                    l_start.setText("00:00:00");
                    vPanel.end = 0;
                    l_end.setText("00:00:00");
                    l_x.setText("x:0");
                    l_y.setText("y:0");
                    l_w.setText("w:0");
                    l_h.setText("h:0");
                    rbStart.setSelected(false);
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
        btnRec.setEnabled(false);
        btnRec.setPreferredSize(new Dimension(90, 30));
        this.btnNext = new JButton("下一个");
        l_start = new JLabel("00:00:00");
        l_start.setPreferredSize(new Dimension(90,15));
        l_end = new JLabel("00:00:00");
        l_end.setPreferredSize(new Dimension(90,15));
        l_x = new JLabel("x:0");
        l_y = new JLabel("y:0");
        l_w = new JLabel("w:0");
        l_h = new JLabel("h:0");
        rbStart.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean selected = rbStart.isSelected();
                if(selected){
                    Duration current = player.getCurrentTime();
                    vPanel.start = (long) current.toMillis();
                    String time_str = getTimeString(current);
                    l_start.setText(time_str);
                    MouseCtrl.allowDraw = true;
                    rbEnd.setEnabled(true);
                }else{
                    vPanel.w = 0;
                    vPanel.h = 0;
                    vPanel.x = 0;
                    vPanel.y = 0;
                    vPanel.paint(vPanel.getGraphics());
                    MouseCtrl.allowDraw = false;
                    rbEnd.setSelected(false);
                    rbEnd.setEnabled(false);
                    btnRec.setEnabled(false);
                    vPanel.start = 0;
                    l_start.setText("00:00:00");
                    vPanel.end = 0;
                    l_end.setText("00:00:00");
                    l_x.setText("x:0");
                    l_y.setText("y:0");
                    l_w.setText("w:0");
                    l_h.setText("h:0");
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
                        Duration current = player.getCurrentTime();
                        vPanel.end = (long) current.toMillis();
                        String time_str = getTimeString(current);
                        l_end.setText(time_str);
                    }else{
                        MouseCtrl.allowDraw = true;
                        btnRec.setEnabled(false);
                        vPanel.end = 0;
                        l_end.setText("00:00:00");
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
        btnNext.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cur += 1;
                player.stop();
                if(cur>=videoNames.length){
                    try{
                        FileOutputStream fos = new FileOutputStream(new File(progress_path), false);
                        fos.write("none.mp4".getBytes());
                        fos.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
                player.dispose();
                player = null;
                String path = media_path+"/"+videoNames[cur];
                try {
                    Map ret = getPlayerInfo(path);
                    MultimediaInfo info = (MultimediaInfo) ret.get("info");
                    player = (MediaPlayer) ret.get("player");
                    viewer.setMediaPlayer(player);
                    resetVideo(player, info);
                    player.play();
                } catch (Exception ex) {
                    ex.printStackTrace();
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
        vPanel.addMouseListener(new MouseCtrl(player, "vPanel", this));
        vPanel.addMouseMotionListener(new MouseCtrl(player, "vPanel", this));
        this.playerListener = new PlayerListener(player, progressBar);
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
                keyAction(e);
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
                keyAction(e);
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
                keyAction(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String name = videoNames[cur];
                try {
                    FileOutputStream fos = new FileOutputStream(new File(progress_path), false);
                    fos.write(name.getBytes());
                    fos.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public void keyAction(KeyEvent e){
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

    public static String getTimeString(Duration d){
        int m = (int) d.toMinutes();
        int s = (int) d.toSeconds()%60;
        int mm = (int) d.toMillis()%1000/10;
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

    private Map getPlayerInfo(String path) throws Exception{
        File video_source = new File(path);
        Encoder encoder = new Encoder();
        MultimediaInfo info = encoder.getInfo(video_source);
        Media m = new Media(video_source.toURI().toString());
        MediaPlayer player = new MediaPlayer(m);
        Map ret = new HashMap();
        ret.put("player", player);
        ret.put("info", info);
        return ret;
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
