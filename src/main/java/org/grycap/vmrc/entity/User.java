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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NaturalId;

/**
 * The User entity enables to represent a registered User on the VMRC Server
 *
 */
@Entity
public class User {
	@Id @GeneratedValue
	@Column(name = "USER_ID")
	private Long id;

	@NaturalId
	@Column(name = "USER_NAME")
	private String userName;

	private String password;

	
	@ManyToOne (cascade=CascadeType.ALL)
	@JoinColumn(name="ACLID")
	private ACL acl;
	

	public User() {
	}

	public User(String userName){
	  this(userName,"");			
	}
	
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.acl = new ACL();
	}

	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ACL getAcl() {
		return acl;
	}

	public void setAcl(ACL acl) {
		this.acl = acl;
	}	
	
	public boolean isAdmin(){
		return this.userName.equals("admin");
	}


	public boolean equals(Object x) {
		return x instanceof User && this.userName.equals(((User) x).getUserName());
	}

	public int hashCode() {
		return (userName + password).hashCode();
	}
	
	public String toString(){
		return "[User: name = " + this.userName + ", password = *******, " + this.acl.toString() + "]";	
	}

}
