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
import java.util.Date;
import org.apache.ftpserver.ftplet.FtpException;


public class FTPSession {
	 private String idSession;
	 private Date dateSession;
	 private String vmiName;
	 private String vmiFileName;
	 private int type;
	 private FTPServer ftpServer;
	  
	 public final static int TYPE_UPLOAD = 0;
	 public final static int TYPE_DOWNLOAD = 1;
	  
	 
	  /**
	   * @param args
	 * @throws FtpException 
	   */
	  public FTPSession(FTPServer ftpServer, String vmiName, String vmiFileName, int type) throws FtpException {		
		dateSession = new Date();
		this.vmiName = vmiName;
		this.vmiFileName = vmiFileName;
		idSession = Long.toString(dateSession.getTime());			
		this.type = type;
		this.ftpServer = ftpServer;
		this.ftpServer.notifyStartOfTransfer();
	  }
	  
	  public FTPSession(FTPServer ftpServer, String vmiName, int type) throws FtpException{
		  this(ftpServer, vmiName, null, type);
	  }

	

	  /**
	   * @param args
	   */
	  public String getSessionId() {
	    return idSession;
	  }

	  /**
	   * @param args
	   */
	  public Date getDateSession() {
	    return dateSession;
	  }

	
	  /**
	   * @param args
	   */
	  
	  public void finalizeTranfer() {
		  this.ftpServer.notifyEndOfTransfer();	  			
	  }

	  /**
	   * @param args
	 * @throws FtpException 
	   */
	  
	  public FTPTransferParams getTransferParameters() throws FtpException {
		FTPTransferParams ftpTransferParams = new FTPTransferParams();
		ftpTransferParams = this.ftpServer.createOnTheFlyFTPConfiguration(vmiName, vmiFileName, this.type);
		return ftpTransferParams;
	  }
	  

	  /**
	   * @param args
	 * @throws Exception 
	   *//*
	  public int initializeTransfer(){
		
		  return FTPServer_.initializeTransfer();
		
	  }
	  */

	  /**
	   * @param args
	   */
	  /*
	  public int startTransfer() {		
		  return FTPServer_.startTransfer();
		
	  }
	  */
}
