/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service;

import java.util.Optional;

/**
 *
 * @author nikos
 */
public interface XmlParsingService {
    
    
    public Optional<String> getValueByTagName(String xmlString, String tagName);
    
}
