import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class EnviromentPanel extends JPanel {
    public EnviromentPanel() {
        super();
//        revalidate();
//        repaint();
    }

    public void paint(Graphics g)
    {
        g.drawImage(Environment.currentEnvironment.buffer, 0, 0, null);
    }

    public void setVisible(boolean aFlag) {
        super.setVisible(true);
    }
}
