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
package org.grycap.vmrc.repository.transfer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.grycap.vmrc.utils.VMRCServerUtils;
import org.grycap.vmrc.ws.VMRCImpl;

public class RepositoryManager {
	private static String DEFAULT_REPO_PATH = VMRCImpl.class.getResource("VMRCImpl.class").getFile() + "/../../../../../../../repository";
	private static String repositoryLocation = DEFAULT_REPO_PATH;
	private static Logger log = Logger.getLogger(RepositoryManager.class);
	
		
	public static String getRepositoryLocation(){
		return repositoryLocation;
	}
	
	private static String getCanonicalPath(String repoLocation){
		try {
			return new File(repositoryLocation).getCanonicalPath();
		} catch (IOException e) { }
		return null;
	}
	
	public static void setRepositoryLocation(String repoLocation){
		if (repoLocation != null) repositoryLocation = repoLocation;		 
		 else repositoryLocation = getCanonicalPath(repositoryLocation);
		log.info("Using repository location: " + repositoryLocation);
		initializeRepository();
	}
	
	private static void initializeRepository() {		 
		File f1 = new File(repositoryLocation);
		f1.mkdirs();			
	  }
	
	/**
	 * Obtain the absolute path where the file will be stored
	 * @param vmiName
	 * @param fileName
	 * @return
	 */
	public static String getRepositoryLocationToStoreFile(String vmiName, String fileName){
		String path = getRepositoryLocationForVMI(vmiName) + File.separator + fileName;
		return path;
	} 

	
	public static String getRepositoryLocationForVMI(String vmiName){
		return getRepositoryLocation() + File.separator + vmiName; 
	}
}
