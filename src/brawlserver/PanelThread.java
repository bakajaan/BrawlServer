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
    private final List threadList = new ArrayList();
    /**
     * 接続数を表示する用のラベル
     */
    private final JLabel threadCountL;
    /**
     * サーバー情報を表示する用のラベル
     */
    private final JLabel info01;

    public PanelThread(JFrame mainF) {
        JPanel serverP = new JPanel();
        serverP.setLayout(null);
        serverP.setBounds(0, 0, 400, 400);
        mainF.add(serverP);
        threadCountL = new JLabel("0");
        threadCountL.setBounds(0, 0, 400, 16);
        serverP.add(threadCountL);
        info01 = new JLabel("null");
        info01.setBounds(0, 16, 400, 16);
        serverP.add(info01);
    }

    @SuppressWarnings("unchecked")
    public void addThread(ServerThread t) {
        threadList.add(t);
        threadCountL.setText("" + threadList.size());
    }

    @Override
    public void run() {
        while (true) {
            info01.setText("");
            if (threadList.size() > 0) {
                for (Iterator ite = threadList.iterator(); ite.hasNext();) {
                    ServerThread t = (ServerThread) ite.next();
                    if (!t.isAlive()) {
                        ite.remove();
                        threadCountL.setText("" + threadList.size());
                    } else {
                        info01.setText(info01.getText() + t.getAX() + ":");
                    }
                }
            }
            try {
                Thread.sleep(12);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }
}
