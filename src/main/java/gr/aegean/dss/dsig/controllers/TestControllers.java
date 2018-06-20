/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author nikos
 */
@Controller
@RequestMapping("test")
public class TestControllers {

    private final Logger log = LoggerFactory.getLogger(TestControllers.class);

    @GetMapping(value = "test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> test() {
        return Flux
                .range(0, 10)
                .delayElements(Duration.ofMillis(2500))
                .map(num -> String.valueOf(num));
    }

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Flux<String> uploadFiles(@RequestBody Flux<Part> parts) {
        return parts
                .filter(p -> p instanceof FilePart)
                .cast(FilePart.class)
                .flatMap(this::saveFile)
                .map(File::getName)
                .doFinally(s -> log.info("Upload files process completed with signal: {}", s));
    }

    private Mono<File> saveFile(FilePart filePart) {
        Path target = Paths.get("/home/nikos").resolve(filePart.filename());
        try {
            Files.deleteIfExists(target);
            File file = Files.createFile(target).toFile();
            log.info("Save file !! " + file.getName());
            return filePart.transferTo(file)
                    .then(Mono.just(file)
            );

//            return Mono.defer(() -> fileCopy)
//                    .map(r -> {
//                        log.info("File name is " + file.getName());
//                        return file;
//                    });
        } catch (IOException e) {
            log.info("ERROR", e);
            throw new RuntimeException(e);
        }
    }

    private Mono<File> getFile() {
        Path target = Paths.get("/home/nikos").resolve("signed.pdf");
        //            Files.deleteIfExists(target);
        File file = new File(target.toUri());
        log.info("File name is!!! " + file.getName());
        return Mono.just(file);
    }
//

    @PostMapping(value = "/upload2",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity> uploadFilesAndReturn(@RequestBody Flux<Part> parts) {
        return parts
                .filter(p -> p instanceof FilePart)
                .cast(FilePart.class)
                .flatMap(this::saveFile)
                .flatMap(f -> Mono.defer(() -> getFile()))
                .map(f -> {
                    log.info("received file " + f.getName());
                    return f;
                })
                //                .cast(File.class)
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

    @GetMapping(value = "/upload3",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity> returnFile() {
        Mono<ResponseEntity> result
                = this.getFile()
                        .map(f -> {
                            log.info("received file " + f.getName());
                            return f;
                        })
                        .map(f -> {
                            try {
                                Resource rec;
                                rec = new InputStreamResource(new FileInputStream(f)); //new FileSystemResource(f.getAbsolutePath());
                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentDispositionFormData(f.getName(), f.getName());
                                return ResponseEntity
                                        .ok()
                                        .cacheControl(CacheControl.noCache())
                                        .headers(headers).body(rec);
                            } catch (FileNotFoundException ex) {
                                HttpHeaders headers = new HttpHeaders();
                                log.info("ERROR", ex);
                                return new ResponseEntity(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                            }

                        })
                        .cast(ResponseEntity.class);
        return result;
//                .doFinally(s -> log.info("Upload files process completed with signal: {}", s));
    }

}
