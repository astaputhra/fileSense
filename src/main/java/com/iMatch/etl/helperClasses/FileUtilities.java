package com.iMatch.etl.helperClasses;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class FileUtilities {
    private static final Logger logger = LoggerFactory.getLogger(FileUtilities.class);

    public static String checksumSHA1(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            long start = System.currentTimeMillis();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[4096];
            DigestInputStream dis = new DigestInputStream(fis, messageDigest);
            while(dis.read(buffer) > -1) {}
            byte[] digest = messageDigest.digest();
            logger.trace("time taken to generate checksum for file " + file.getName() + " " + (System.currentTimeMillis()-start) + " Millis" );
/*
            BigInteger bigint = new BigInteger(1, digest);
            long l = bigint.longValue();
            return l;
*/
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02X",b));
            }
            return sb.toString();

        } catch (Exception e) {
            logger.error("error generating sha1-checksum " + e.getMessage());
            return null;
        }
    }

    public static File createTmpFile(MultipartFile file) throws IOException {
        File tmpFile;
        String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());
        if(baseName.length() < 3) baseName += "xyz";
        tmpFile = File.createTempFile(baseName, "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        file.transferTo(tmpFile);
        return tmpFile;
    }

}
