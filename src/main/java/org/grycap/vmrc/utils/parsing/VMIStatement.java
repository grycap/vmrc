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


import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.grycap.vmrc.entity.Application;

public class VMIStatement {
	protected String attribute;
	protected String operator;
	protected Object value;
	private int rankValue;	
	private String expression;
	private Logger log;
	
	  
	private static String[] OPERATORS = {"<",">","<=",">=","="};
	
	private static Hashtable<String,String> htLangMap;
	  
	public VMIStatement(String expression) throws UnableToParseException{
		this.log = Logger.getLogger(getClass());
		this.rankValue =  (expression.startsWith("soft") ? new Integer(expression.split(" ")[1]).intValue() : -1);	
		this.expression = expression;
		htLangMap = new Hashtable<String,String>();
		populateLangMap(htLangMap);
		if (this.expression.contains("disk.applications"))
			parseApplication();
		else parse();
	  }
	
	private boolean isValidAttribute(String attrName){
		return this.htLangMap.containsKey(attrName);
	}

	/**
	 * Mapping between the VMI description language attributes and 
	 * the Hibernate entity names in an HQL query.
	 * 
	 * Assume vmi as VMI, os as OS, app as Application
	 * @param ht
	 */
	private void populateLangMap(Hashtable<String,String> ht){
		ht.put("system.name", "vmi.name");	 
		ht.put("system.hypervisor", "vmi.hypervisor");	
		ht.put("system.location", "vmi.location");			
	  	ht.put("cpu.arch", "vmi.arch");
		ht.put("cpu.count", "os.name");
		ht.put("disk.size", "vmi.diskSize");
		ht.put("disk.os.name", "os.name");
		ht.put("disk.os.name", "os.name");
		ht.put("disk.os.flavour", "os.flavour");
		ht.put("disk.os.version", "os.version");
		ht.put("disk.os.credentials.user", "vmi.userLogin");
		ht.put("disk.os.credentials.password", "vmi.userPassword");
		ht.put("disk.applications", "vmi.applications");
	}
	
	  public boolean isSoft(){return rankValue != -1;}
	  	  
	  public String getAttribute(){return attribute;}
	  
	  public String getOperator(){return operator;}
	  
	  public Object getValue(){return value;}
	  
	  public void setValue(Object value){
		  this.value = value;
	  }
	  
	  /**
		 * @return the rankValue
		 */
		public int getRankValue() {
			return rankValue;
		}

		/**
		 * @param rankValue the rankValue to set
		 */
		public void setRankValue(int rankValue) {
			this.rankValue = rankValue;
		}
	  
	  private void parseApplication() throws UnableToParseException{
		  try{
			  String appName = "", appVersion = "", appPath = "";
			  this.attribute = "disk.applications";
			  int offset = 0;
		  
			  if (expression.startsWith("soft")) offset = 2;
			  
			  String[] tokens = expression.split(" ");
			  appName = filter(tokens[offset + 4]);
			  if (tokens.length >= offset + 7){
				  this.operator = tokens[offset + 6];
				  appVersion = filter(tokens[offset + 7]);
			  }
			  if (tokens.length >= offset + 10){
				  appPath = filter(tokens[offset + 10]);
			  }
		  
			  this.value = new Application(appName, appVersion, appPath);
		  }catch(Exception ex){
			  String msg = "Could not parse application related expression: " + expression; 
			  log.error(msg);
			  throw new UnableToParseException(msg,ex);
		  }		
	  }
	  	  
	  //Removes unwanted characters
	  private String filter(String str){
		  return str.replace(')', ' ').replace(',',' ').trim();
	  }
	  
	  private void parse() throws UnableToParseException {
		  boolean parsed = false; 
		  for (int i = 0; i < OPERATORS.length; i++){
			  String[] parts = expression.split(OPERATORS[i]);	
			  if ( parts.length == 2 && parts[1].charAt(0)!='=' && parts[0].charAt(parts[0].length()-1) != '>'){		
				   attribute = parts[0].trim();
				   if (!isValidAttribute(attribute)){
					   String msg = "Unknown attribute " + attribute + ". Check the VMI definition language syntax."; 
					   log.error(msg);
					   throw new UnableToParseException(msg);
				   }
				   operator = OPERATORS[i];
				   value = parts[1].replace('\'', ' ').trim();
				   parsed = true;
				   break;
				}					
			}
		  if (!parsed) throw new UnableToParseException("Unable to parse expression: " + expression);
	  }
	
	  public String toString(){
		  String str;
		  str = isSoft() ? ("soft " + this.rankValue + " ") : "";
		  if (this.attribute.equals("disk.applications")){
			  Application app = (Application) this.value;
			  str += "disk.applications contains (name = " +  app.getName() + ", version " + this.operator + " " + app.getVersion() + ", path = " + app.getPath() + ")";
		  } else{
			  str +=  this.attribute + " " + this.operator + " " + this.value;
		  }
		  return str;
		  
		  
	  }
	  
	  public String toHQLClause(){
		  /**
		   * We don't want to generate HQL code for the soft attributes and for the attributes which might involve complex version numberings
		   * Since we can't compare version numbers through SQL, we have to programmatically do this
		   */
		if ( 	isSoft() ||
				this.attribute.equals("disk.applications") ||
				this.attribute.equals("disk.os.version"))
				return null; 
		else
			return htLangMap.get(this.attribute) + " " + this.operator + " '" + this.value + "'";
	  

	  }

}
