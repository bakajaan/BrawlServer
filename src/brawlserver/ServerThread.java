/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brawlserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 * 接続毎生成されるスレッド
 * クライアントからデータを受信し、
 * 他のスレッドから受信した情報を送信する
 *
 * @author bakaj
 */
public class ServerThread extends Thread {

    static Vector threads;
    Socket conn;
    int AX = 200;
    int AY = 300;
    int BX = -100;
    int BY = -100;
    int AT = 0;
    int BT = 0;
    int AH = 0;
    int BH = 0;
    char AHeading;
    char BHeading;
    char mode;

    /**
     * スレッドのコンストラクタ
     * スレッドの塊に自分を追加する
     *
     * @param s
     */
    public ServerThread(Socket s) {
        super();
        conn = s;
        //スレッドの塊を作成&自スレッドを追加
        if (threads == null || threads.isEmpty()) {
            threads = new Vector();
            mode = 'a';
        } else {
            mode = 'b';
        }
        threads.add(this);
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            System.err.println("*** Connected ***");
            //接続
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            out = new PrintWriter(conn.getOutputStream());
            out.println(mode);
            out.flush();
            while (true) {
                try {
                    //受信内容の読み取り
                    String a = in.readLine();
                    //先頭二文字によって受信内容を判断する。
                    switch (a.charAt(0)) {
                        //モーションタイプの受信&精査&送信
                        case 'T':
                            AT = Integer.parseInt(a.replace("T", ""));
                            talk(AT, 't');
                            break;
                        //向いている方向の受信&精査&送信
                        case 'H':
                            AH = Integer.parseInt(a.replace("H", ""));
                            talk(AH, 'h');
                            break;
                        //X座標の受信&精査&送信
                        case 'X':
                            AX = Integer.parseInt(a.replace("X", ""));
                            talk(AX, 'x');
                            break;
                        //Y座標の受信&精査&送信
                        case 'Y':
                            AY = Integer.parseInt(a.replace("Y", ""));
                            talk(AY, 'y');
                            break;
                        //終了処理
                        case 'C':
                            in.close();
                            out.close();
                            conn.close();
                            threads.remove(this);
                            System.err.println("*** Connection closed ***");
                            return;
                    }
                } catch (IOException | NumberFormatException e) {
                    //例外を受信したらスレッドを閉じる
                    in.close();
                    out.close();
                    conn.close();
                    threads.remove(this);
                    System.err.println("*** Connection closed ***");
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
            try {
                //例外を受信したらスレッドを閉じる
                in.close();
                out.close();
                conn.close();
                threads.remove(this);
            } catch (IOException ev) {
            }
        }
    }

    /**
     * 自分も含めすべてのスレッドにデータを送信
     *
     * @param a
     * @param xy
     */
    public void talk(int a, char xy) {
        for (int i = 0; i < threads.size(); i++) {
            //スレッド情報を取得
            ServerThread t = (ServerThread) threads.get(i);
            if (t.isAlive()) {
                //全てのスレッドに情報を送信
                t.talkone(this, a, xy);
            }
        }
    }

    public void talkone(ServerThread talker, int a, char xy) {
        PrintWriter out;
        try {
            out = new PrintWriter(conn.getOutputStream());
            if (talker == this) {
                //自分から送られてきたメッセージだった場合そのまま送信
                switch (xy) {
                    case 't':
                        out.println(BT);
                        break;
                    case 'h':
                        out.println(BH);
                        break;
                    case 'x':
                        out.println(BX);
                        break;
                    case 'y':
                        out.println(BY);
                        break;
                }
                out.flush();
            } else {
                //自分以外から送られてきたメッセージの場合代入
                switch (xy) {
                    case 't':
                        BT = a;
                        break;
                    case 'h':
                        BH = a;
                        break;
                    case 'x':
                        BX = a;
                        break;
                    case 'y':
                        BY = a;
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
