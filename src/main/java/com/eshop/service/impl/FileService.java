package com.eshop.service.impl;

import com.eshop.service.IFileService;
import com.eshop.util.FTPUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by windvalley on 2018/7/24.
 */
@Service
@Slf4j
public class FileService implements IFileService {
    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtName;
        log.info("开始上传文件, 文件名{}，上传路径{}， 新文件名{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (fileDir.exists() != true){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);
            
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            targetFile.delete();

            return targetFile.getName();
        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }
    }
}
