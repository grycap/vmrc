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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.grycap.vmrc.dao.ACLDao;
import org.grycap.vmrc.dao.ApplicationDao;
import org.grycap.vmrc.dao.OSDao;
import org.grycap.vmrc.dao.VMIDao;
import org.grycap.vmrc.entity.ACL;
import org.grycap.vmrc.entity.Application;
import org.grycap.vmrc.entity.OS;
import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.entity.VMI;
import org.grycap.vmrc.exceptions.ServiceException;
import org.grycap.vmrc.utils.HQLBuilder;
import org.grycap.vmrc.utils.parsing.VMIDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class VMIService extends BaseService<VMI, VMIDao> {
	
	@Autowired
	private OSDao osDao;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ACLDao aclDao;
	
	@Override
	public VMI saveOrUpdate(VMI vmi) throws ServiceException {
		try {
			if(vmi.getOs() != null) {
				OS os = osDao.getByExample(vmi.getOs());
				if(os == null) os = osDao.saveOrUpdate(vmi.getOs());
				vmi.setOs(os);
			}
			
			if(vmi.getApplications() != null) {
				Set<Application> applications = vmi.getApplications();
				vmi.setApplications(new HashSet<Application>());
				for(Application application : applications) {
					Application applicationToSave = applicationDao.getByExample(application); 
					if(applicationToSave == null) applicationToSave = applicationDao.saveOrUpdate(application);
					vmi.getApplications().add(applicationToSave);
				}
			}
			
			ACL acl = vmi.getAcl();
			if(acl == null) acl = new ACL();
			aclDao.save(acl);
			vmi.setAcl(acl);
			
			return baseDao.saveOrUpdate(vmi);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	public List<VMI> searchVMIs(VMIDescriptor vd, User user){
		log.debug("About to search");
		HQLBuilder hqlBuilder = new HQLBuilder(vd, user);
		String hqlQuery = hqlBuilder.toHQL();
		log.debug("HQL query: " + hqlQuery);
		List<VMI> vmiList = this.baseDao.getAllVMIByQuery(hqlQuery);
		List<VMI> resList = new ArrayList<VMI>();
		
		log.debug("Number of results: " + vmiList.size() + ": Query result: " + vmiList);
		
		for (VMI vmi : vmiList){
			int rankValue = vd.rankVMI(vmi);
			log.info("The rankValue is " + rankValue + " for VMI " + vmi);
			if (rankValue != -1){
				vmi.setRankValue(rankValue);
				resList.add(vmi);
			} else {
				log.debug("The VMI " + vmi.getName() + " does not fulfill the requirements.");			
			}
				
			
		}
		
		return resList;		
	}

	public void deleteByName(String name) throws ServiceException {
		try {
			VMI vmi = baseDao.getByProperty("name", name);
			baseDao.delete(vmi);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		
	}

	public VMI getByName(String name) throws ServiceException {
		try {
			return baseDao.getByProperty("name", name);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public List<VMI> getListCheckingACL(User user) throws ServiceException {
		try {
			return baseDao.getListCheckingACL(user);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public List<Application> listApplications() throws ServiceException{
		try {
			return applicationDao.list();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
		

}
