/*******************************************************************************
 * Copyright 2012 I3M-GRyCAP
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.grycap.vmrc.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.grycap.vmrc.repository.transfer.RepositoryManager;
import org.grycap.vmrc.service.ConfigurationService;
import org.grycap.vmrc.utils.VMRCServerUtils;
import org.grycap.vmrc.ws.VMRCImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfigurator {
	private Logger log = Logger.getLogger(ApplicationConfigurator.class);
	public static String PROPERTIES_PATH = VMRCImpl.class.getResource("VMRCImpl.class").getFile() + "/../../../../../vmrc.properties";
	
	@Autowired
	private ConfigurationService configurationService;
	
	private static Properties properties;
	
	@PostConstruct
	private void configure() {
		try {
			log.info("Starting VMRCServer.");
			Properties p = loadProperties();
			configurationService.createRequiredUsers(p);
			
			RepositoryManager.setRepositoryLocation(p.getProperty("repository_location"));
		} catch (Exception e) {
			log.error("Error configuring application: Reason: " + e.getMessage(), e);
		}
	}
	
	
	
	 private  String getPropertiesFilePath(){
		 try {
			return new File(PROPERTIES_PATH).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	 }
	 
	 
	 public Properties loadProperties() throws FileNotFoundException, IOException{
			String propertiesFilePath = getPropertiesFilePath();	
			properties = new Properties();
			log.debug("Loading configuration from properties file: " + propertiesFilePath);
			properties.load(new FileInputStream(propertiesFilePath));
			log.debug("Succesfully loaded configuration from properties file. ");
			return properties;
		}
	 
}
