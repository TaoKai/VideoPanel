import javafx.embed.swing.JFXPanel;

import java.awt.*;

public class JFXDPanel extends JFXPanel {
    String fileName="";
    String category="";
    int x=0;
    int y=0;
    int w=0;
    int h=0;
    long start=0;
    long end=0;
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g = (Graphics2D)g;
        g.setColor(new Color(255,0,0));
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        if(w>0 && h>0){
            g.drawRect(this.x, this.y, this.w, this.h);
        }
    }
}
