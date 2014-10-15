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
package org.grycap.vmrc.model;

import org.grycap.vmrc.entity.User;
import org.grycap.vmrc.entity.VMI;

public enum VMIOperation {
	ADD("add"),
	UPLOAD("upload"),
	SEARCH("search"),
	LIST("list"),
	DOWNLOAD("download"),
	DELETE("delete")
	;
	
	
	private String operationName;

	private VMIOperation(String operationName) {
		this.operationName = operationName;
	}
	public String getOperationName() { return operationName; }
	public void setOperationName(String operationName) { this.operationName = operationName; }
	public String getPermissionValue(VMI vmi) {
		String permValue = null;
		switch (this) {
		case ADD:
			permValue = vmi.getAcl().getAddPerm();
			break;

		case DOWNLOAD:
			permValue = vmi.getAcl().getDownloadPerm();
			break;
			
		case DELETE:
			permValue = vmi.getAcl().getDeletePerm();
			break;
			
		case SEARCH:
			permValue = vmi.getAcl().getSearchPerm();
			break;
			
		case UPLOAD:
			permValue = vmi.getAcl().getUploadPerm();
			break;
			
		default:
			break;
		}
		return permValue;
	}
	
	
	public String getPermissionValue(User user) {
		String permValue = null;
		switch (this) {
		case ADD:
			permValue = user.getAcl().getAddPerm();
			break;

		case DOWNLOAD:
			permValue = user.getAcl().getDownloadPerm();
			break;
			
		case DELETE:
			permValue = user.getAcl().getDeletePerm();
			break;
			
		case SEARCH:
			permValue = user.getAcl().getSearchPerm();
			break;
			
		case UPLOAD:
			permValue = user.getAcl().getUploadPerm();
			break;
			
		default:
			break;
		}
		return permValue;
	}
}
