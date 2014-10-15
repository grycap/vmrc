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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.grycap.vmrc.utils.parsing.OVFBuilder;
import org.hibernate.annotations.NaturalId;

/**
 * The VMI entity enables to represent a Virtual Machine Image (VMI) registered on the VMRC Server
 *
 */
@NamedQueries({ @NamedQuery(name = "VMI::getVMIByName", query = "select vmi from VMI vmi where vmi.name = :vmiName") })
@Entity
@Table(name = "VMI")
public class VMI implements Comparable<VMI> {

	@Id
	@GeneratedValue
	@Column(name = "VMI_ID")
	private Long id;

	@NaturalId
	@Column(name = "VMI_NAME", length = 255)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TIMESTAMP", nullable = false, updatable = false)
	private Date timestamp;

	private String hypervisor;

	private String arch;

	private String diskSize;

	private String location;

	private String userLogin;

	private String userPassword;

	private String checksum;
	
	@ManyToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="ACLID")
	private ACL acl;
	
	private String owner;

	// A rank value depends on the specific VMIDescriptor. We don't want this
	// information to end up on the DB
	@Transient
	private int rankValue;

	/**
	 * We don't want to delete the corresponding App rows whenever a VMI is
	 * deleted, since they could be Apps for other VMIs as well.
	 * 
	 * @ManyToMany(cascade = {CascadeType.NONE})
	 */

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "VMI_APP", joinColumns = { @JoinColumn(name = "VMI_ID") }, inverseJoinColumns = { @JoinColumn(name = "APP_ID", insertable = true) })
	private Set<Application> applications = new HashSet<Application>();

	@OneToOne
	@JoinColumn(name = "OS_ID")
	private OS os;


	public VMI() {
		this.timestamp = new Date();
	}
	public VMI(String name) {
		this();
		this.name = name;
		// this.timestamp = new Date();
		this.hypervisor = "unknown";
	}

	public Long getId() {
		return this.id;
	}

	private void setId(Long id) {
		this.id = id;
	}
	
	public Date getTimestamp(){
		return this.timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public void setRankValue(int rankValue) {
		this.rankValue = rankValue;
	}

	public int getRankValue() {
		return rankValue;
	}

	public Set<Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}

	public void addApplication(Application app) {
		if (app == null)
			throw new IllegalArgumentException("UserLog::addJobLog : null value in parameter");
		// app.setVMI(this);
		this.applications.add(app);
	}

	public boolean matchesApplication(Application app, String operator) {
		for (Application a : this.applications) {
			if (a.matches(app, operator))
				return true;
		}
		return false;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof VMI))
			return false;

		final VMI ue = (VMI) o;
		return this.name.equals(ue.name);
	}

	public int compareTo(VMI o) {
		return this.rankValue - o.getRankValue();
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		String str = "";
		str += "Name: " + this.name + " Hypervisor: " + this.hypervisor + " OS: " + this.os + " Timestamp: " + this.timestamp;
		if (this.applications != null)
			str += " | Applications: " + this.applications.toString();
		return str;
	}

	public ACL getAcl() {
		return acl;
	}

	public void setAcl(ACL acl) {
		this.acl = acl;
	}	

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String toOVF(){
		return new OVFBuilder(this).toOVF();
	}

}
