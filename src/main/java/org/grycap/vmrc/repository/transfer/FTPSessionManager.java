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

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.log4j.Logger;

/**
 * The FTPSessionManager creates and destroys FTPSessions on-demand
 * A single FTP Server is employed, which gives support to the different uploads/downloads
 */
public class FTPSessionManager {
	private Hashtable<String, FTPSession> mapSession;
	private ThreadSessionAlive threadSessionAlive;
	private static int MAX_SESSION_TIME =  900000; // An FTP session can remain open up to 15 minutes
	private Logger log;
	private FTPServer ftpServer;

	  /**
	   * @param args
	 * @throws IOException 
	   */
	  public FTPSessionManager() throws IOException {
		this.log = Logger.getLogger(getClass());
		if (mapSession == null) mapSession = new Hashtable<String, FTPSession>();
	    else mapSession.clear();
		this.ftpServer = new FTPServer();
	    threadSessionAlive = new ThreadSessionAlive(this);
	    threadSessionAlive.start();	   
	    
	  }


	  /**
	   * @param args
	 * @throws FtpException 
	   */
	  public FTPSession createNewFTPUploadSession(String vmiName, String vmiFileName) throws FtpException {
	    FTPSession ftpSession = new FTPSession(ftpServer, vmiName, vmiFileName, FTPSession.TYPE_UPLOAD);
	    mapSession.put(ftpSession.getSessionId(), ftpSession);
	    return ftpSession;
	  }
	  
	  public FTPSession createNewFTPDownloadSession(String vmiName) throws FtpException {
		    FTPSession ftpSession = new FTPSession(ftpServer, vmiName,FTPSession.TYPE_DOWNLOAD);
		    mapSession.put(ftpSession.getSessionId(), ftpSession);
		    return ftpSession;
		  }


	  
	  /**
	   * @param args
	   */
	  public void deleteFTPSession(String idSession) {
		mapSession.remove(idSession);			
	  }

	  

	  /**
	   * @param args
	   */
	  
	  protected synchronized void keepAliveSession() {
	    Iterator it = mapSession.entrySet().iterator();
		while (it.hasNext()) {
	  	  Map.Entry e = (Map.Entry) it.next();
		  FTPSession ftpSession = (FTPSession) e.getValue();
		  long nowStamp = new Date().getTime();
		  if ((nowStamp - ftpSession.getDateSession().getTime()) > MAX_SESSION_TIME) {
			log.warn("Time exceeded for FTP session: " + ftpSession.getSessionId());
			ftpSession.finalizeTranfer();
		    deleteFTPSession(ftpSession.getSessionId());
		  }
		}
	  }
	  
	  

}
