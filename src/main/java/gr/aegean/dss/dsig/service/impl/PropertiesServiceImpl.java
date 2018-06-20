/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.aegean.dss.dsig.service.impl;

import gr.aegean.dss.dsig.service.PropertiesService;
import org.springframework.stereotype.Service;

/**
 *
 * @author nikos
 */
@Service
public class PropertiesServiceImpl implements PropertiesService {

    @Override
    public String getProperty(String propertyName) {
        return System.getenv(propertyName);
    }

}
