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
 * The Application entity enables to represent the applications installed on the VMI
 *
 */
@NamedQueries({ @NamedQuery(name = "Application::getApplicationByNameVersionPath", query = "select app from Application app where app.name = :name and app.version = :version and app.path = :path") })
@Entity
@Table(name = "Application")
public class Application {

	@Id
	@GeneratedValue
	@Column(name = "APP_ID")
	private Long id;

	@Column(name = "APP_NAME", length = 255)
	private String name;

	@Column(name = "VERSION", length = 255)
	private String version;

	@Column(name = "PATH", length = 255)
	private String path;
	
	private transient Logger log;

	public Application() {
	}

	public Application(String name, String version, String path) {
		this.name = name;
		this.version = version;
		this.path = path;
		this.log = Logger.getLogger(getClass());
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean matches(Application app, String operator) {	
		//log.debug("About to match " + this + " with " + app);
		
		if (this.name.equals(app.getName())){
			if (this.version != null && operator != null)
				if (VersionComparator.matchesVersion(this.version, app.getVersion(), operator))
					return true;
				else return false;
		} else return false;
		return true;
	}

	public boolean equals(Object o) {
		String oName, oVersion, oPath;
		if (this == o)
			return true;
		return o instanceof Application && this.name != null && ((oName = ((Application) o).getName()) != null) && this.name.equals(oName) && this.version != null
				&& ((oVersion = ((Application) o).getVersion()) != null) && this.version.equals(oVersion) && this.path != null && ((oPath = ((Application) o).getPath()) != null)
				&& this.path.equals(oPath);
	}

	public int hashCode() {
		return (name + version + path).hashCode();
	}

	public String toString() {
		String str = "";
		str += "Name: " + this.name;
		if (this.version != null) str += " Version: " + this.version;
		if (this.path != null) str += " Path: " + this.path;
		return str;
	}

}
