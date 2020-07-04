

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ApplicationUtils : 程序工具类，提供大量的便捷方法
 */
public class ApplicationUtils {

    /**
     * 产生一个36个字符的UUID
     *
     * @return UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * md5加密
     *
     * @param value 要加密的值
     * @return md5加密后的值
     */
    public static String md5Hex(String value) {
        return DigestUtils.md5Hex(value);
    }

    /**
     * sha1加密
     *
     * @param value 要加密的值
     * @return sha1加密后的值
     */
    public static String sha1Hex(String value) {
        return DigestUtils.sha1Hex(value);
    }

    /**
     * sha256加密
     *
     * @param value 要加密的值
     * @return sha256加密后的值
     */
    public static String sha256Hex(String value) {
        return DigestUtils.sha256Hex(value);
    }

    /**
     * 方法名： exeCMDForTopu<br>
     * 描述  ： 拓扑图数据解析的命令执行方法 <br>
     * <br>
     *
     * @param cmd <br>
     * @return java.util.List<java.lang.StringBuffer><br>
     */
    public static List<StringBuffer> exeCMDForTopu(String cmd) {
        if (cmd == null || cmd.equals("")) return null;
        BufferedReader br = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        Process p = null;
        List<StringBuffer> list = new ArrayList<>();
        int wiresharkLineStrLength = 66; //正常一行的字符数
        try {
            Runtime runtime = Runtime.getRuntime();
            String[] cmds = {"/bin/bash", "-c", cmd};
            p = runtime.exec(cmds);
            inputStream = p.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            br = new BufferedReader(inputStreamReader);

            StringBuffer buffer = new StringBuffer();
            String readLine;
            int start_index = 20;
            while ((readLine = br.readLine()) != null) {
                try {
                    if (readLine.contains("->")) {
                        continue;
                    }
                    if (readLine.startsWith("0000")) {
                        if (buffer.length() > 0) {
                            buffer = new StringBuffer(buffer.substring((start_index - 16) * 2));
                            list.add(buffer);
                        }
                        String length = readLine.substring(12, 17).replaceAll(" ", "");
                        start_index = StringUtil.getNumberForMachineSequence(length);
                    } else if (readLine.startsWith("0010")) {
                        buffer = new StringBuffer();
                        readLine = readLine.substring(readLine.indexOf("  ")).trim();
                        if (readLine.length() != wiresharkLineStrLength) {
                            readLine = readLine.substring(0, readLine.length() * 2 / 3);
                        } else {
                            readLine = readLine.substring(0, readLine.length() * 2 / 3 + 3);
                        }
                        buffer.append(readLine.trim().replaceAll(" ", ""));
                    } else if (readLine.length() > 0) {
                        readLine = readLine.substring(readLine.indexOf("  ")).trim();
                        if (readLine.length() != wiresharkLineStrLength) {
                            readLine = readLine.substring(0, readLine.length() * 2 / 3);
                        } else {
                            readLine = readLine.substring(0, readLine.length() * 2 / 3 + 3);
                        }
                        buffer.append(readLine.trim().replaceAll(" ", ""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (buffer.length() > 0) {
                list.add(buffer);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
            }
        }
    }


    /**
     * 本地执行 sh 命令
     *
     * @param cmd shell命令
     * @return 终端内容
     */
    public static StringBuffer exeCMD(String cmd) {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bfRd = null;
        Process process = null;
        try {
            String[] cmdArray = {"/bin/bash", "-c", cmd};
            process = Runtime.getRuntime().exec(cmdArray);
            bfRd = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = bfRd.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            process.destroy();
        } catch (Exception e) {
            System.err.println("Linux erro ------------ :" + cmd);
            return null;
        } finally {
            try {
                if (bfRd != null) bfRd.close();
                if (process != null) process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    public static int exeNmap(String ip, String type, int port) {
        //TCP: nmap -p 端口 IP
        //UDP: nmap -sU 端口 IP
        //ICMP: nmap -sP IP
        String exe = "";
        if (type.equals("TCP")) {
            exe = String.format("nmap %s -p %d", ip, port);
        } else if (type.equals("UDP")) {
            exe = String.format("nmap -sU %s -p %d", ip, port);
        } else if (type.equals("Ping")) {
            exe = String.format("nmap -sP %s", ip);
            StringBuffer sb = ApplicationUtils.exeCMD(exe);
            if (sb != null) {
                return sb.indexOf("MAC Address") > -1 ? 1 : 0;
            }
        } else {
            return 0;
        }
        System.out.println("exeNmap :" + exe);

        StringBuffer sb = ApplicationUtils.exeCMD(exe);
        if (sb != null) {
            return sb.indexOf("closed") > -1 ? 0 : 1;
        }
        return 0;
    }
}

