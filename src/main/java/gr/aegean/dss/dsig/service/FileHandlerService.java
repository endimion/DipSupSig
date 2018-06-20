/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service;

import java.io.File;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 *
 * @author nikos
 */
public interface FileHandlerService {

    public Mono<File> saveFile(FilePart filePart);

    public File signTempFile(File f, SigningService signServ, String signatureText);

}
