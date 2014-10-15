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


/**
 *  This class enables to parse the requirements expression into its minimal parts
 *  For example "os.version>='10.04'" is dissected into the 
 *    attribute (i.e) os.version, 
 *    operator (>=) 
 *    value (10.04)
 *  
 *  
 */
public class ReqTriadParser {
   private String attribute;
   private String operator;
   private String value;
   private String expression;
   
   private static String[] OPERATORS = {"<",">","<=",">=","="};
   
   public ReqTriadParser(String expression) throws UnableToParseException{
	   this.expression = expression;	
	   parse();
   }

   public String getAttribute(){return attribute;}
   
   public String getOperator(){return operator;}
   
   public String getValue(){return value;}
   
   
   private void parse() throws UnableToParseException {
	  boolean parsed = false; 
	  for (int i = 0; i < OPERATORS.length; i++){
		  String[] parts = expression.split(OPERATORS[i]);	
		  if ( parts.length == 2 && parts[1].charAt(0)!='=' && parts[0].charAt(parts[0].length()-1) != '>'){		
			   attribute = parts[0];
			   operator = OPERATORS[i];
			   value = parts[1].replace('\'', ' ').trim();
			   parsed = true;
			   break;
			}					
		}
	  if (!parsed) throw new UnableToParseException("Unable to parse expression: " + expression);
   }

}
