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

    //<editor-fold defaultstate="collapsed" desc="メンバ">
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
    int BX = 200;
    /**
     * 敵Y座標
     */
    int BY = 300;
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
    int AH = 1;
    /**
     * 敵の向いている向き
     * 0:動いていない　1:右　2:左
     */
    int BH = 2;
    /**
     * 自分のモード
     */
    char mode;
    /**
     * データ受信用リーダー
     */
    BufferedReader in;
    /**
     * データ送信用ライター
     */
    PrintWriter out;
//</editor-fold>

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
        try {
            //接続
            System.out.println("*** Connected ***");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            out = new PrintWriter(conn.getOutputStream());
            out.println(mode);
            out.flush();
            while (true) {
                //受信内容の読み取り
                String receiveT = in.readLine();
                if (receiveT.charAt(0)== 'C') {
                    in.close();
                    out.close();
                    conn.close();
                    threads.remove(this);
                    System.out.println("*** Connection closed ***");
                    break;
                } else {
                    AT = Integer.parseInt(receiveT.substring(
                            receiveT.indexOf("T") + 1, receiveT.indexOf("t")));
                    AH = Integer.parseInt(receiveT.substring(
                            receiveT.indexOf("H") + 1, receiveT.indexOf("h")));
                    AX = Integer.parseInt(receiveT.substring(
                            receiveT.indexOf("X") + 1, receiveT.indexOf("x")));
                    AY = Integer.parseInt(receiveT.substring(
                            receiveT.indexOf("Y") + 1, receiveT.indexOf("y")));
                    String sendT = ""
                            + "T" + AT + "t"
                            + "H" + AH + "h"
                            + "X" + AX + "x"
                            + "Y" + AY + "y";
                    talk(sendT);
                }
            }
            in.close();
            out.close();
            conn.close();
            threads.remove(this);
            System.err.println("*** Connection closed ***");
        } catch (IOException | NumberFormatException e) {
            System.out.println(e);
            //例外を受信したらスレッドを閉じる
            try {
                in.close();
                out.close();
                conn.close();
                threads.remove(this);
                System.err.println("*** Connection closed ***");
            } catch (IOException ev) {
                System.out.println(ev);
            }
        }
    }

    /**
     * 全てのスレッドのtalkoneメソッドにデータを送信
     *
     * @param text
     */
    public void talk(String text) {
        for (int i = 0; i < threads.size(); i++) {
            //スレッド情報を取得
            ServerThread t = (ServerThread) threads.get(i);
            if (t.isAlive()) {
                //全てのスレッドに情報を送信
                t.talkone(this, text);
            }
        }
    }

    /**
     * スレッドから受信したデータを処理
     *
     * @param talker　スレッドの判別ID
     * @param receiveT
     */
    public void talkone(ServerThread talker, String receiveT) {
        if (talker == this) {
            String sendT = ""
                    + "T" + BT + "t"
                    + "H" + BH + "h"
                    + "X" + BX + "x"
                    + "Y" + BY + "y";
            out.println(sendT);
            out.flush();
        } else {
            //自分以外から送られてきたメッセージの場合代入
            BT = Integer.parseInt(receiveT.substring(
                    receiveT.indexOf("T") + 1, receiveT.indexOf("t")));
            BH = Integer.parseInt(receiveT.substring(
                    receiveT.indexOf("H") + 1, receiveT.indexOf("h")));
            BX = Integer.parseInt(receiveT.substring(
                    receiveT.indexOf("X") + 1, receiveT.indexOf("x")));
            BY = Integer.parseInt(receiveT.substring(
                    receiveT.indexOf("Y") + 1, receiveT.indexOf("y")));
        }
    }
}
