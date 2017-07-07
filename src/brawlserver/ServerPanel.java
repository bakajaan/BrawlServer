package brawlserver;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author bakaj
 */
public class ServerPanel extends JPanel {
    
    int threadCount = 0;
    List threadList = new ArrayList();
    JLabel threadCountL;
    
    public ServerPanel() {
        this.setLayout(null);
        this.setBounds(0, 0, 400, 400);
        this.setVisible(true);
        threadCountL = new JLabel("" + threadCount);
        threadCountL.setBounds(0, 0, 200, 16);
        this.add(threadCountL);
    }
    
    public void addThread(Thread t) {
        threadList.add(t);
        threadCountL.setText("" + threadList.size());
    }
}
