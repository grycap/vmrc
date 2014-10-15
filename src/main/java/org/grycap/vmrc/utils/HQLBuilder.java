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
package org.grycap.vmrc.utils;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.utils.parsing.VMIDescriptor;
import org.grycap.vmrc.utils.parsing.VMIStatement;

public class HQLBuilder {
	private VMIDescriptor vmiDescriptor;
	private User user;
	private Logger log;
	
	
	public HQLBuilder(VMIDescriptor vmiD, User user){
		this.log = Logger.getLogger(getClass());
		this.vmiDescriptor = vmiD;
		this.user = user;
	}
	
	public String toHQL(){
		String hqlQuery = "select distinct vmi from VMI as vmi left join vmi.applications as app left join vmi.os as os left join vmi.acl as vmiAcl where ";

		StringBuffer whereClause = new StringBuffer();
		Hashtable<String, List<VMIStatement>> descHard = this.vmiDescriptor.getHardRequirements();
		for (String key : descHard.keySet()){
			String s = descHard.get(key).get(0).toHQLClause();
			if(s != null) {		
				whereClause.append(s).append(" and ");
			}
		}	
		whereClause.replace(whereClause.length() - 4 , whereClause.length(), ""); //remove the last 'and'
		
		//Permissions
		if (user.getAcl().getSearchPerm().equals("owner")){
			whereClause.append(" and vmi.owner = '" + user.getUserName() + "'");
		}
		
		if (!user.isAdmin())
			whereClause.append(" and (vmiAcl.searchPerm = 'all' or (vmiAcl.searchPerm = 'owner' and vmi.owner = '" + user.getUserName() + "'))");
		
		
		hqlQuery += whereClause.toString();
		
		//log.debug("Built HQL query: " + hqlQuery);
		return hqlQuery;
		
	}

}
