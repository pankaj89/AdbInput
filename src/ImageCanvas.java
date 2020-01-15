import java.awt.*;

public class ImageCanvas extends Canvas {
    @Override
    public void paint(Graphics g) {
        Toolkit t=Toolkit.getDefaultToolkit();
        Image i=t.getImage("/icons/adbicon.png");
        g.drawImage(i, 0,0,this);
    }
}
