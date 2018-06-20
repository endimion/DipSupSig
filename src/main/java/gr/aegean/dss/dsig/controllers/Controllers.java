/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.controllers;

import gr.aegean.dss.dsig.service.FileHandlerService;
import gr.aegean.dss.dsig.service.SigningService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author nikos
 */
@Controller
public class Controllers {

    private final Logger log = LoggerFactory.getLogger(TestControllers.class);
    private final String SIGNER_NAME = System.getenv("SignerName");

    @Autowired
    private SigningService signServ;

    @Autowired
    private FileHandlerService fileServ;

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity> uploadFilesAndReturn(@RequestBody Flux<Part> parts) {
        return parts
                .filter(p -> p instanceof FilePart)
                .cast(FilePart.class)
                .flatMap(fileServ::saveFile)
                .map(f -> {
                    return fileServ.signTempFile(f, signServ, SIGNER_NAME);
                })
                .flatMap(f -> {
                    try {
                        Resource rec;
                        rec = new InputStreamResource(new FileInputStream(f));
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentDispositionFormData(f.getName(), f.getName());
                        return Flux.just(ResponseEntity
                                .ok()
                                .cacheControl(CacheControl.noCache())
                                .headers(headers).body(rec));
                    } catch (FileNotFoundException ex) {
                        HttpHeaders headers = new HttpHeaders();
                        log.info("ERROR", ex);
                        return Flux.just(ResponseEntity
                                .ok()
                                .cacheControl(CacheControl.noCache())
                                .headers(headers).body(null));
                    }
                })
                .cast(ResponseEntity.class)
                .next();

    }

}
