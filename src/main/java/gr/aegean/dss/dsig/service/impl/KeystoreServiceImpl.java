/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service.impl;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import gr.aegean.dss.dsig.service.KeystoreService;
import gr.aegean.dss.dsig.service.PropertiesService;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class KeystoreServiceImpl implements KeystoreService {

    private final String KEYSTORE_PASSWORD;
    private final String KEYSTORE_PATH;
    private final String CERTIFICATE_PATH;
    private final static Logger log = LoggerFactory.getLogger(KeystoreService.class);

    private final PropertiesService propServ;

    @Autowired
    public KeystoreServiceImpl(PropertiesService propServ) {
        this.propServ = propServ;
        KEYSTORE_PASSWORD = this.propServ.getProperty("PASSWORD");
        KEYSTORE_PATH = this.propServ.getProperty("KEYSTORE_PATH");
        CERTIFICATE_PATH = this.propServ.getProperty("CERTIFICATE_PATH");
    }

    @Override
    public DSSPrivateKeyEntry getPrivateKey() {
        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            String path = classLoader.getResource(KEYSTORE_PATH).getPath();
            String path = KEYSTORE_PATH;
            Pkcs12SignatureToken signingToken = new Pkcs12SignatureToken(path, new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
            return signingToken.getKeys().get(0);
        } catch (IOException ex) {
            log.info("Keystore not error", ex);
        }
        return null;
    }

    @Override
    public File getCertificate() {
//        ClassLoader classLoader = getClass().getClassLoader();
//        String path = classLoader.getResource(CERTIFICATE_PATH).getPath();
        String path = CERTIFICATE_PATH;
        return new File(path);
    }

    @Override
    public SignatureTokenConnection getSigningToken() {
        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            String path = classLoader.getResource(KEYSTORE_PATH).getPath();
            String path = KEYSTORE_PATH;
            return new Pkcs12SignatureToken(path, new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
        } catch (IOException ex) {
            log.info("Keystore not error", ex);
        }
        return null;
    }

}
