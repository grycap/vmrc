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

import java.io.IOException;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.log4j.Logger;


public class ConfFtplet extends DefaultFtplet{
	private Logger log;
	private FTPServer ftpServer;
	
	public ConfFtplet(FTPServer ftpServer){
		this.log = Logger.getLogger(getClass());
		log.info("Creating new ConfFtplet");
		this.ftpServer = ftpServer;
	}
	
	@Override
	public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
		log.info("Performing callback onDisconnect with session: " + session);			
		this.ftpServer.notifyEndOfTransfer();		
		return FtpletResult.DEFAULT;
	}	

}
