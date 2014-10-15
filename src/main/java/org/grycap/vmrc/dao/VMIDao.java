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
package org.grycap.vmrc.dao;

import java.util.List;

import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.entity.VMI;
import org.grycap.vmrc.exceptions.DaoException;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object (DAO) to access/modify VMI information from the DataBase
 * 
 */
@Repository
@SuppressWarnings("unchecked")
public class VMIDao extends BaseDao<VMI> {
	
	/**
	 * Get a List<VMI> according to the specified HQL query
	 * @param hqlQuery
	 * @return The List<VMI>
	 */
	public List<VMI> getAllVMIByQuery(String hqlQuery){
		return getAllVMIByQuery(hqlQuery, null);
	}
	
	/**
	 * Get a List<VMI> according to the specified HQL query. 
	 * The VMIs must include the specified applications. 
	 * @param hqlQuery
	 * @return The List<VMI>
	 */
	public List<VMI> getAllVMIByQuery(String hqlQuery, String[] appNames){
		Query q = getSessionFactory().getCurrentSession().createQuery(hqlQuery);
		if (appNames != null){
			q.setParameterList("apps", appNames);
			q.setInteger("apps_count",appNames.length);
		}
		List<VMI> l = q.list();
	   return l;
	}

	/**
	 * Get a List<VMI> considering the ACL permissions for each VMI.
	 * @param user The User that performs the query.
	 * @return The List<VMI>
	 * @throws DaoException
	 */
	public List<VMI> getListCheckingACL(User user) throws DaoException {
		try {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(clazz);
			criteria.createAlias("acl", "acl");
			
			if (!user.isAdmin()){
			    Criterion aclOwner = Restrictions.and(Restrictions.eq("acl.listPerm", "owner"), Restrictions.eq("owner", user.getUserName()));
				if (user.getAcl().getListPerm().equalsIgnoreCase("all")) {
					Criterion aclAll = Restrictions.eq("acl.listPerm", "all");				
					criteria.add(Restrictions.or(aclAll, aclOwner));				
				}else {				
					criteria.add(aclOwner);
				}
			}
			log.debug("Performing query with criteria: " + criteria);			
			return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch(Exception e) {
			throw new DaoException(e);
		}
	}	
}
