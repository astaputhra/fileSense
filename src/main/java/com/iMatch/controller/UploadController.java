package com.iMatch.controller;

import com.iMatch.ETLDirectoryScanner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileManagementProcess.class);

    @Autowired
    ETLDirectoryScanner etlDirectoryScanner;

    @Autowired
    FileManagementProcess fileManagementProcess;

    @Value("#{appProp['etl.directory.tmpFolder']}")
    private String tmpFolder;

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "F://temp//";

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {


        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @Transactional
    @GetMapping("/upload/filePath/{filePath}")
    public String uploadStatus(@PathVariable String filePath)  {
        logger.debug(" Request Has Been Received For File {}",filePath);
        try {
            if(fileManagementProcess.preProcess(FileUtils.getFile(tmpFolder + "\\" + filePath))){
                FileUtils.forceDelete(new File(tmpFolder+"\\"+filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Admin!!! Error While Deleting the File From Temp Folder");
        }
        return "redirect:/";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() throws NamingException {
        return "uploadStatus";
    }

}