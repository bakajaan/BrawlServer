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

    /**
     * スレッドの塊
     */
    static Vector threads;
    /**
     * 通信用ソケット
     */
    Socket conn;
    /**
     * 自分X座標
     */
    int AX = 200;
    /**
     * 自分Y座標
     */
    int AY = 300;
    /**
     * 敵X座標
     */
    int BX = -100;
    /**
     * 敵Y座標
     */
    int BY = -100;
    /**
     * 自分の動きのタイプ
     * 0-2:歩き　3:攻撃　4:ジャンプ　5:死亡
     */
    int AT = 0;
    /**
     * 敵の動きのタイプ
     * 0-2:歩き　3:攻撃　4:ジャンプ　5:死亡
     */
    int BT = 0;
    /**
     * 自分の向いている向き
     * 0:動いていない　1:右　2:左
     */
    int AH = 0;
    /**
     * 敵の向いている向き
     * 0:動いていない　1:右　2:左
     */
    int BH = 0;
    /**
     * 自分のモード
     */
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
     * 全てのスレッドのtalkoneメソッドにデータを送信
     *
     * @param a　数値
     * @param xy　タイプ
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

    /**
     * スレッドから受信したデータを処理
     *
     * @param talker　スレッドの判別ID
     * @param a　数値
     * @param xy　タイプ
     */
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
