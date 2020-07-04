

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hai on 2/13/17.
 */
public class SocketUtils {
    private static Log log = LogFactory.getLog(SocketUtils.class);

    /**
     * 发送指定命令到指定的服务器，获取返回结果
     *
     * @param SERVICE_PORT    服务器端口
     * @param SERVICE_ADDRESS 服务器地址
     * @param str             指定命令
     * @return
     */
    public String sendMsg(int SERVICE_PORT, String SERVICE_ADDRESS, String str) {
        log.info("ip:"+SERVICE_ADDRESS+";port:"+SERVICE_PORT+";cmd:"+str);
        String sb = "";
        Socket socket = null;
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVICE_ADDRESS, SERVICE_PORT)/*, 1000 * 6*/);//建立连接最多等待6s
//            socket.setSoTimeout(1000 * 60);

            pw = new PrintWriter(
                    new OutputStreamWriter(
                            socket.getOutputStream(), "UTF-8"), true);

            br = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(), "UTF-8"));

            pw.println(str);
            pw.flush();
            while ((sb = br.readLine()) != null) {
                log.info("result:"+sb);
                return sb;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pw != null) {
                pw.close();
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb;
        }
    }

}
