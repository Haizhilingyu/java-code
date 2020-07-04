/**
 * FileUtil.java
 *
 * Copyright(C)2008 Founder Corporation.
 * written by Founder Corp.
 */

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * [类名]<br>
 * FileUtil<br>
 * [功能概要]<br>
 * <br>
 * <br>
 * [変更履歴]<br>
 * 2009-3-16 ver1.00 新建 zhaoxinsheng<br>
 * 2010-08-05 ver1.00 加入了extName函数 guoyuxi<br>
 *
 * @author FOUNDER CORPORATION
 * @version 1.00
 */
public class FileUtil {

    /**
     * 日志打印对象
     */
    private static final Log logger = LogFactory.getLog(FileUtil.class);

    // 删除整个目录，包括子目录 方法
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] filelist = file.listFiles();
                for (File subfile : filelist) {
                    deleteFile(subfile.getAbsolutePath());
                }
            }
            file.delete();
        }
    }

    /**
     * 删除目录下所有文件,不包含文件夹
     * @param file
     */
    public static void deleteAll(File file){

        if(file.isFile() || file.list().length ==0)
        {
            return;
        }else{
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    /**
     * 按后缀删除文件
     * @param filepath 路径
     * @param suffix 后缀名
     */
    public static void deleteFileBySuffix(String filepath,String suffix){
        logger.info("删除"+suffix+"文件");
        File file=new File(filepath);//里面输入特定目录
        File temp;
        File[] filelist= file.listFiles();
        for(int i=0;i<filelist.length;i++)
        {temp=filelist[i];
            if(temp.getName().endsWith(suffix))//获得文件名，如果后缀为suffix 就删除文件
            {
                temp.delete();//删除文件
            }
        }
    }

    /**
     * 自己写文件，一解决在linux中renameTo不能工作的问题
     *
     * @param f
     * @param newFile
     * @throws IOException
     */
    public static void writeTo(File f, File newFile) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        FileOutputStream fos = new FileOutputStream(newFile);
        try {
            byte[] buf = new byte[8192];
            do {
                int rc = fis.read(buf);
                if (rc == -1)
                    break;
                fos.write(buf, 0, rc);
            } while (true);
        } finally {
            fis.close();
            fos.close();
        }
    }

    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static void copyFile(File src, File dst, String charset){
    	if (!src.getAbsolutePath().equalsIgnoreCase(dst.getAbsolutePath())) {
    		BufferedReader in = null;
    		BufferedWriter out = null;
    		try {
    			in = new BufferedReader(new FileReader(src),1024*5);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {

				out  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst),charset),1024*5);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			char[] buf = new char[1024 * 5];
            int len = 0;
            try {
				while ((len = in.read(buf)) > 0) {
				    out.write(buf, 0, len);
				    out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

    	}
    }

    /**
     * 读取文本文件内容
     * @param filePath
     * @return
     */
    public static StringBuffer readFile(String filePath){
        StringBuffer sb = new StringBuffer();
        try {
            File file=new File(filePath);
            //判断文件是否存在
            if(!file.isFile() || !file.exists()) {
                System.out.println("找不到指定的文件:"+filePath);
                return null;
            }

            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file));//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                sb.append(lineTxt+"\n");
            }
            read.close();
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return sb;
    }

    // 复制文件
    public static boolean copyFile(File src, File dst) throws IOException {
        // if the parameters are same,then don't excute anything.or it make original file null.
        if (!src.getAbsolutePath().equalsIgnoreCase(dst.getAbsolutePath())) {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024 * 5];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
             out.flush();
            // in.close();
            out.close();
        }
        return true;
    }

    // 上传文件
    public static boolean upFile(InputStream in, String TargetPath) throws FileNotFoundException, IOException {
        // if the parameters are same,then don't excute anything.or it make original file null.
        File targetfile = new File(TargetPath);

        if (!targetfile.exists())
            makeFile(TargetPath);
        OutputStream out = new FileOutputStream(TargetPath);
        try {
            System.out.println("=upFile begin transfer file=");
            // Transfer bytes from in to out
            byte[] buf = new byte[1024 * 5];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
            throw e;
        } finally {
            in.close();
            out.close();
        }

        return true;
    }

    // 删除文件
    public static boolean delFileByName(String file) throws Exception {
        File f = new File(file);

        // File fB = new File(file1);
        if (f.exists()) {
            logger.info("begin delete the file==>" + file + "  from the file system!");
            f.delete();
        } else {
            logger.info(file + "not in the file system!");
        }

        return true;
    }

    /**
     * 创建目录 不存在时创建。已存在目录时，直接返回。
     *
     * @param folderPath :目录路径
     * @return
     * @throws IOException
     */
    public static boolean createFolder(String folderPath) throws IOException {
        boolean result = false;
        File f = new File(folderPath);
        if (!f.isDirectory())
            result = f.mkdirs();
        return result;
    }

    /**
     * 删除目录下所有文件
     *
     * @param directory (File 对象)
     */
    public static boolean emptyDirectory(File directory) {
        File[] entries = directory.listFiles();
        for (int i = 0; i < entries.length; i++) {
            entries[i].delete();
        }
        return true;
    }

    /**
     * 创建文件
     *
     * @param filepath :文件所在目录路径,比如:c:/test/test.txt test目录必须存在。
     * @return
     */
    public static boolean makeFile(String filepath) throws IOException {
        boolean result = false;
        File file = new File(filepath);

        result = file.createNewFile();

        file = null;
        return result;
    }

    /**
     * 删除文件
     *
     * @param filepath :文件所在物理路径
     * @return
     */
    public static boolean isDel(String filepath) {
        boolean result = false;
        File file = new File(filepath);
        result = file.delete();
        file = null;
        return result;
    }

    /**
     * 文件重命名
     *
     * @param filepath :文件所在物理路径
     * @param destname :新文件名
     * @return
     */
    public static boolean renamefile(String filepath, String destname) {
        boolean result = false;
        File f = new File(filepath);
        String fileParent = f.getParent();
        String filename = f.getName();
        File rf = new File(fileParent + File.separator + destname);
        if (f.renameTo(rf)) {
            result = true;
        }
        f = null;
        rf = null;
        return result;
    }

    /**
     * 将文件内容写入文件中
     *
     * @param filepath :文件所在物理路径
     * @param content :写入内容
     * @param isAppend :是否追加写入
     * @throws Exception
     */
    public static boolean WriteFile(String filepath, String content, boolean isAppend) throws Exception {
        FileWriter filewriter = new FileWriter(filepath, isAppend); // 写入多行 第2个参数=true append的方式写入
        PrintWriter printwriter = new PrintWriter(filewriter);
        printwriter.println(content);
        printwriter.flush();
        printwriter.close();
        filewriter.close();
        logger.info("写入成功");
        return true;
    }

    /**
     * 将文件内容写入文件中
     *
     * @param filepath :文件所在物理路径
     * @param content :写入内容
     * @param isAppend :是否追加写入,用于session导出使用
     * @throws Exception
     */
    public static boolean WriteSessionFile(String filepath, String content, boolean isAppend) throws Exception {
        FileWriter filewriter = new FileWriter(filepath, isAppend); // 写入多行 第2个参数=true append的方式写入
        PrintWriter printwriter = new PrintWriter(filepath,"UTF-8");
        printwriter.println(content);
        printwriter.flush();
        printwriter.close();
        filewriter.close();
        return true;
    }

    /**
     * 日志备份
     *
     * @param filePath :日志备份路径
     * @param baksize  :日志备份大小参考值(字节大小)
     * @throws IOException
     */
    public static boolean logBak(String filePath, long baksize) throws IOException {
        File f = new File(filePath);
        long len = f.length();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = simpledateformat.format(new Date());
        String fileName = f.getName();
        int dot = fileName.indexOf(".");
        String bakName = s + fileName.substring(dot);
        System.out.println("bakName:"+bakName);
        if (len >= baksize) {
            renamefile(filePath, bakName);
            makeFile(filePath);
        }
        f = null;
        return true;
    }

    /**
     * 得到文件扩展名
     *
     * @param f :文件对象
     */
    public static String extName(File f) {

        String fileName = f.getName();
        int dot = fileName.lastIndexOf(".") + 1;
        return fileName.substring(dot);
    }
    /**
     * 读取文件内容，返回String
     * @param filePath
     * @return
     */
    public static String readFileToString(String filePath)
    {
        // 读取txt内容为字符串
        StringBuffer txtContent = new StringBuffer();
        // 每次读取的byte数
        byte[] b = new byte[8 * 1024];
        InputStream in = null;
        try
        {
            // 文件输入流
            in = new FileInputStream(filePath);
            while (in.read(b) != -1)
            {
            // 字符串拼接
                txtContent.append(new String(b));
            }
            // 关闭流
            in.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return txtContent.toString();
    }

    public static int findFile(String findPath,String key) {
        // 查找目录
        File dir = new File(findPath);
        File[] rm = dir.listFiles();
        int number = 0;
        for (File file : rm) {
            if (file.getName().indexOf(key) >= 0) {
                if (file.isFile()) {
                    number++;
                }
            }
        }
        return number;
    }

    /**
     * 创建压缩文件
     */
    public static int createZip(List<File> files,String outPath){
        byte[] buffer = new byte[1024];
        try {
            //创建压缩流
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                    outPath));
            //依次读入文件并添加到压缩文件
            for (File file:files) {
                FileInputStream fis = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                //设置压缩文件内的字符编码，不然会变成乱码
                out.setEncoding("UTF-8");
                int len;
                // 读入需要下载的文件的内容，打包到zip文件
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();
                fis.close();
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            logger.error("文件下载出错", e);
            return 0;
        }
        return 1;
    }
    /**
     * 压缩文件
     *
     * @param zipFileName
     *            保存的压缩包文件路径
     * @param filePath
     *            需要压缩的文件夹或者文件路径
     * @param isDelete
     *            是否删除源文件
     * @throws Exception
     */
    public static void zip(String zipFileName, String filePath, boolean isDelete) throws Exception {
        zip(zipFileName, new File(filePath), isDelete);
    }

    /**
     * 压缩文件
     *
     * @param zipFileName
     *            保存的压缩包文件路径
     * @param inputFile
     *            需要压缩的文件夹或者文件
     * @param isDelete
     *            是否删除源文件
     * @throws Exception
     */
    public static void zip(String zipFileName, File inputFile, boolean isDelete) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        if (!inputFile.exists()) {
            throw new FileNotFoundException("在指定路径未找到需要压缩的文件！");
        }
        zip(out, inputFile, inputFile.getName(), isDelete);
        out.close();
    }

    /**
     * 递归压缩方法
     *
     * @param out
     *            压缩包输出流
     * @param inputFile
     *            需要压缩的文件
     * @param base
     *            压缩的路径
     * @param isDelete
     *            是否删除源文件
     * @throws Exception
     */
    private static void zip(ZipOutputStream out, File inputFile, String base, boolean isDelete) throws Exception {

        if (inputFile.isDirectory()) { // 如果是目录
            File[] inputFiles = inputFile.listFiles();
            out.setEncoding("UTF-8");
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < inputFiles.length; i++) {
                zip(out, inputFiles[i], base + inputFiles[i].getName(), isDelete);
            }
        } else { // 如果是文件
            if (base.length() > 0) {
                out.putNextEntry(new ZipEntry(base));
            } else {
                out.putNextEntry(new ZipEntry(inputFile.getName()));
            }
            FileInputStream in = new FileInputStream(inputFile);
            try {
                int len;
                byte[] buff = new byte[1024];
                out.setEncoding("UTF-8");
                while ((len = in.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                out.flush();
            } catch (IOException e) {
                throw e;
            } finally {
                in.close();
            }
        }
        if (isDelete) {
            inputFile.delete();
        }
    }

    /**
     * 解压到指定目录
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    public static int unZipFiles(String zipPath,String descDir)throws IOException{
        return unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * @param zipFile
     * @param descDir
     * @author isea533
     */
    @SuppressWarnings("rawtypes")
    public static int unZipFiles(File zipFile,String descDir)throws IOException{
        File pathFile = new File(descDir);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for(Enumeration entries = zip.getEntries(); entries.hasMoreElements();){
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if(!file.exists()){
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if(new File(outPath).isDirectory()){
                continue;
            }
            //输出文件路径信息
            System.out.println(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while((len=in.read(buf1))>0){
                out.write(buf1,0,len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
        return 1;
    }

    /**
     * freemark模板替换
     * @param templateUrl 模板路径
     * @param url  文件保存路径
     * @param data  替换数据
     * @param templeName 模板名称
     * @return
     */
    public static int replaceFile(String templateUrl,String url,Object data,String templeName){
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        // 设置模本装置方法和路径
        File templePath = new File(templateUrl);
        try {
            configuration.setDirectoryForTemplateLoading(templePath);
            // temple.ftl为要装载的模板
            Template t = configuration.getTemplate(templeName);
            // 设置编码格式
            // 输出文档路径及名称
            File outFile = new File(url);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
            t.process(data, out);
            out.close();
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }


    public static boolean exists(String fullPath){
        File f = new File(fullPath);
        if(f.exists()) return true;
        return false;
    }

}
