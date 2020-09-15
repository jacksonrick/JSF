package com.jsf.utils.file;

import com.jsf.utils.exception.SysException;
import com.jsf.utils.string.StringUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

/**
 * Created with IntelliJ IDEA.
 * Description: zip解压缩
 * User: xujunfei
 * Date: 2020-01-02
 * Time: 14:22
 */
public class ZipUtil {

    /**
     * 解压文件
     *
     * @param srcFile
     * @param destDirPath
     */
    public static void unzip(File srcFile, String destDirPath) {
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + " 文件不存在");
        }
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile, Charset.forName("GBK"));
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                java.util.zip.ZipEntry entry = (ZipEntry) entries.nextElement();
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[4096];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unzip error ", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解压文件
     *
     * @param srcFile
     * @param desFile
     */
    public static void ungzip(File srcFile, File desFile) {
        FileInputStream is;
        FileOutputStream os;
        InputStream gzis;
        final int MAX_BYTE = 1024 * 1000;
        int len = 0;
        byte[] b = new byte[MAX_BYTE];
        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(desFile);
            try {
                gzis = new GZIPInputStream(is);
                while ((len = gzis.read(b)) != -1)
                    os.write(b, 0, len);
                os.flush();
                gzis.close();
                os.close();
                is.close();
            } catch (IOException e) {
                throw new SysException(e.getMessage(), e);
            }
        } catch (FileNotFoundException e) {
            throw new SysException(e.getMessage(), e);
        }
    }


    public static int zip(String filePath, String zipFilePath, boolean keepDirStructure) throws IOException {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        return zip(filePaths, zipFilePath, keepDirStructure);
    }

    /**
     * 压缩文件
     *
     * @param filePaths
     * @param zipFilePath
     * @param keepDirStructure
     * @return
     * @throws IOException
     */
    public static int zip(List<String> filePaths, String zipFilePath, boolean keepDirStructure) throws IOException {
        byte[] buf = new byte[1024];
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists())
            zipFile.createNewFile();
        int fileCount = 0;
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < filePaths.size(); i++) {
                String relativePath = filePaths.get(i);
                if (StringUtil.isBlank(relativePath)) {
                    continue;
                }
                File sourceFile = new File(relativePath);// 绝对路径找到file
                if (sourceFile == null || !sourceFile.exists()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(sourceFile);
                if (keepDirStructure) {
                    //保持目录结构
                    zos.putNextEntry(new java.util.zip.ZipEntry(relativePath));
                } else {
                    //直接放到压缩包的根目录
                    zos.putNextEntry(new java.util.zip.ZipEntry(sourceFile.getName()));
                }
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
                fileCount++;
            }
            zos.close();
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
        return fileCount;
    }

    /**
     * 压缩文件夹
     *
     * @param dir
     * @param keepDirStructure
     */
    public static void zipDir(String dir, String zipFile, boolean keepDirStructure) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            File sourceFile = new File(dir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
        } catch (Exception e) {
            throw new RuntimeException("zip dir error", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[1024];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param srcFile
     * @param desFile
     */
    public static void gzip(File srcFile, File desFile) {
        FileInputStream fis;
        FileOutputStream fos;
        GZIPOutputStream gzos;

        final int MAX_BYTE = 1024 * 1000;
        int len = 0;
        byte[] b = new byte[MAX_BYTE];
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(desFile);
            gzos = new GZIPOutputStream(fos);
            while ((len = fis.read(b)) != -1)
                gzos.write(b, 0, len);
            gzos.flush();
            gzos.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            throw new SysException(e.getMessage(), e);
        }
    }

    /**
     * 7zip解压，输出到同级目录
     * 服务器需要安装p7zip
     *
     * @param srcFile
     * @return
     * @throws Exception
     */
    public static String un7Zip(String srcFile) throws Exception {
        String newZipFilePath = srcFile.substring(0, srcFile.lastIndexOf("."));
        ProcessUtil.instance().process("7za x -o" + newZipFilePath + " " + srcFile);
        return newZipFilePath;
    }

    /**
     * 7zip解压，输出到同级目录，同时对乱码进行转码
     * macos暂不支持
     *
     * @param srcFile
     * @return
     * @throws Exception
     */
    public static String un7ZipEncode(String srcFile) throws Exception {
        String newZipFilePath = srcFile.substring(0, srcFile.lastIndexOf("."));
        ProcessUtil.instance().process("LC_ALL=C 7za x -o" + newZipFilePath + " " + srcFile);
        ProcessUtil.instance().process("convmv -f GBK -t utf8 --notest " + newZipFilePath + "/*");
        return newZipFilePath;
    }

}
