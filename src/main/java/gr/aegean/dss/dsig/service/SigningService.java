/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import java.io.IOException;

/**
 *
 * @author nikos
 */
public interface SigningService {

    public DSSDocument signDocumet(FileDocument toSignDocument, String signingInstitutionName) throws IOException;
}
