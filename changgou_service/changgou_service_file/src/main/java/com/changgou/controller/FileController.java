package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileController {
    @RequestMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        //1.包装文件上传对象
        /**
         * 最后一个参数获得文件扩展名
         */
        FastDFSFile dfsFile = new FastDFSFile(file.getOriginalFilename(), file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()));
        //2.调用FastDFS上传文件
        String[] upload = FastDFSClient.upload(dfsFile);
//        数组大小就是2
//        for (String s : upload) {
//            System.out.println(s);
//        }
        //3.返回文件上传结果
        String url = FastDFSClient.getTrackerUrl() + upload[0] + "/" + upload[1];
        return new Result(true, StatusCode.OK,url);
    }
}
