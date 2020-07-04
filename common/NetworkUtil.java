


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.md.common.util.StringUtil.isNotBlank;


public class NetworkUtil {
    private static Logger logger = LogManager.getLogger(NetworkUtil.class);
    private static final String networkPath = "/etc/sysconfig/network-scripts/";

    /**
     * 获取网卡信息
     *
     * @return 网卡列表
     */
    public static List getNetwork() {

        List<Network> rsList = new ArrayList();
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();

            NetworkInterface networkif;

            String name = "", ipv4 = "", ipv6 = "", mac = "", code = "", gate = "";
            int manager = 0;

            while (networks.hasMoreElements()) {

                networkif = networks.nextElement();

                name = networkif.getName();
                logger.debug(" name:" + name);
                if ("lo".equalsIgnoreCase(name) /*!networkif.isUp()*/) {
                    continue;
                }

                Network network = new Network();
                //ipv4 & ipv6
                for (Enumeration<InetAddress> i = networkif.getInetAddresses(); i.hasMoreElements(); ) {
                    InetAddress ia = i.nextElement();
                    logger.debug(" InetAddress:" + ia.getHostAddress());
                    if (ia instanceof Inet6Address) {
                        ipv6 = ia.getHostAddress();
                    }

                    if (ia instanceof Inet4Address) {
                        ipv4 = ia.getHostAddress();
                    }
                }

                //code
                List<InterfaceAddress> list = networkif.getInterfaceAddresses();
                for (InterfaceAddress ifa : list) {
                    InetAddress address = ifa.getAddress();
                    if (address.isLoopbackAddress() == true || address.getHostAddress().contains(":")) {
                        continue;
                    }
                    code = getMask(ifa.getNetworkPrefixLength());
                }

                gate = getGateway(name);
                mac = getMac(networkif);

                network.setName(name);
                network.setIpv4(ipv4);
                network.setIpv6(ipv6);
                network.setMac(mac);
                network.setCode(code);
                network.setGate(gate);
                network.setManager(manager);

                logger.info("ipv4:" + ipv4
                        + " ipv6:" + ipv6
                        + " name:" + name
                        + " mac:" + mac
                        + " gate:" + gate
                        + " manager:" + manager
                        + " code:" + code);

                rsList.add(network);
            }
        } catch (Exception e) {
            logger.error(e, e.fillInStackTrace());
        }
        return rsList;
    }


    /**
     * 更新网卡
     *
     * @param network
     * @return
     */
    public static int updateNetwork(Network network) {
        String name, code, gate, ip;
        PrintWriter printwriter = null;
        StringBuffer sb = new StringBuffer();

        name = network.getName();
        if (StringUtil.isEmpty(name)) {
            return 0;
        }

        //检查是否已有网卡的网段和待修改的相同
//        if (StringUtils.isNull(network.getIpv4()) || checkIp(network)) {
//            return 2;
//        }

        String path = networkPath + "ifcfg-" + name;
        logger.debug("path:" + path);
        File file = new File(path);
        try {
            if (!file.exists() && file.createNewFile()) {
                //文件不存在，新建文件成功
                code = isNotBlank(network.getCode()) ? network.getCode() : null;
                gate = isNotBlank(network.getGate()) ? network.getGate() : null;
                ip = isNotBlank(network.getIpv4()) ? network.getIpv4() : null;
                sb.append("DEVICE=" + name + "\n");
                sb.append("BOOTPROTO=static\n");
                sb.append("NM_CONTROLLED=\"yes\"\n");
                sb.append("ONBOOT=\"yes\"\n");
                sb.append("IPADDR=" + ip + "\n");
                sb.append("NETMASK=" + code + "\n");
                if (isNotBlank(network.getGate())) {
                    sb.append("GATEWAY=" + gate + "\n");
                }
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String rs;
                while ((rs = br.readLine()) != null && isNotBlank(rs)) {
//                    if(log.isDebugEnabled())log.debug("rs:"+rs);
//                    if(log.isDebugEnabled())log.debug("getCode:"+network.getCode());
//                    if(log.isDebugEnabled())log.debug("getIpv4:"+network.getIpv4());
//                    if(log.isDebugEnabled())log.debug("getGate:"+network.getGate());
//                    if(log.isDebugEnabled())log.debug("name:"+name);

                    if (rs.contains("GATEWAY") && !isNotBlank(network.getGate())) {
                        continue;
                    }
                    if (rs.contains("DEVICE") && isNotBlank(name)) {
                        sb.append("DEVICE=" + name + "\n");
                        continue;
                    } else if (rs.contains("NETMASK") && isNotBlank(network.getCode())) {
                        sb.append("NETMASK=" + network.getCode() + "\n");
                        continue;
                    } else if (rs.contains("IPADDR") && isNotBlank(network.getIpv4())) {
                        sb.append("IPADDR=" + network.getIpv4() + "\n");
                        continue;
                    } else if (rs.contains("GATEWAY") && isNotBlank(network.getGate())) {
                        sb.append("GATEWAY=" + network.getGate() + "\n");
                        continue;
                    } else {
                        sb.append(rs + "\n");
                    }
                }

                if (!sb.toString().contains("BOOTPROTO")) {
                    sb.append("BOOTPROTO=static\n");
                }
                if (!sb.toString().contains("NETMASK") && isNotBlank(network.getCode()))
                    sb.append("NETMASK=" + network.getCode() + "\n");

                if (!sb.toString().contains("IPADDR") && isNotBlank(network.getIpv4()))
                    sb.append("IPADDR=" + network.getIpv4() + "\n");

                if (!sb.toString().contains("GATEWAY") && isNotBlank(network.getGate()))
                    sb.append("GATEWAY=" + network.getGate() + "\n");

                if (!sb.toString().contains("DEVICE") && isNotBlank(name))
                    sb.append("DEVICE=" + name + "\n");
            }
            logger.debug(sb.toString());
            printwriter = new PrintWriter(new FileWriter(path, false));
            printwriter.println(sb.toString());
            printwriter.flush();


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        logger.info("/etc/init.d/network restart");
                        ApplicationUtils.exeCMD("/etc/init.d/network restart");
                    } catch (Exception e) {
                        logger.error(e, e.fillInStackTrace());
                    }
                }
            });
            thread.start();
            return 1;
        } catch (Exception e) {
            logger.error(e, e.fillInStackTrace());
        } finally {
            if (printwriter != null) {
                printwriter.close();
            }
        }
        return 0;
    }


    /**
     * 获取给定网卡名称的网关信息
     *
     * @return
     */
    private static String getGateway(String name) {
        String gate = "";
        String path = networkPath + "ifcfg-" + name;
        File file = new File(path);
        logger.debug("path:" + path);
        if (!file.exists()) {
            logger.info("file not exists");
            return gate;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String rs;
            while ((rs = br.readLine()) != null) {
//                log.debug("rs:" + rs);
                if (rs.contains("GATEWAY")) {
                    logger.debug(rs.substring(rs.indexOf("=") + 1));
                    return rs.substring(rs.indexOf("=") + 1);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e, e.fillInStackTrace());
        } catch (Exception e) {
            logger.error(e, e.fillInStackTrace());
        }
        return gate;
    }


    /**
     * 获取给定网卡的MAC地址
     *
     * @return
     */
    private static String getMac(NetworkInterface networkInterface) {
        StringBuffer sb = new StringBuffer();
        try {
            if (networkInterface.getHardwareAddress() != null) {
                // 获得MAC地址
                //结果是一个byte数组，每项是一个byte，我们需要通过parseByte方法转换成常见的十六进制表示
                byte[] addres = networkInterface.getHardwareAddress();
                if (addres != null && addres.length > 1) {
                    sb.append(parseByte(addres[0])).append(":").append(
                            parseByte(addres[1])).append(":").append(
                            parseByte(addres[2])).append(":").append(
                            parseByte(addres[3])).append(":").append(
                            parseByte(addres[4])).append(":").append(
                            parseByte(addres[5]));
                }
            }
        } catch (SocketException e) {
            logger.error(e, e.fillInStackTrace());
        }
        return sb.toString();
    }

    /**
     * 获取子网掩码
     *
     * @param length
     * @return
     */
    private static String getMask(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;
        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }
        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    /**
     * 格式化二进制
     *
     * @param b
     * @return
     */
    private static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
    }

}
