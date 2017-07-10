package brawlserver;

import static javax.swing.JFrame.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 * サーバークラス
 * 接続を待ち、接続されるとスレッドを生成する
 *
 * @author bakaj
 */
public class BrawlServer {

    /**
     * 解放するポート番号
     */
    static int port = 7788;

    /**
     * メインメソッド
     *
     * @param args
     */
    public static void main(String[] args) {
        //フレームの作成
        JFrame mainF = new JFrame();
        mainF.setBounds(0, 0, 400, 400);
        mainF.setVisible(true);
        mainF.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainF.setLayout(null);
        PanelThread panelT = new PanelThread(mainF);
        panelT.start();
        try {
            //サーバー準備
            ServerSocket server = new ServerSocket(port);
            Socket conn = null;
            System.err.println("Ready");
            while (true) {
                try {
                    //接続を待つ
                    conn = server.accept();
                    //スレッド生成
                    ServerThread t = new ServerThread(conn);
                    //スレッド実行
                    t.start();
                    panelT.addThread(t);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
