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

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.grycap.vmrc.dao.BaseDao;
import org.grycap.vmrc.exceptions.DaoException;
import org.grycap.vmrc.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService<Type, TypeDao extends BaseDao<Type>> {
	protected Logger log = Logger.getLogger(this.getClass());
	protected TypeDao baseDao;
	
	

	public List<Type> getList() throws ServiceException {
		try {
			return baseDao.list();
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Stores an object
	 * 
	 * @param element The object to store
	 * @throws ServiceException 
	 */
	public Type save(Type element) throws ServiceException {
		try {
			return baseDao.save(element);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	
	public Type saveOrUpdate(Type element) throws ServiceException {
		try {
			return baseDao.saveOrUpdate(element);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Updates an object
	 * 
	 * @param element The object to update
	 * @throws ServiceException 
	 */
	public Type update(final Type element) throws ServiceException {
		try {
			return baseDao.update(element);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Deletes an object.
	 * 
	 * @param element Object to delete
	 * @throws ServiceException 
	 */
	public void delete(Type element) throws ServiceException {
		try {
			baseDao.delete(element);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	
	@Autowired
	public void setBaseDao(TypeDao inBaseDao) {
		this.baseDao = inBaseDao;
	}
	
	
	/**
	 * Returns an object by its Id
	 * 
	 * @param id Object Id
	 * @return Type
	 * @throws ServiceException
	 */
	public Type getElementById(Serializable id) throws ServiceException {
		try {
			return baseDao.getById(id);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

}
