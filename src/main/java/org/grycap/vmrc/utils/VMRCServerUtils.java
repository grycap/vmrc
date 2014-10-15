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
package org.grycap.vmrc.utils;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class VMRCServerUtils {	
	private static String NUMBERS = "0123456789";
	private static String LETTERSUP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String LETTERDOWN = "abcdefghijklmnopqrstuvwxyz";

	 
	 public static String generateRandomChain(int length) {
		    String pswd = "";
		    String key = NUMBERS + LETTERSUP + LETTERDOWN;
			for (int i = 0; i < length; i++) pswd+=(key.charAt((int)(Math.random() * key.length())));
		    return pswd;
		  }
	 
	 public static String getPublicIP() {
			String ip = "127.0.0.1";
		    try {
				for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) { 
				  NetworkInterface iface = ifaces.nextElement(); 
				  for (Enumeration<InetAddress> addresses = iface.getInetAddresses(); addresses.hasMoreElements(); ) { 
				    InetAddress address = addresses.nextElement();
				    
				    ip = address.getHostAddress();
				    
				    /**
				     * Checks for IPV4 address since Apache commons FTP does not appear to support IPV6 (as of early 2012)
				     */
				    if (address instanceof Inet4Address && !ip.startsWith("192") && !ip.startsWith("127") && !ip.startsWith("0")) return ip;
				    
				  } 
				}
			} catch (SocketException e) {
			  e.printStackTrace();
			}
		    return ip;
		  }
	 
	 /**
	   * @param args
	   */
	  public static String[] listDirectory(String srcDirectory) {
	    File dir = new File(srcDirectory);
	    String[] files = null;
	    if (! dir.isDirectory() ) return files;
	    else {
	      files = dir.list();
	      return files;
	    }
	  }
}
