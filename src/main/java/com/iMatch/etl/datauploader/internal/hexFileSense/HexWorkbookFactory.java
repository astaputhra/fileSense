package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class HexWorkbookFactory {
    public static Workbook create(File file, String password, boolean readOnly) throws IOException, InvalidFormatException, EncryptedDocumentException {
        if (! file.exists()) {
            throw new FileNotFoundException(file.toString());
        }

        try {
            NPOIFSFileSystem fs = new NPOIFSFileSystem(file, readOnly);
            try {
                return create(fs, password);
            } catch (RuntimeException e) {
                IOUtils.closeQuietly(fs);
                throw e;
            }
        } catch(OfficeXmlFileException e) {
            // opening as .xls failed => try opening as .xlsx
            OPCPackage pkg = OPCPackage.open(file, readOnly ? PackageAccess.READ : PackageAccess.READ_WRITE);
            try {
                return new XSSFWorkbook(pkg);
            } catch (IOException ioe) {
                // ensure that file handles are closed (use revert() to not re-write the file)
                pkg.revert();
                //pkg.close();

                // rethrow exception
                throw ioe;
            } catch (IllegalArgumentException ioe) {
                // ensure that file handles are closed (use revert() to not re-write the file)
                pkg.revert();
                //pkg.close();

                // rethrow exception
                throw ioe;
            }
        }
    }
    private static Workbook create(NPOIFSFileSystem fs, String password) throws IOException, InvalidFormatException {
        DirectoryNode root = fs.getRoot();

        // Encrypted OOXML files go inside OLE2 containers, is this one?
        if (root.hasEntry(Decryptor.DEFAULT_POIFS_ENTRY)) {
            EncryptionInfo info = new EncryptionInfo(fs);
            Decryptor d = Decryptor.getInstance(info);

            boolean passwordCorrect = false;
            InputStream stream = null;
            try {
                if (password != null && d.verifyPassword(password)) {
                    passwordCorrect = true;
                }
                if (!passwordCorrect && d.verifyPassword(Decryptor.DEFAULT_PASSWORD)) {
                    passwordCorrect = true;
                }
                if (passwordCorrect) {
                    stream = d.getDataStream(root);
                }
            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            }

            if (! passwordCorrect) {
                if (password != null)
                    throw new EncryptedDocumentException("Password incorrect");
                else
                    throw new EncryptedDocumentException("The supplied spreadsheet is protected, but no password was supplied");
            }

            OPCPackage pkg = OPCPackage.open(stream);
            return WorkbookFactory.create(pkg);
        }

        // If we get here, it isn't an encrypted XLSX file
        // So, treat it as a regular HSSF XLS one
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
        }
        Workbook wb = new HSSFWorkbook(root, true);
        Biff8EncryptionKey.setCurrentUserPassword(null);
        return wb;
    }
}
