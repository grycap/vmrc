/*******************************************************************************
 * Copyright 2012-2013, Grid and High Performance Computing group (http://www.grycap.upv.es)
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.DbUserManager;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.Logger;
import org.grycap.vmrc.utils.VMRCServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/*
 * 
 * Unfinished attempt to use DataBase-supported User Manager. The Users are stored
 * into FTP_USER with the enableflag set to 0 even though the default value should be 1
 * and despite the code-configuration. We revert back to file-based users (FTPServer).
 *
 */
public class SpringFTPServer {
	public static int MAX_FTP_USERS = 100;
	public static int DEFAULT_FTP_PORT = 21000;
	public static String FTP_USERS_FILE = "users.properties";
	
	private int numConnections;
	private String host;		
	private DbUserManager userManager;
	private DefaultFtpServer server;
	private Logger log; 
	


	
	public SpringFTPServer() throws IOException{
	  this.log = Logger.getLogger(getClass());
	  this.numConnections = 0;	 
	  this.host = VMRCServerUtils.getPublicIP();
	  	  
      ApplicationContext classPathXmlApplicationContext =
          new ClassPathXmlApplicationContext ("ftpd.xml");
       server =  (DefaultFtpServer)
          classPathXmlApplicationContext.getBean ("vmrc-embedded-ftp-server");
       this.userManager = (DbUserManager) this.server.getUserManager();          
		log.info("Obtained FTP Server: "  + server); 
	       
	}
	
	
	private String selectNewUser() {
		String newUser = null;
		  for (int i=0; i<MAX_FTP_USERS; i++) {
		    try {
			  newUser = "user"+i;
		  	  if (! userManager.doesExist(newUser)) break;
			} catch (FtpException e) {
				log.error("Error reading user file from FTP server.");
		  	  return null;
		    }
		
			if (i == MAX_FTP_USERS-1) {
			  log.error("Maximum number of users exceeded.");
		  	  return null;
			}
		  }

		  return newUser;
		  }
	
	
	 public  void deleteNewUser(String user) throws FtpException {
		 if (userManager.doesExist(user)) userManager.delete(user);			
	 }
	
	 /**
	   * Create
	   * @param vmiName The name of the VMI
	   * @param vmiFileName The filename of the VMI (to be uploaded or retrieved)
	   * @param type The type of connection (upload, download) (@see FTPSession)
	   * @return
	   */
	  public FTPTransferParams createOnTheFlyFTPConfiguration(String vmiName, String vmiFileName, int type) throws FtpException{
		  log.debug("Creating on-the-fly FTP configuration");
		  FTPTransferParams tp = new FTPTransferParams();
		  BaseUser user = new BaseUser();
		  String filePath, msg;
		  String newUser = selectNewUser();
		  if (newUser == null) {
			  msg = "Error creating new user.";
			  log.error(msg);
			  throw new FtpException(msg);
			}
		  
		// Create a temporary user in the FTP server.
		  user.setEnabled(true);
		  user.setName(newUser);
		  String newPassword = VMRCServerUtils.generateRandomChain(8);
		  user.setPassword(newPassword);
		  
		 
		  if (type == FTPSession.TYPE_UPLOAD){	  
			filePath = RepositoryManager.getRepositoryLocationToStoreFile(vmiName, vmiFileName);
			log.debug("Creating the appropriate dirs to host file: " + vmiFileName);
			new File(filePath).getParentFile().mkdirs();
			user.setHomeDirectory(new File(filePath).getParent());
			List<Authority> auths = new ArrayList<Authority>();
			Authority auth = new WritePermission();
			auths.add(auth);
			user.setAuthorities(auths);
		  } else{ //DOWNLOAD
			  String[] files = VMRCServerUtils.listDirectory(RepositoryManager.getRepositoryLocationForVMI(vmiName)); 
			  if (files == null){				  
				  msg = "No file in the repository found for VMI " + vmiName;
				  log.error(msg);
				  throw new FtpException(msg);		  
			  }
			  log.debug("Found " + files.length + " related to " + vmiName + " in the repository. Will pick the first one.");
			  filePath = RepositoryManager.getRepositoryLocationToStoreFile(vmiName,files[0]); 
			  log.debug("Obtained file " + filePath);
		  }
		  				 
		    try {
		      // Store the user into users.properties file
		      userManager.save(user);
			} catch (FtpException e) {
				log.error("Problems while saving FTP user: " + user);
			   deleteNewUser(newUser);			   
		    }
		  
			tp.setUser(newUser);
		    tp.setPass(newPassword);
		    tp.setPath(filePath);
		    tp.setHost(this.host);
		    tp.setPort(DEFAULT_FTP_PORT);
		  return tp;
	  }
	  
	  private void startServerInternal() throws FtpException {
		  log.info("Starting Internal FTP Server");
		  server.start();
		  /*
			log.info("Creating FTP server.");
			if (numConnections == 0){				
				log.debug("Creating and starting FtpServer instance");								
				server.start();	  	  		    
			} else {
		    log.info("Resuming already existing FTP server instance.");
		    server.resume();
		    log.info("Available FTP server: " + server);
			}
			*/
	  }

		  /**
		   * @param args
		   */
		  public void suspendServerInternal() {
		    server.suspend();
		  }

		  /**
		   * @param args
		   */
		  
		  public synchronized void notifyEndOfTransfer() {		
		    numConnections--;
		    log.debug("Finalizing FTP session. ("+ numConnections + ") active sessions");
		    //TODO Removed created user
		    if (numConnections==0) {
		    	log.debug("No active FTP sessions. Suspending FTP server.");
		    	suspendServerInternal();
		    }
		  }
		 	 

		  /**
		   * @param args
		 * @throws FtpException 
		   */
		  public synchronized void notifyStartOfTransfer() throws FtpException {
			if (numConnections == 0) {
				log.debug("Initiating first FTP session. Starting up the FTP server.");
				startServerInternal();
				numConnections++;
			}			
		  }

		  /**
		   * @param args
		 * @throws IOException 
		   */
		  public FtpServerFactory configureFtpServerFactory() throws IOException{
			 FtpServerFactory fsf = new FtpServerFactory();		
			 log.info("Configuring an on-the-fly FTP server.");
			 
			  String ftpUsersFilePath = null;
			  File fFTPServerUserFile = null;
			  ftpUsersFilePath = RepositoryManager.getRepositoryLocation() + File.separator + "conf" + File.separator + FTP_USERS_FILE;
			  log.debug("Creating FTP users file at " + ftpUsersFilePath);
		      fFTPServerUserFile = new File(ftpUsersFilePath);
		      fFTPServerUserFile.mkdirs();
			  fFTPServerUserFile.delete();
			  
			  fFTPServerUserFile.createNewFile();
			  			 		  		      
		      final Map<String, Ftplet> ftpletMap = new HashMap<String, Ftplet>();
		      /*
		      Ftplet ftplet = new ConfFtplet(this);
		      ftpletMap.put("default",ftplet);		      
		      fsf.setFtplets(ftpletMap);
		      */
		      /*
		      userManagerFactory = new PropertiesUserManagerFactory();
		      userManagerFactory.setFile(new File(ftpUsersFilePath));
		      userManagerFactory.setPasswordEncryptor(new Md5PasswordEncryptor());	    
		      userManager = userManagerFactory.createUserManager();
		      */
		      
		      
		      
		      
		      ListenerFactory listenerFactory = new ListenerFactory();
		      listenerFactory.setPort(DEFAULT_FTP_PORT);
		      fsf.addListener("default", listenerFactory.createListener());
		      fsf.setUserManager(userManager);
			  
		      // Inicializar host ip.
		      this.host = VMRCServerUtils.getPublicIP();
		      return fsf;
		  }
}