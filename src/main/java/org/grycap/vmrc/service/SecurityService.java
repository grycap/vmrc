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
package org.grycap.vmrc.service;

import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.grycap.vmrc.dao.ACLDao;
import org.grycap.vmrc.dao.UserDao;
import org.grycap.vmrc.dao.VMIDao;
import org.grycap.vmrc.entity.ACL;
import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.entity.VMI;
import org.grycap.vmrc.exceptions.DaoException;
import org.grycap.vmrc.exceptions.ServiceException;
import org.grycap.vmrc.model.VMIOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SecurityService extends BaseService<User, UserDao> {
	private Logger log = Logger.getLogger(SecurityService.class);
	
	@Autowired
	private ACLDao aclDao;
	
	@Autowired
	private VMIDao vmiDao;
	
	
	public List<User> listUsers() throws DaoException{
		return this.baseDao.list();
	}
	
	public User findUserByName(String userName) throws DaoException{
		User u = this.baseDao.getByProperty("userName", userName);
		if (u == null) throw new DaoException("Inexistent User " + userName);
		return u;
	}
	
		
	public User getUserFromContext(WebServiceContext webServiceContext) throws ServiceException {
		try {
			MessageContext messageContext = webServiceContext.getMessageContext();
			Map<String, Object> httpHeaders = (Map<String, Object>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);
			List<?> userList = (List<?>) httpHeaders.get("Username");
			List<?> passList = (List<?>) httpHeaders.get("Password");
	
			String clientUserName = (userList != null) ? userList.get(0).toString() : "anonymous";
			String clientPassword = (passList != null) ? passList.get(0).toString() : "";
			
			return new User(clientUserName, clientPassword);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public boolean hasValidUserCredentials(WebServiceContext webServiceContext) throws ServiceException {
		try {			
			User user = getUserFromContext(webServiceContext);
			log.debug("Checking the client credentials. Obtained user: " + user.getUserName());
			return baseDao.getByExample(user) != null;
		} catch (Exception e) {;
			throw new ServiceException(e);
		}
	}
	
	public boolean hasValidAdminUserCredentials(WebServiceContext webServiceContext) throws ServiceException {
		try {
			User user = getUserFromContext(webServiceContext);
			return "admin".equalsIgnoreCase(user.getUserName()) && baseDao.getByExample(user) != null;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public boolean isOwnerOfVMIOrAdmin(VMI vmi, WebServiceContext webServiceContext) throws ServiceException {
		User user = getUserFromContext(webServiceContext);
		return user.isAdmin() || user.getUserName().equalsIgnoreCase(vmi.getOwner());
	}
	
	public boolean checkOperationCredentials(VMIOperation operation, VMI vmi, WebServiceContext webServiceContext) throws ServiceException {
		User user = getUserFromContext(webServiceContext);
		return checkOperationCredentials(operation, vmi, user);
	}
	
	
	/**
	 * Indicates if a user is allowed to perform a certain operation for a certain VMI.
	 * @param operation
	 * @param vmi
	 * @param user
	 * @return true if the operation is allowed to be performed by the user for that certain VMI. False, otherwise.
	 */
	public boolean checkOperationCredentials(VMIOperation operation, VMI vmi, User user) {
		try {
			
			//admin is almighty
			if("admin".equals(user.getUserName())) return true;
			
			//Check user permissions
			String userPermValue = operation.getPermissionValue(user);
			if ("none".equals(userPermValue)) return false;
						
			//Check VMI permissions
			String vmiPermValue = operation.getPermissionValue(vmi);
			if("all".equals(vmiPermValue)) return true;
			if("owner".equals(vmiPermValue) && user.getUserName().equalsIgnoreCase(vmi.getOwner())) return true;
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}
	
	
	
	

	public User addUser(String userName, String userPassword) throws ServiceException {
		try {
			User user = new User(userName, userPassword);
			return baseDao.saveOrUpdate(user);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}
	
	public void deleteUser(String userName) throws ServiceException {
		try {
			log.debug("Attempting to delete user " + userName);
			User user = findUserByName(userName);
			baseDao.delete(user);
			log.debug("Sucessfully deleted user " + userName);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}
	

	public void changeVMIAcl(String vmiName, String operation, String perm) throws ServiceException {
		log.debug("About to change ACL for VMI " + vmiName + " operation " + operation + " with perm " + perm);
		if(!"all".equals(perm) && !"owner".equals(perm)) {
			throw new ServiceException("Ilegal permission in ACL. Supported values are [owner|all]");
		}
		
		try {
			VMI vmi = vmiDao.getByProperty("name", vmiName);
			if(vmi == null) throw new ServiceException("VMI not found to change ACL");
			
			ACL acl = vmi.getAcl();
			if(VMIOperation.ADD.getOperationName().equals(operation)) acl.setAddPerm(perm);
			else if(VMIOperation.DOWNLOAD.getOperationName().equals(operation)) acl.setDownloadPerm(perm);
			else if(VMIOperation.DELETE.getOperationName().equals(operation)) acl.setDeletePerm(perm);
			else if(VMIOperation.SEARCH.getOperationName().equals(operation)) acl.setSearchPerm(perm);
			else if(VMIOperation.UPLOAD.getOperationName().equals(operation)) acl.setUploadPerm(perm);
			else if(VMIOperation.LIST.getOperationName().equals(operation)) acl.setListPerm(perm);			
			else throw new ServiceException("Unknown operation. Supported values are [list|search|upload|download|delete|add]");
			
			aclDao.saveOrUpdate(acl);
		} catch (Exception e) {
			log.error(e);
			throw new ServiceException("Error setting ACL: " + e.getMessage(),e);
		}
		
	}
	
	
	public void changeUserAcl(String userName, String operation, String perm) throws ServiceException {
		log.debug("About to change ACL for User " + userName + " operation " + operation + " with perm " + perm);
		if( !("all".equals(perm) || "owner".equals(perm) || "none".equals(perm))) {
			throw new ServiceException("Ilegal permission in ACL. Supported values are [owner|all|none]");
		}
		
		try {
			User user = baseDao.getByProperty("userName",userName);
			if(user == null) throw new ServiceException("User not found to change ACL");
			
			ACL acl = user.getAcl();
			if(VMIOperation.ADD.getOperationName().equals(operation)) acl.setAddPerm(perm);
			else if(VMIOperation.DOWNLOAD.getOperationName().equals(operation)) acl.setDownloadPerm(perm);
			else if(VMIOperation.DELETE.getOperationName().equals(operation)) acl.setDeletePerm(perm);
			else if(VMIOperation.SEARCH.getOperationName().equals(operation)) acl.setSearchPerm(perm);
			else if(VMIOperation.UPLOAD.getOperationName().equals(operation)) acl.setUploadPerm(perm);
			else if(VMIOperation.LIST.getOperationName().equals(operation)) acl.setListPerm(perm);
			else throw new ServiceException("Uknown operation in acl");
			
			aclDao.saveOrUpdate(acl);
		} catch (Exception e) {
			log.error(e);
			throw new ServiceException("Error setting acl",e);
		}
		
	}
	
	public User getUserFromContextFromDatabase(WebServiceContext webServiceContext) throws DaoException, ServiceException{
		User user = getUserFromContext(webServiceContext);
		return baseDao.getByProperty("userName",user.getUserName());
		
	}

}
