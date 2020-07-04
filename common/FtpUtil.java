

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.TimeZone;


public class FtpUtil {
   private FTPClient ftpClient;
   private final static Logger log = LoggerFactory.getLogger(FtpUtil.class);

   public FtpUtil() {
       super();
       // 从配置文件中读取初始化信息
       this.ftpClient = new FTPClient();
   }

   /**
    * 连接并登录FTP服务器
    */
   public boolean ftpLogin(String url, String account, String password, Integer port, String remoteDir) {
       boolean isLogin = false;
       log.info("url:" + url + " account:" + account + "password:" + password + "port:" + port + "remoteDir:" + remoteDir);
       FTPClientConfig ftpClientConfig = new FTPClientConfig(
               FTPClientConfig.SYST_NT);
       ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
       this.ftpClient.setControlEncoding("UTF-8");

       this.ftpClient.configure(ftpClientConfig);
       try {
           if (port > 0) {
               this.ftpClient.connect(url, port);
           } else {
               this.ftpClient.connect(url);
           }
           // FTP服务器连接回答
           int reply = this.ftpClient.getReplyCode();
           if (!FTPReply.isPositiveCompletion(reply)) {
               this.ftpClient.disconnect();
               return isLogin;
           }
           this.ftpClient.login(account, password);
           if (this.ftpClient.listNames()!=null){
               this.ftpClient.changeWorkingDirectory(remoteDir);
               this.ftpClient.setFileType(FTPClient.FILE_STRUCTURE);
               isLogin = true;
           }
       } catch (SocketException e) {
           e.printStackTrace();

       } catch (IOException e) {
           e.printStackTrace();
       }
       System.out.println(this.ftpClient.getBufferSize());
       this.ftpClient.setBufferSize(1024 * 2);
       this.ftpClient.setDataTimeout(2000);
       return isLogin;
   }

   /**
    * 退出并关闭FTP连接
    */
   public void close() {
       if (null != this.ftpClient && this.ftpClient.isConnected()) {
           try {
               this.ftpClient.logout();// 退出FTP服务器

           } catch (IOException e) {
               e.printStackTrace();
           } finally {
               try {
                   this.ftpClient.disconnect();// 关闭FTP服务器的连接
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }

   /**
    * 检查FTP服务器是否关闭 ，如果关闭接则连接登录FTP
    *
    * @return
    */
   public boolean isOpenFTPConnection() {
       boolean isOpen = false;
       if (null == this.ftpClient) {
           return false;
       }
       try {
           // 没有连接
           if (!this.ftpClient.isConnected()) {
               isOpen = this.ftpLogin(AppConfig.PAD_FTP_URL, AppConfig.PAD_FTP_ACCOUNT, AppConfig.PAD_FTP_PASSWORD, AppConfig.PAD_FTP_PORT, AppConfig.PAD_FTP_REMOTE_DIR);
           }
       } catch (Exception e) {
           e.printStackTrace();
           isOpen = false;
       }
       return isOpen;
   }

   /**
    * 设置传输文件的类型[文本文件或者二进制文件]
    *
    * @param fileType--FTPClient.BINARY_FILE_TYPE,FTPClient.ASCII_FILE_TYPE
    */
   public void setFileType(int fileType) {
       try {
           this.ftpClient.setFileType(fileType);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   /**
    * 根据路径删除ftp服务器文件
    *
    * @param filepath 文件路径
    */
   public void deleteFileByPath(String filepath) {
       try {
           this.ftpClient.deleteFile(filepath);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   /**
    * 下载文件
    *
    * @param localFilePath  本地文件名及路径
    * @param remoteFileName 远程文件名称
    * @return
    */
   public boolean downloadFile(String localFilePath, String remoteFileName) {
       BufferedOutputStream outStream = null;
       boolean success = false;
       try {
           outStream = new BufferedOutputStream(new FileOutputStream(
                   localFilePath));
           success = this.ftpClient.retrieveFile(remoteFileName, outStream);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if (outStream != null) {
               try {
                   outStream.flush();
                   outStream.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return success;
   }

   /**
    * 下载文件
    *
    * @param localFile      本地文件
    * @param remoteFileName 远程文件名称
    * @return
    */
   public boolean downloadFile(File localFile, String remoteFileName) {
       BufferedOutputStream outStream = null;
       FileOutputStream outStr = null;
       boolean success = false;
       try {
           outStr = new FileOutputStream(localFile);
           outStream = new BufferedOutputStream(outStr);
           success = this.ftpClient.retrieveFile(remoteFileName, outStream);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               if (null != outStream) {
                   try {
                       outStream.flush();
                       outStream.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               if (null != outStr) {
                   try {
                       outStr.flush();
                       outStr.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }

               }
           }
       }
       return success;
   }

   /**
    * 上传文件
    *
    * @param localFilePath  本地文件路径及名称
    * @param remoteFileName FTP 服务器文件名称
    * @return
    */
   public boolean uploadFile(String localFilePath, String remoteFileName) {
       BufferedInputStream inStream = null;
       boolean success = false;
       try {
           inStream = new BufferedInputStream(new FileInputStream(
                   localFilePath));
           success = this.ftpClient.storeFile(remoteFileName, inStream);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if (inStream != null) {
               try {
                   inStream.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return success;
   }

   /**
    * 上传文件
    *
    * @param localFile      本地文件
    * @param remoteFileName FTP 服务器文件名称
    * @return
    */
   public boolean uploadFile(File localFile, String remoteFileName) {
       BufferedInputStream inStream = null;
       boolean success = false;
       try {
           inStream = new BufferedInputStream(new FileInputStream(localFile));
           success = this.ftpClient.storeFile(remoteFileName, inStream);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if (inStream != null) {
               try {
                   inStream.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return success;
   }

   /**
    * 变更工作目录
    *
    * @param remoteDir--目录路径
    */
   public void changeDir(String remoteDir) {
       try {
           this.ftpClient.changeWorkingDirectory(remoteDir);
       } catch (IOException e) {
           e.printStackTrace();
       }

   }

   /**
    * 变更工作目录
    */
   public void changeDir(String[] remoteDirs) {
       String dir = "";
       try {
           for (int i = 0; i < remoteDirs.length; i++) {
               this.ftpClient.changeWorkingDirectory(remoteDirs[i]);
               dir = dir + remoteDirs[i] + "/";
           }

       } catch (IOException e) {
           e.printStackTrace();
       }

   }

   /**
    * 返回上级目录
    */
   public void toParentDir(String[] remoteDirs) {
       try {
           for (int i = 0; i < remoteDirs.length; i++) {
               this.ftpClient.changeToParentDirectory();
           }

       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   /**
    * 返回上级目录
    */
   public void toParentDir() {
       try {
           this.ftpClient.changeToParentDirectory();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   /**
    * 获得FTP 服务器下所有的文件名列表
    */
   public String[] getListFiels() {
       String files[] = null;
       try {
           files = this.ftpClient.listNames();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return files;
   }

   public FTPClient getFtpClient() {
       return ftpClient;
   }

}
