package com.eshop.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by windvalley on 2018/7/24.
 */
public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static int ftpPort = Integer.parseInt(PropertiesUtil.getProperty("ftp.server.port"));
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, ftpPort, ftpUser, ftpPassword);
        logger.info("开始连接FTP服务器");
        if (ftpUtil.uploadFile("/test", fileList) == true){
            logger.info("结束上传文件成功");
            return true;
        }
        return false;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        //连接FTP服务器
        if (connectServer(this.ip, this.port, this.user, this.password) == true){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem: fileList){
                    fileInputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fileInputStream);
                }
                ftpClient.logout();
                return true;
            } catch (IOException e) {
                logger.error("上传文件出错", e);
            } finally {
                fileInputStream.close();
                if(ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            }
        }
        return false;
    }

    private boolean connectServer(String ip, int port, String user, String password){
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            return ftpClient.login(user, password);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常", e);
        }
        return false;
    }

    public FTPUtil(String ip, int port, String user, String password){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
