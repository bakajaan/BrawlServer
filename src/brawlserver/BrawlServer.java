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
     * メインメソッド
     *
     * @param args
     */
    public static void main(String[] args) {

        /**
         * 解放するポート番号
         */
        int port = 7788;
        
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
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
