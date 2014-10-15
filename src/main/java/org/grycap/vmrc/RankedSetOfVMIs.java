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
package org.grycap.vmrc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.grycap.vmrc.entity.VMI;

public class RankedSetOfVMIs {
	//private SortedSet<VMI> set;
	private List<VMI> list;
	private transient Logger log;

	public RankedSetOfVMIs(){
		this.log = Logger.getLogger(getClass());
		//this.set = new TreeSet<VMI>();
		this.list = new ArrayList<VMI>();
	}
	
	public void add(VMI vmi){
	  log.debug("Adding vmi " + vmi + " to the RankedSetOfVMIs");
	//  this.set.add(vmi);	
	  this.list.add(vmi);
	}
	
	public int size(){return this.list.size();}
	
	public void addFromList(List<VMI> l){
		for (VMI v : l) add(v);		
	}
	
	public String toString(){
	    return this.list.toString();
	}
	
	
}
