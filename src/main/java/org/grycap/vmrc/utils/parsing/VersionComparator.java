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

public class VersionComparator {

	/**
	 * Indicates whether the version v1 matches the version v2 according to the operator specified.
	 * 
	 * For example, version 1.5.1 matches 1.6.0_07 considering the operator <=
	 * 
	 * @param s1
	 * @param s2
	 * @param operator
	 * @return
	 */
	public static boolean matchesVersion(String v1, String v2, String operator){
		boolean matches = false;
		int cmp = compareVersions(v1,v2);
		if (operator.equals("=")) return cmp == 0;
		if (operator.equals("<")) return cmp < 0;
		if (operator.equals("<=")) return cmp <= 0;
		if (operator.equals(">=")) return cmp >= 0;
		if (operator.equals(">")) return cmp > 0;
		
		return false;
	}
	
	 /**
	   * Compares versions of softwares
	   * 1.5.1 < 1.6.0_6 < 1.6.0_07 < 1.6.0_07-b06 < 1.6.0_07-b07 < 1.6.0_08-a06 < 2.0 < 2.0.0.16 < 5.10 < 9.0.0.0 <10.1.2.0 < Generic_127127-11
	   * @param s1
	   * @param s2
	   * @return
	   *  
	   */
	  public static int compareVersions(String s1, String s2){
	      if( s1 == null && s2 == null )
	          return 0;
	      else if( s1 == null )
	          return -1;
	      else if( s2 == null )
	          return 1;

	      String[]
	          arr1 = s1.split("[^a-zA-Z0-9]+"),
	          arr2 = s2.split("[^a-zA-Z0-9]+")
	      ;

	      int i1, i2, i3;

	      for(int ii = 0, max = Math.min(arr1.length, arr2.length); 
	      ii <= max; ii++){
	          if( ii == arr1.length )
	              return ii == arr2.length ? 0 : -1;
	          else if( ii == arr2.length )
	              return 1;

	          try{
	              i1 = Integer.parseInt(arr1[ii]);
	          }
	          catch (Exception x){
	              i1 = Integer.MAX_VALUE;
	          }

	          try{
	              i2 = Integer.parseInt(arr2[ii]);
	          }
	          catch (Exception x){
	              i2 = Integer.MAX_VALUE;
	          }

	          if( i1 != i2 ){
	              return i1 - i2;
	          }

	          i3 = arr1[ii].compareTo(arr2[ii]);

	          if( i3 != 0 )
	              return i3;
	      }

	      return 0;
	  }
	  
	  public static void main(String[] args){
		 System.out.println(matchesVersion("1.5.1", "1.6.3", ">"));  
		 System.out.println(matchesVersion("10.0", "1", ">="));
	  
	  }
	  
	
}
