package brawlserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * サーバースレッド
 * 接続クライアント数を表示する
 *
 * @author bakaj
 */
public class PanelThread extends Thread {

    /**
     * クライアント分のスレッドを格納するリスト
     */
    List threadList = new ArrayList();
    /**
     * 接続数を表示する用のラベル
     */
    JLabel threadCountL;
    /**
     * 表示用パネル
     */
    JPanel serverP;

    public PanelThread(JFrame mainF) {
        serverP = new JPanel();
        serverP.setLayout(null);
        serverP.setBounds(0, 0, 400, 400);
        mainF.add(serverP);
        threadCountL = new JLabel("0");
        threadCountL.setBounds(0, 0, 400, 16);
        serverP.add(threadCountL);
    }

    public void addThread(Thread t) {
        threadList.add(t);
        threadCountL.setText("" + threadList.size());
        this.start();
    }

    @Override
    public void run() {
        while (threadList.size() > 0) {
            for (Iterator ite = threadList.iterator(); ite.hasNext();) {
                Thread t = (Thread) ite.next();
                if (!t.isAlive()) {
                    ite.remove();
                    threadCountL.setText("" + threadList.size());
                }
            }
        }
    }
}
