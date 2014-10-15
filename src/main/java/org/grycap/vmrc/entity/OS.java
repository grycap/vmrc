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
package org.grycap.vmrc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.grycap.vmrc.utils.parsing.VersionComparator;

/**
 * The OS entity enables to represent the Operating System (OS) installed on the VMI
 *
 */
@NamedQueries({ @NamedQuery(name = "OS::getOSByNameFlavourVersion", query = "select os from OS os where os.name = :name and os.flavour = :flavour and os.version = :version") })
@Entity
@Table(name = "OS")
public class OS {

	@Id @GeneratedValue
	@Column(name = "OS_ID")
	private Long id;

	private String name;

	private String flavour;

	private String version;

	private transient Logger log;

	public OS() {
		this.log = Logger.getLogger(getClass());
	}

	public OS(String name, String flavour, String version) {
		this.name = name;
		this.flavour = flavour;
		this.version = version;
	}

	public Long getId() {
		return this.id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFlavour() {
		return flavour;
	}

	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean matchesVersion(String version, String operator) {

		boolean b = VersionComparator.matchesVersion(getVersion(), version, operator);
		log.debug("About to see if " + this + " matches " + " version: " + version + " with operator " + operator + ":" + b);
		return b;
	}

	public boolean equals(Object x) {
		return x instanceof OS && this.name.equals(((OS) x).getName()) && this.flavour.equals(((OS) x).getFlavour()) && this.version.equals(((OS) x).getVersion());
	}

	public int hashCode() {
		return (name + flavour + version).hashCode();
	}

	public String toString() {
		return "(" + this.name + "," + this.flavour + "," + this.version + ")";
	}

}
