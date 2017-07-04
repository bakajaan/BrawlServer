/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brawlserver;

import static javax.swing.JFrame.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author bakaj
 */
public class BrawlServer {

    static int port = 7788;

    public static void main(String[] args) {
        //フレームの作成
        JFrame mainF = new JFrame();
        mainF.setBounds(0, 0, 100, 100);
        mainF.setVisible(true);
        mainF.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //ラベルの追加
        JLabel label1 = new JLabel("a");
        mainF.add(label1);
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
                    //label1.setText();
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
