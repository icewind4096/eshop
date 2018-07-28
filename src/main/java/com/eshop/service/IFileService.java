package com.eshop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by windvalley on 2018/7/24.
 */
public interface IFileService {
    public String upload(MultipartFile file, String path);
}
