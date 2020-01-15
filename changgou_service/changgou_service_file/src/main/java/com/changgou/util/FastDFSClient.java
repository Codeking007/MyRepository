package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * FastDFS操作工具
 * 实现信息获取,文件上传,文件下载,文件删除的相关操作
 */
public class FastDFSClient {
    //初始化Tracker配置信息
    static {
        try {
            //1.获取配置文件路径
            String filepath = new ClassPathResource("fdfs_client.conf").getPath();
            //2.加载配置文件
            ClientGlobal.init(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TrackerServer getTrackerServer() {
        TrackerServer trackerServer = null;
        try {
            //3.创建一个TrackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.使用TrackerClient对象创建连接
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerServer;
    }

    public static StorageClient getStorageClient() {
        //5.创建一个StorageClient对象
        StorageClient storageClient = new StorageClient(getTrackerServer(), null);
        return storageClient;
    }

    /**
     * 文件上传
     *
     * @param fastDFSFile
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) {
        String[] uploadResult = null;
        try {
            //附加参数
            NameValuePair[] meta_list = new NameValuePair[1];
            //文件作者
            meta_list[0] = new NameValuePair("author", fastDFSFile.getAuthor());
            //上传文件
            //upload_file(文件字节数组,文件扩展名,附加参数)
            uploadResult = getStorageClient().upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadResult;
    }

    /**
     * 获取文件信息
     *
     * @param groupName      组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) {
        FileInfo info = null;
        try {
            info = getStorageClient().get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 文件下载
     *
     * @param groupName      组名
     * @param remoteFileName 文件存储完整名
     * @return
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) {
        InputStream inputStream = null;
        try {
            byte[] bytes = getStorageClient().download_file(groupName, remoteFileName);
            inputStream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 删除文件
     *
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            getStorageClient().delete_file(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组信息
     *
     * @param groupName 组名
     * @return
     */
    public static StorageServer getStorageServer(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer, groupName);
            return storageServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件名和文件存储路径获取Storage服务的IP,端口
     *
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            ServerInfo[] infos = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Tracker服务地址
     *
     * @return
     */
    public static String getTrackerUrl() {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            String url = "http://" + trackerServer.getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port() + "/";
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //测试主方法
    public static void main(String[] args) throws Exception {
        //获取文件信息
//        FileInfo info = getFileInfo("group1", "M00/00/00/wKjThF4dj6yALu9IAAIUF_ZSqNY937.png");
//        System.out.println(info);
//
        //下载文件
//        InputStream is = downloadFile("group1", "M00/00/00/wKjThF4dj6yALu9IAAIUF_ZSqNY937.png");
//        FileOutputStream os = new FileOutputStream("C:\\Users\\78011\\Desktop\\tu.png");
//        int len = -1;
//        while ((len = is.read()) != -1) {
//            os.write(len);
//        }
//        os.close();
//        is.close();

        //删除文件
        deleteFile("group1", "M00/00/00/wKjThF4drnWAdt0aAAGcfai9nfE128.png");

        //获取组信息
//        StorageServer storageServer = getStorageServer("group1");
//        System.out.println("store的下标: " + storageServer.getStorePathIndex());
//        System.out.println("store的ip和端口: " + storageServer.getInetSocketAddress());

        //获取storage服务ip,端口信息
//        ServerInfo[] infos = getServerInfo("group1", "M00/00/00/wKjThF4dj6yALu9IAAIUF_ZSqNY937.png");
//        for (ServerInfo info : infos) {
//            System.out.println(info.getIpAddr() + info.getPort());
//        }

        //获取Tracker服务地址
//        String trackerUrl = getTrackerUrl();
//        System.out.println(trackerUrl);
    }
}
