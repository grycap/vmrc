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
import javax.persistence.Table;

/**
 * The ACL (Access Control List) entity enables to represent the permission model
 * employed for both the Users and the VMIs
 * @see User 
 * @see VMI
 */
@Entity 
@Table(name = "ACL")
public class ACL {
	@Id @GeneratedValue
	@Column(name = "ACL_ID")
	private Long id;

	private String addPerm;

	private String uploadPerm;

	private String searchPerm;
	
	private String listPerm;

	private String downloadPerm;

	private String deletePerm;
	
	public final static int DEFAULT_USER = 0;
	public final static int ADMIN_USER = 1;
	public final static int ANONYMOUS_USER = 2;
	

	public ACL() {
		this(DEFAULT_USER);
	}
	
	public ACL(int type){
		switch(type){
		case DEFAULT_USER: 
			this.addPerm = this.listPerm = this.downloadPerm =  this.deletePerm = this.uploadPerm = this.searchPerm  = "owner";
			break;
		case ADMIN_USER:
			this.addPerm = this.listPerm = this.downloadPerm =  this.deletePerm = this.uploadPerm = this.searchPerm  = "all";
			break;
		case ANONYMOUS_USER:			
			this.listPerm = this.searchPerm = "all";
			this.addPerm = this.uploadPerm = this.downloadPerm = this.deletePerm =  "none";
			break;			 
		}
	}
	
	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getAddPerm() {
		return addPerm;
	}

	public void setAddPerm(String add) {
		this.addPerm = add;
	}

	public String getUploadPerm() {
		return uploadPerm;
	}

	public void setUploadPerm(String upload) {
		this.uploadPerm = upload;
	}

	public String getSearchPerm() {
		return searchPerm;
	}

	public void setSearchPerm(String search) {
		this.searchPerm = search;
	}
	
	public String getListPerm() {
		return listPerm;
	}

	public void setListPerm(String list) {
		this.listPerm = list;
	}

	public String getDownloadPerm() {
		return downloadPerm;
	}

	public void setDownloadPerm(String download) {
		this.downloadPerm = download;
	}

	public String getDeletePerm() {
		return deletePerm;
	}

	public void setDeletePerm(String remove) {
		this.deletePerm = remove;
	}

	public boolean equals(Object x) {
		return x instanceof ACL && this.addPerm.equals(((ACL) x).getAddPerm()) && this.uploadPerm.equals(((ACL) x).getUploadPerm()) && this.searchPerm.equals(((ACL) x).getSearchPerm())
				&& this.downloadPerm.equals(((ACL) x).getDownloadPerm()) && this.deletePerm.equals(((ACL) x).getDeletePerm());
	}

	@Override
	public String toString() {
		return "ACL [id=" + id + ", add=" + addPerm + ", upload=" + uploadPerm + ", search=" + searchPerm + ", download=" + downloadPerm + ", remove=" + deletePerm + "]";
	}

}
