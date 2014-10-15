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

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.grycap.vmrc.exceptions.DaoException;
import org.grycap.vmrc.utils.ClassUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * Base Data Access Object (DAO) to access/modify information from the Database
 *
 */
@SuppressWarnings("unchecked")
@Transactional
public abstract class BaseDao<Type> {
	protected Class<?> clazz = ClassUtils.getClassFromGenericInstance(this);
	protected String idField;
	protected String tableName;
	protected Logger log;
	
	public BaseDao(){
		this.log = Logger.getLogger(getClass());
	}

	@Autowired
	private SessionFactory sessionFactory;
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Transaction beginTransaction() { 
		return getSessionFactory().getCurrentSession().beginTransaction();
	}
	
	

	/**
	 * Stores the object in the database.
	 * 
	 * @param object
	 *            object to store
	 * @throws DaoException
	 *             DAO-level exception
	 */
	public Type save(Type object) throws DaoException {
		try {
			getSessionFactory().getCurrentSession().save(object);
			getSessionFactory().getCurrentSession().flush();
			return object;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Stores or updates the object in the database.
	 * 
	 * @param object
	 *            object to store
	 * @throws DaoException
	 *             DAO-level exception
	 */
	public Type saveOrUpdate(Type object) throws DaoException {
		try {
			getSessionFactory().getCurrentSession().saveOrUpdate(object);
			getSessionFactory().getCurrentSession().flush();
			return object;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Updated the object in the database.
	 * 
	 * @param object
	 *            object to update
	 * @throws DaoException
	 *             DAO-level exception
	 */
	public Type update(Type object) throws DaoException {
		try {
			getSessionFactory().getCurrentSession().evict(object);
			getSessionFactory().getCurrentSession().merge(object);
			getSessionFactory().getCurrentSession().flush();
			return object;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Removes the object from the database.
	 * 
	 * @param object
	 *            object to remove
	 * @throws DaoException (DAO-level exception)
	 */
	public void delete(Type object) throws DaoException {
		try {
			getSessionFactory().getCurrentSession().delete(object);
		} catch (HibernateException e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Retrieves a list of objects.
	 * 
	 * @return List List of objects
	 * @throws DaoException
	 *             
	 */
	public List<Type> list() throws DaoException {
		try {
			List<Type> lista = getSessionFactory().getCurrentSession().createCriteria(clazz).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			return lista;
		} catch (HibernateException e) {
			throw new DaoException(e);
		}
	}

	/**
	 * Retrieves the object by its ID.
	 * 
	 * @param id Object identifier
	 * @return Type The object
	 * @throws DaoException
	 *             
	 */
	public Type getById(Serializable id) throws DaoException {
		try {
			return (Type) getSessionFactory().getCurrentSession().get(clazz, id);
		} catch (HibernateException e) {
			throw new DaoException(e);
		}
	}
	
	/**
	 * Retrieves an object given a similar one.
	 * @param example
	 * @return Object instance
	 * @throws DaoException DAO-level exception
	 */
	public Type getByExample(Type example) throws DaoException {
		try {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(clazz).add(Example.create(example).excludeNone());
			return (Type) criteria.uniqueResult();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public long getTotalPages(int offSet) throws DaoException {
		long resultado = 0;

		if (offSet >= 0) {
			try {
				Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(clazz);
				criteria.setProjection(Projections.rowCount());
				resultado = ((Integer) criteria.uniqueResult()) / offSet;
			} catch (Exception e) {
				resultado = 0;
				e = null;
			}
		}
		return resultado;
	}

	public List<Type> getListPaged(int first, int offSet) throws DaoException {
		List<Type> lista = null;
		try {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(clazz);
			criteria.setFirstResult(first);
			criteria.setMaxResults(offSet);
			lista = criteria.list();
		} catch (Exception e) {
			e = null;
			lista = null;
		}
		return lista;
	}
	

	/**
	 * Obtain an object according to a given property (attribute)
	 * @param propertyName
	 * @param name
	 * @return
	 * @throws DaoException
	 */
	public Type getByProperty(String propertyName, String name) throws DaoException {
		try {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(clazz);
			criteria.add(Restrictions.eq(propertyName, name));
			return (Type) criteria.uniqueResult();
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	
}

