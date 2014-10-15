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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.grycap.vmrc.dao.UserDao;
import org.grycap.vmrc.entity.ACL;
import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.exceptions.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ConfigurationService extends BaseService<User, UserDao> {
	private Logger log = Logger.getLogger(ConfigurationService.class);

	public void createRequiredUsers(Properties p) throws ServiceException {
		try {
			String adminPass = p.get("admin_password") != null ? (String) p.get("admin_password") : "passwd1";
			createOrUpdateUser("admin", adminPass, ACL.ADMIN_USER);
			createOrUpdateUser("anonymous", "", ACL.ANONYMOUS_USER);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private void createOrUpdateUser(String userName, String password, int aclType) throws ServiceException {
		User dbUser;
		try {
			User user = new User(userName, password);
			user.setAcl(new ACL(aclType));			
			if ( (dbUser = baseDao.getByProperty("userName", userName)) !=null){			
				dbUser.setPassword(password);
				baseDao.saveOrUpdate(dbUser);
			} else{				
				baseDao.saveOrUpdate(user);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
