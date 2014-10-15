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
package org.grycap.vmrc.utils.parsing;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.grycap.vmrc.entity.Application;
import org.grycap.vmrc.entity.OS;
import org.grycap.vmrc.entity.VMI;


public class VMIDescriptor {
	private Hashtable<String, List<VMIStatement>> descHard;
	private Hashtable<String, List<VMIStatement>> descSoft;
	private transient Logger log;

	public VMIDescriptor() {
		this.log = Logger.getLogger(getClass());
		initTables();
	}

	private void initTables() {
		this.descHard = new Hashtable<String, List<VMIStatement>>();
		this.descSoft = new Hashtable<String, List<VMIStatement>>();
	}

	public VMI parseVMIFromString(String vmiDesc) throws Exception {
		initTables();
		parseFromString(vmiDesc);
		return toVMI();

	}

	public void parseFromFile(String fileName) throws Exception {
		String vmiDesc = loadFromFile(fileName);
		parseFromString(vmiDesc);
	}

	public void parseFromString(String vmiDesc) throws Exception {
		Scanner scanner = new Scanner(vmiDesc);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			VMIStatement vs = parseVMIStatement(line);
			insertStatement(vs);
		}
	}
	
	public Hashtable<String, List<VMIStatement>> getHardRequirements(){
		return this.descHard;
	}

	protected VMIStatement parseVMIStatement(String line) throws Exception {		
		try{
			log.debug("About to parse VMI Descriptor line: " + line);
			VMIStatement vs = new VMIStatement(line);
			log.debug("Successfully parsed VMI Descriptor line: " + line);
			return vs;
		}catch(UnableToParseException ex){
			log.error("Could not parse VMIStatement " + line + ". Reason: " + ex.getMessage());
			throw ex;
		}
		
	}

	private void insertStatement(VMIStatement vp) {
		Hashtable<String, List<VMIStatement>> ht = vp.isSoft() ? this.descSoft : this.descHard;
		List<VMIStatement> l = ht.get(vp.getAttribute());
		if (l == null) {
			l = new ArrayList<VMIStatement>();
		}

		l.add(vp);
		ht.put(vp.getAttribute(), l);
	}

	private String loadFromFile(String textFilePath) throws IOException {
		String result;

		FileInputStream fis = new FileInputStream(textFilePath);
		byte[] b = new byte[fis.available()];
		fis.read(b);
		fis.close();
		result = new String(b);

		return result;
	}

	private String toString(Hashtable<String, List<VMIStatement>> ht) {
		String str = "";
		Enumeration<String> e = ht.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			List<VMIStatement> l = ht.get(key);
			for (VMIStatement v : l) {
				str += v + "\n";
			}
		}
		return str;
	}

	public String toString() {
		return toString(this.descHard) + toString(this.descSoft);
	}
	
	public void setName(String vmiName){
	   List<VMIStatement> l = this.descHard.get("system.name");
	   l.get(0).setValue(vmiName);
	}
	
	private String getAttributeValue(String attr) {
		String value = "";
		List<VMIStatement> l = this.descHard.get(attr);
		if (l == null) {
			log.error("NULL");
		} else {
			value = l.get(0).getValue().toString();
		}
		return value;
	}
	
	private boolean hasAttributeValue(String attr){
		return getAttributeValue(attr) != null;
	}


	public VMI toVMI() {
		VMI vmi = new VMI();

		if(hasAttributeValue("system.name")) vmi.setName(getAttributeValue("system.name"));		
		if (hasAttributeValue("system.hypervisor")) vmi.setHypervisor(getAttributeValue("system.hypervisor"));
		
		if (hasAttributeValue("system.location")) vmi.setLocation(getAttributeValue("system.location"));
		
		if (hasAttributeValue("cpu.arch")) vmi.setArch(getAttributeValue("cpu.arch"));
		if (hasAttributeValue("disk.size")) vmi.setDiskSize(getAttributeValue("disk.size"));

		if (hasAttributeValue("disk.os.name")){
			OS os = new OS();
			os.setName(getAttributeValue("disk.os.name"));
			os.setFlavour(getAttributeValue("disk.os.flavour"));
			os.setVersion(getAttributeValue("disk.os.version"));
			vmi.setOs(os);
		}

		if (hasAttributeValue("disk.os.credentials.user")) vmi.setUserLogin(getAttributeValue("disk.os.credentials.user"));
		if (hasAttributeValue("disk.os.credentials.password")) vmi.setUserPassword(getAttributeValue("disk.os.credentials.password"));

				
		List<VMIStatement> l = this.descHard.get("disk.applications");
		if (l!=null) {
			Set<Application> setApps = new HashSet<Application>();
			for (VMIStatement vs : l) {
				setApps.add((Application) vs.getValue());
			}
			vmi.setApplications(setApps);
		}

		return vmi;
	}

	public int rankVMI(VMI vmi) {

		List<VMIStatement> l;
		int rankValue = 0;

		log.debug("Ranking VMI " + vmi.getName());

		// Check disk.os.version
		boolean satisfiesOsVersion = false;
		if ((l = this.descHard.get("disk.os.version")) != null) {
			VMIStatement vs = l.get(0);
			if (vmi.getOs().matchesVersion(vs.getValue().toString(), vs.getOperator())) {
				log.info("VMI satisfies the VMIDescriptor for disk.os.version");
				satisfiesOsVersion = true;
			}
		} else satisfiesOsVersion = true;

		// Check hard apps
		boolean satisfiesHardApps = true;
		log.debug("Checking if " + vmi.getName() + " satisfies the application hard requirements"); 
		if ((l = this.descHard.get("disk.applications")) != null) {
			for (VMIStatement vs2 : l) {
				log.debug("Checking statement " + vs2);
				Application app = (Application) vs2.getValue();
				if (!vmi.matchesApplication(app, vs2.getOperator())) {
					log.debug("Statement " + vs2 + " cannot be met by VMI " + vmi.getName());
					satisfiesHardApps = false;
					break;
				}
			}
		}

		if (!satisfiesHardApps || !satisfiesOsVersion)
			return -1;

		/*
		 * Checking soft requirements. Currently only soft requirements for apps
		 * are considered
		 */
		// Check hard apps
		// boolean satisfiesSoftApps = true;
		if ((l = this.descSoft.get("disk.applications")) != null) {
			for (VMIStatement vs2 : l) {
				log.debug("Checking soft VMIStatement " + vs2);
				Application app = (Application) vs2.getValue();
				if (vmi.matchesApplication(app, vs2.getOperator())) {
					log.debug("The Rank Value of the VMIStatement is: " + vs2.getRankValue());
					rankValue += vs2.getRankValue();
				}
			}
		}

		return rankValue;
	}

}
