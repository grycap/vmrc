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
package org.grycap.vmrc.dao.test;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations={"/applicationContext.xml"})
@Transactional
@TransactionConfiguration(transactionManager="transactionManager")
public abstract class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	protected final Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
}
