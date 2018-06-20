/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service.impl;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import gr.aegean.dss.dsig.service.FileHandlerService;
import gr.aegean.dss.dsig.service.SigningService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 *
 * @author nikos
 */
@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    private final Logger log = LoggerFactory.getLogger(FileHandlerServiceImpl.class);

    @Override
    public Mono<File> saveFile(FilePart filePart) {
        try {
            File file = File.createTempFile("upload-", "-toSign");
            log.info("Save file !! " + file.getName());
            return filePart.transferTo(file)
                    .then(Mono.just(file)
                    );
        } catch (IOException e) {
            log.info("ERROR", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public File signTempFile(File f, SigningService signServ, String signatureText) {
        try {
            FileDocument toSignDocument = new FileDocument(f.getPath());
            DSSDocument signedDocumet = signServ.signDocumet(toSignDocument, signatureText);
            File file = File.createTempFile("upload-", "-signed");
            signedDocumet.writeTo(new FileOutputStream(file));
            return file;
        } catch (IOException e) {
            log.info("Error", e);
            return f;
        }
    }

}
