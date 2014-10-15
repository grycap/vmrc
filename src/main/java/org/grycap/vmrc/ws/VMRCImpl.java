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
package org.grycap.vmrc.ws;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.grycap.vmrc.entity.Application;
import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.entity.VMI;
import org.grycap.vmrc.exceptions.ServiceException;
import org.grycap.vmrc.model.VMIOperation;
import org.grycap.vmrc.repository.transfer.FTPSession;
import org.grycap.vmrc.repository.transfer.FTPSessionManager;
import org.grycap.vmrc.repository.transfer.FTPTransferParams;
import org.grycap.vmrc.service.SecurityService;
import org.grycap.vmrc.service.VMIService;
import org.grycap.vmrc.utils.parsing.VMIDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;


/**
 * 
 * VMRC Web Service implementation
 * 
 */
@WebService
@SOAPBinding(style = Style.DOCUMENT)
@Transactional
public class VMRCImpl extends SpringBeanAutowiringSupport {
	private static final Logger log = Logger.getLogger(VMRCImpl.class);
	@Autowired
	private VMIService vmiService;
	
	@Autowired
	private SecurityService securityService;

	@Resource
	private WebServiceContext webServiceContext;
	
	FTPSessionManager ftpSessionManager;
	
	
	public VMRCImpl(){
		initVMRC();
	}

	private void initVMRC(){
	 // log.info("AFTER INJECTION: vmiService " + vmiService);
	  //None at the moment	
	}
	
	/**************************************************************************
	 *   ============  USER OPERATIONS ======================================= 
	 **************************************************************************/
	
	/**
	 * Register a new VMI in the catalog
	 * @param vmi
	 * @return
	 * @throws Exception
	 */
	public VMI addVMIByObject(@WebParam(name = "vmi") VMI vmi) throws Exception {
		try {
			log.info("Invoked addVMIByObject with VMI: " + vmi);
			checkCredentials();			
			checkOperationCredentials(VMIOperation.ADD, vmi);
			VMI result = vmiService.saveOrUpdate(vmi);
			log.info("Successfully saved VMI : " + result);
			return result;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}
	
	
	/**
	 * Registers a new VMI in the catalog
	 * @param vmiDescStr The VMI descriptor
	 * @return The registered VMI
	 * @throws Exception
	 */
	public VMI addVMI(@WebParam(name = "vmiDescStr") String vmiDescStr) throws Exception {
		try {
			log.info("Invoked addVMIByDescriptor with VMI descriptor " + vmiDescStr);
			checkCredentials();		
			VMIDescriptor vmiDescriptor = new VMIDescriptor();					
			VMI vmi = vmiDescriptor.parseVMIFromString(vmiDescStr);			
			User user = securityService.getUserFromContext(webServiceContext);
			vmi.setOwner(user.getUserName());
			log.debug("Parsed VMI descriptor into VMI " + vmi);
			//checkOperationCredentials(VMIOperation.ADD, vmi);
			if (vmiService.getByName(vmi.getName()) != null){
				String msg = "The VMI " + vmi.getName() + " already exists in VMRC.";
				log.error(msg);
				throw new ServiceException(msg);
			}		
			
			vmiService.saveOrUpdate(vmi);
			log.info("Successfully performed addVMIByDescriptor");
			return vmi;
		} catch (Exception e) {
			log.error("Problems while invoking addVMI: " + e);
			throw e;
		}
	}
	
	/**
	 * Search the list of VMIs that match the requirements specified by the VMI descriptor
	 * @param vmiDescStr The VMI descriptor
	 * @return The list of candidate VMIs
	 * @throws Exception
	 */
	public List<VMI> search(@WebParam(name = "vmiDescStr") String vmiDescStr) throws Exception{
		try {
			if (vmiDescStr == null || vmiDescStr.equals("")) return list();		
			log.info("Invoked SEARCH with VMI descriptor: " + vmiDescStr);	
			checkCredentials();					
			VMIDescriptor vmiDescriptor = new VMIDescriptor();
			vmiDescriptor.parseFromString(vmiDescStr);			
			User user = securityService.getUserFromContextFromDatabase(webServiceContext);
			log.info("Performing SEARCH with user: " + user);
			List<VMI> l = vmiService.searchVMIs(vmiDescriptor, user);
			log.info("Successfully performed SEARCH. Obtained list of VMIs: " + l);
			return l;
		} catch (Exception e) {
			log.error("Problems while invoking SEARCH: "+ e);
			throw e;
		}
		
	}

	/**
	 * Lists the VMIs registered in the catalog (those whose ACL allow listing)
	 * @return The list of VMIs
	 * @throws Exception
	 */
	public List<VMI> list() throws Exception {
		try {
			log.info("Invoked LIST");			
			checkCredentials();					
			List<VMI> l = vmiService.getListCheckingACL(securityService.getUserFromContextFromDatabase(webServiceContext));
			log.debug("Successfully performed LIST. Obtained list of VMIs: " + l);
			return l;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Problems while invoking LIST: "+ e);
			throw new Exception(e);
		}
	}
	
	/**
	 * List the registered applications. This allows the user to know the application names
	 * already employed, in order to avoid a mess with the naming of the applications.
	 * @return
	 * @throws Exception
	 */
	public List<Application> listApplications() throws Exception{
		try{
			log.info("Invoked LIST APPLICATIONS");
			checkCredentials();
			List<Application> l = vmiService.listApplications();
			log.debug("Successfully performed LIST APPLICATIONS. Obtained list of Applications: " + l);
			return l;			
		}catch(Exception e){
			log.error("Problems while invoking LIST APPLICATIONS: " + e);
			throw new Exception(e);
		}
	}
	
	
	/**
	 * Delete a VMI from the catalog
	 * @param vmiName The name of the VMI
	 * @throws Exception
	 */
	public void delete(@WebParam(name = "vmiName") String vmiName) throws Exception {
		try {
			log.info("Invoked DELETE of VMI " + vmiName);
			checkCredentials();
			VMI vmi = vmiService.getByName(vmiName);
			checkOperationCredentials(VMIOperation.DELETE, vmi);
			vmiService.deleteByName(vmiName);
		} catch (Exception e) {
			log.error(e);
			throw new Exception("Error deleting VMI with name " + vmiName, e);
		}
	}
	
	
	
	
	/**
	 * Obtain the OVF-compliant XML representation of a VMI
	 * @param vmiName
	 * @return
	 * @throws Exception
	 */
	public String getOVFByVMI(@WebParam(name = "vmiName") String vmiName) throws Exception{
		try{
			log.info("Invoked GETOVF for VMI " + vmiName);
			checkCredentials();
			VMI vmi = vmiService.getByName(vmiName);
			String ovf = vmi.toOVF();
			log.info("Successsfully performed GETOVF for VMI " + vmiName);
			return ovf;
		}catch(Exception e){
			log.error(e);
			throw e;
		}
	}
	
	/**
	 * Modify the ACL of a VMI to specify a given perm (all|owner) to a given operation (add|list|upload|search|download|remove)
	 * @param vmiName
	 * @param operation
	 * @param user
	 * @throws Exception
	 */
	public void changeVMIAcl(@WebParam(name = "vmiName") String vmiName, @WebParam(name = "operation") String operation, @WebParam(name = "perm") String perm) throws Exception {
		try {
			log.debug("Invoked CHANGE_VMI_ACL for VMI " + vmiName + ", operation " + operation + " with perm " + perm);			
			VMI vmi = vmiService.getByName(vmiName);
			checkOwnerCredentialsOrAdmin(vmi);									
			securityService.changeVMIAcl(vmiName, operation, perm);
			log.debug("Successfully performed CHANGE_VMI_ACL for VMI " + vmiName);
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e.getMessage());
		}
	}
	
	
	/**
	 * Request to upload a VMI. This causes a temporary FTP server to be fired up in order for the client to perform the 
	 * data transfer.  
	 * @param vmiName The name of the VMI
	 * @param vmiFileName
	 * @return The FTP connection details
	 * @throws Exception
	 */
	public FTPTransferParams requestToUploadVMI(@WebParam(name = "vmiName") String vmiName, @WebParam(name = "vmiFileName") String vmiFileName) throws Exception {
		try {
			checkCredentials();
			log.info("Invoked requestToUploadVMI with VMI " + vmiName + " for file: " + vmiFileName);
			VMI vmi = vmiService.getByName(vmiName);
			if (vmi == null){
				String msg = "The VMI " + vmiName + " does not exist.";
				log.error(msg);
				throw new ServiceException(msg);
			}
			checkOperationCredentials(VMIOperation.UPLOAD, vmi);
			if (ftpSessionManager == null){
				log.debug("Creating new FTPSessionManager");
				ftpSessionManager = new FTPSessionManager();
			}
			log.debug("Creating new FTPSession to store " + vmiFileName + " for VMI " + vmiName);
			FTPSession ftpSession = ftpSessionManager.createNewFTPUploadSession(vmiName, vmiFileName);		
			FTPTransferParams ftpSessionParams = ftpSession.getTransferParameters();
			vmi.setLocation(ftpSessionParams.getPath());
			vmiService.saveOrUpdate(vmi); //Is this necessary?
			log.debug("Sucessfully performed requestToUploadVMI. Obtained FTP transfer params: " + ftpSessionParams);
			return ftpSessionParams;						
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Request to download a VMI
	 * @param vmiName
	 * @return The FTP connection details
	 * @throws Exception
	 */
	public FTPTransferParams requestToDownloadVMI(@WebParam(name = "vmiName") String vmiName) throws Exception{		
		try{
			checkCredentials();
			log.info("Invoked requestToDownloadVMI with name " + vmiName);
			VMI vmi = vmiService.getByName(vmiName);
			if (vmi == null){
				String msg = "The VMI " + vmiName + " does not exist.";
				log.error(msg);
				throw new ServiceException(msg);
			}
			checkOperationCredentials(VMIOperation.DOWNLOAD, vmi);
			if (ftpSessionManager == null){
				log.debug("Creating new FTPSessionManager");
				ftpSessionManager = new FTPSessionManager();
			}			
			log.debug("Creating new FTPSession to download VMI " + vmiName);
			FTPSession ftpSession = ftpSessionManager.createNewFTPDownloadSession(vmiName);		
			FTPTransferParams ftpTransferParams = ftpSession.getTransferParameters();
			log.info("Successfully performed requestToDownladVMI with name " + vmiName);
			return ftpTransferParams;
		}catch(Exception e){
			log.error(e);
			throw new Exception(e.getMessage());
		}
	}
	
	
	/**************************************************************************
	 *   ============  ADMIN OPERATIONS ======================================= 
	 **************************************************************************/
	
	
	/**
	 * List users
	 * 
	 * @throws Exception
	 */
	public List<User> listUsers() throws Exception {
		try {
			log.info("Invoked LIST_USERS");
			checkAdminCredentials();
			List<User> l = this.securityService.listUsers();
			log.info("Successfully performed LIST_USERS. Obtained: " + l);
			return l;
		} catch (Exception e) {
			String msg = "Error listing users. Reason: " + e.getMessage();
			log.error(msg);
			throw new Exception(msg, e);
		}
	}
	
	public User listUser(@WebParam(name = "userName") String userName) throws Exception{
		try {
			log.info("Invoked LIST_USER with " + userName);
			checkAdminCredentials();
			User u = this.securityService.findUserByName(userName);
			log.info("Successfully performed LIST_USER. Obtained: " + u);
			return u;
		} catch (Exception e) {
			String msg = "Error listing user. Reason: " + e.getMessage();
			log.error(msg);
			throw new Exception(msg, e);
		}
	}
	
	
	/**
	 * Add a new User to the catalog. Only the admin user is allowed to do it.
	 * @param userName
	 * @param userPassword
	 * @throws Exception
	 */
	public User addUser(@WebParam(name = "userName") String userName, @WebParam(name = "userPassword") String userPassword) throws Exception {
		try {
			log.info("Invoked ADDUSER with (" + userName + "," + userPassword + ")");
			checkAdminCredentials();
			User u = securityService.addUser(userName, userPassword);
			log.info("Successfully performed ADDUSER with (" + userName + "," + userPassword + ")");
			return u;
		} catch (Exception e) {
			String msg = "Error creating new user. Reason: " + e.getMessage();
			log.error(msg);
			throw new Exception(msg, e);			
		}
	}
	
	/**
	 * Delete user from the catalog. No related VMIs will be removed. The ownership of the VMIs will not
	 * be altered.
	 * @param userName
	 * @param userPassword
	 * @throws Exception
	 */
	public void deleteUser(@WebParam(name = "userName") String userName) throws Exception {
		try {
			log.info("Invoked DELETEUSER for " + userName);
			checkAdminCredentials();
			securityService.deleteUser(userName);
			log.info("Successfully performed DELETEUSER");
		} catch (Exception e) {
			String msg = "Error deleting user. Reason: " + e.getMessage();
			log.error(msg);
			throw new Exception(msg, e);
		}
	}
	
	public void changeUserAcl(@WebParam(name = "userName") String userName, @WebParam(name = "operation") String operation, @WebParam(name = "perm") String perm) throws Exception {
		try {
			log.debug("Invoked CHANGEUSERACL for user " + userName + ", operation " + operation + " with perm " + perm);
			checkAdminCredentials();
			securityService.changeUserAcl(userName, operation, perm);
			log.debug("Successfully performed CHANGEUSERACL");
		} catch (Exception e) {
			String msg = "Error changing User ACL. Reason: "+ e.getMessage();
			log.error(msg);
			throw new Exception(msg,e);
		}
	}
	
	
	
	/**************************************************************************
	 *   ============  AUXILIARY OPERATIONS ======================================= 
	 **************************************************************************/
	
	private void checkCredentials() throws ServiceException, Exception {		
		if(!securityService.hasValidUserCredentials(webServiceContext)) {
			throw new Exception("Unauthorized attempt to perform operation");
		}
	}
	
	private void checkOwnerCredentialsOrAdmin(VMI vmi) throws ServiceException, Exception {
		if(!securityService.isOwnerOfVMIOrAdmin(vmi,webServiceContext)) {
			throw new Exception("Unauthorized attempt to perform operation");
		}
	}
	
	private void checkOperationCredentials(VMIOperation operation, VMI vmi) throws ServiceException, Exception {
		if(!securityService.checkOperationCredentials(operation, vmi, webServiceContext)) {
			throw new Exception("Unauthorized attempt of performing operation " + operation.getOperationName() + " to VMI " + vmi.getName());
		}
	}
	
	private void checkAdminCredentials() throws ServiceException, Exception {
		if(!securityService.hasValidAdminUserCredentials(webServiceContext)) {
			throw new Exception("Unauthorized attempt of invoking method: Admin user required");
		}
	}

}
