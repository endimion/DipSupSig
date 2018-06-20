/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service.impl;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignaturePackaging;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import gr.aegean.dss.dsig.service.KeystoreService;
import gr.aegean.dss.dsig.service.SigningService;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class SigningServiceImpl implements SigningService {

    @Autowired
    private KeystoreService keyServ;

    @Override
    public DSSDocument signDocumet(FileDocument toSignDocument, String signingInstitutionName) throws IOException {
        PAdESSignatureParameters parameters = new PAdESSignatureParameters();
        parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        //Display a visual denotation of the signature
        SignatureImageParameters imageParameters = new SignatureImageParameters();
        imageParameters.setxAxis(200);
        imageParameters.setyAxis(200);
        SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
        textParameters.setFont(new Font("serif", Font.PLAIN, 14));
        textParameters.setTextColor(Color.BLUE);
        textParameters.setText(signingInstitutionName);
        imageParameters.setTextParameters(textParameters);
        parameters.setSignatureImageParameters(imageParameters);

        DSSPrivateKeyEntry privateKey = keyServ.getPrivateKey();
        parameters.setSigningCertificate(privateKey.getCertificate());
        parameters.setCertificateChain(privateKey.getCertificateChain());
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        PAdESService service = new PAdESService(commonCertificateVerifier);

        // This function obtains the signature value for signed information using the
        // private key and specified algorithm
        DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();

        ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
        SignatureValue signatureValue = keyServ.getSigningToken().sign(dataToSign, digestAlgorithm, privateKey);

        // We invoke the service to sign the document with the signature value obtained in
        // the previous step.
        DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
//        signedDocument.save("/home/nikos/Desktop/signed.pdf");
//        signedDocument.
        return signedDocument;
    }

}
