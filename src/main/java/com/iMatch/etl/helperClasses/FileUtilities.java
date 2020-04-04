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

/*
	public static String makeCopy(String fileToCopy) {
		String newFileName = getTmpFileName(fileToCopy);
		File srcFile = new File(fileToCopy);
		File destFile = new File(newFileName);
		try {
			FileUtils.copyFile(srcFile, destFile, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return newFileName;
	}
*/
/*

	public static String getTmpFileName(String filename) {
		String tmpFileName = null;
		String fileSeparator = System.getProperty("file.separator");
		String tmpDirName = FileUtils.getTempDirectoryPath() + fileSeparator + "kettleTmpStore";
		File tmpDirFile = new File(tmpDirName);
		try {
			FileUtils.forceMkdir(tmpDirFile);
			String fileName = FilenameUtils.getBaseName(filename);
			String extension = FilenameUtils.getExtension(filename);
			if (!extension.isEmpty()) {
				fileName = fileName + "." + extension;
			}
			tmpFileName = tmpDirName + fileSeparator + fileName;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return tmpFileName;
	}
*/

/*
    public static File makeCopy(File file) {
        String tmpFileName = getTmpFileName(file.getName());
        File tmpFile = new File(tmpFileName);
        try {
            FileUtils.copyFile(file, tmpFile);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpFile;
    }
*/
/*

    public static File generateFileName(String filename) {
        (new File(FilenameUtils.getFullPath(filename))).mkdirs();
        return generateFileName(filename, false);
    }

*/
    private static synchronized File generateFileName(String filename, boolean isRecursive) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Unable to create new file {} - error is {}", filename, e.getMessage());
                return null;
            }
            return file;
        }
        if(isRecursive) return null;

        String basename = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        String path = FilenameUtils.getFullPath(filename);
        for(int i = 0; i < 1000; i++){
            File newFile = generateFileName(path + basename + "(" + i + ")" + "." + extension, true);
            if(newFile != null) return newFile;

        }
        logger.error("Too many temporary files with base name as {} - cannot be processed ", basename);
        return  null;
    }
    public static File createTmpFile(MultipartFile file) throws IOException{
        File tmpFile;
        String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());
        if(baseName.length() < 3) baseName += "xyz";
        tmpFile = File.createTempFile(baseName, "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        return tmpFile;
    }
    public static File createTmpFile(File file) throws IOException{
        File tmpFile;
        String baseName = FilenameUtils.getBaseName(file.getName());
        if(baseName.length() < 3) baseName += "xyz";
        tmpFile = File.createTempFile(baseName, "." + FilenameUtils.getExtension(file.getName()));
        return tmpFile;
    }

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

}
