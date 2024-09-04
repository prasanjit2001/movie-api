package com.movie.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    public String uploadFile(String path, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String filePath = Paths.get(path, fileName).toString();
        System.out.println("Attempting to upload file to: " + filePath);

        // Create file object
        File f = new File(path);
        if (!f.exists()) {
            boolean dirCreated = f.mkdirs();
            if (!dirCreated) {
                throw new IOException("Failed to create directory: " + path);
            }
        }
        // Copy the file or upload the file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return fileName;
    }

    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = Paths.get(path, fileName).toString().replace("\\", "/");
        System.out.println("Attempting to read file from: " + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found at: " + filePath);
        }
        return new FileInputStream(file);
    }
}
