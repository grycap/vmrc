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

import java.util.Set;
import org.grycap.vmrc.entity.Application;
import org.grycap.vmrc.entity.VMI;

/**
 * Creates an OVF-compliant XML document out of an VMI definition
 *
 */
public class OVFBuilder {
	private VMI vmi;
	
	public OVFBuilder(VMI vmi){
		this.vmi = vmi;
	}
	
	
	public String toOVF() {
		 // Create the OVF document header.
		 StringBuffer ovfSection = new StringBuffer();
		 ovfSection.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		 ovfSection.append("<Envelope vmw:buildId=\"build-166674\"\n");
		 ovfSection.append("  xmlns=\"http://schemas.dmtf.org/ovf/envelope/1\"\n");
		 ovfSection.append("  xmlns:cim=\"http://schemas.dmtf.org/wbem/wscim/1/common\"\n");
		 ovfSection.append("  xmlns:ovf=\"http://schemas.dmtf.org/ovf/envelope/1\"\n");
		 ovfSection.append("  xmlns:rasd=\"http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_ResourceAllocationSettingData\"\n");
		 ovfSection.append("  xmlns:vmw=\"http://www.vmware.com/schema/ovf\"\n");
		 ovfSection.append("  xmlns:vssd=\"http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2/CIM_VirtualSystemSettingData\"\n");
		 ovfSection.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		 ovfSection.append("  <References>\n");
		 ovfSection.append("    <File ovf:href=\"xxx.vmdk\" ovf:id=\"file1\" />\n");
		 ovfSection.append("  </References>\n");
		 ovfSection.append("  <DiskSection>\n");
		 ovfSection.append("    <Info>Virtual disk information</Info>\n");
		 ovfSection.append("    <Disk ovf:capacity=\"4\" ovf:capacityAllocationUnits=\"byte * 2^30\" ovf:diskId=\"vmdisk1\" ovf:fileRef=\"file1\" ovf:format=\"http://www.vmware.com/interfaces/specifications/vmdk.html#streamOptimized\"/>\n");
		 ovfSection.append("  </DiskSection>\n");
		 ovfSection.append("  <NetworkSection>\n");
		 ovfSection.append("    <Info>The list of logical networks</Info>\n");
		 ovfSection.append("    <Network ovf:name=\"nat\">\n");
		 ovfSection.append("      <Description>The nat network</Description>\n");
		 ovfSection.append("    </Network>\n");
		 ovfSection.append("  </NetworkSection>\n");
		 ovfSection.append("  <VirtualSystem ovf:id=\"xxx\">\n");
		 ovfSection.append("    <Info>A virtual machine</Info>\n");
		 ovfSection.append("    <Name>xxx</Name>\n");

		 // Create OVF application section.
			generateOVFApplicationSection(ovfSection);
			
			// Create OVF system section.
			generateOVFOperatingSystemSection(ovfSection);

			// Create OVF generic section.
			ovfSection.append("    <VirtualHardwareSection>\n");
			ovfSection.append("      <Info>Virtual hardware requirements</Info>\n");

			// Create OVF hypervisor section.
			generateOVFHypervisorSection(ovfSection);

			// Create OVF system section.
			generateOVFCPUSection(ovfSection);
			generateOVFMemory(ovfSection);
			generateOVFDiskSection(ovfSection);

			// Create OVF generic section.
			ovfSection.append("    </VirtualHardwareSection>\n");
			ovfSection.append("  </VirtualSystem>\n");
			ovfSection.append("</Envelope>\n");

			return ovfSection.toString();
		  }
	
	 /**
	   * Fill the Stringbuffer with an OVF section describing the operating system.  
	   * @param ovfSection fill a stringbufer with an OVF section describing a operating system requirement.
	   */
	  private void generateOVFOperatingSystemSection(StringBuffer ovfSection) {
		String osName = this.vmi.getOs().getName();
		String osFlavour = this.vmi.getOs().getFlavour();
		String osVersion = this.vmi.getOs().getVersion();
			
		String userCred = this.vmi.getUserLogin(); 
		String passwordCred = this.vmi.getUserPassword();
		
	    ovfSection.append("    <OperatingSystemSection ovf:id=\"79\" vmw:osType=\"xxx\">\n");
		ovfSection.append("      <Info> W </Info>\n");
		ovfSection.append("      <Description> W </Description>\n");
		ovfSection.append("      <UserCredential>" + userCred + "</UserCredential>\n");
		ovfSection.append("      <PasswordCredential>" + passwordCred + "</PasswordCredential>\n");
		ovfSection.append("      <Type>" + osName + "</Type>\n");
		ovfSection.append("      <Flavour>" + osFlavour + "</Flavour>\n");
		ovfSection.append("      <Version>" + osVersion + "</Version>\n");
		ovfSection.append("    </OperatingSystemSection>\n");
	  }

	  /**
	   * Fill the stringbuffer with an OVF section describing the hypervisor.  
	   * @param ovfSection fill a stringbufer with an OVF section describing the hypervisor assigned to the VM.
	   */
	  private void generateOVFHypervisorSection(StringBuffer ovfSection) {
		String hypervisor = this.vmi.getHypervisor();
		 		  
		ovfSection.append("      <System>\n");
		ovfSection.append("        <vssd:ElementName>Virtual Hardware Family</vssd:ElementName>\n");
		ovfSection.append("        <vssd:InstanceID>0</vssd:InstanceID>\n");
		ovfSection.append("        <vssd:VirtualSystemIdentifier>xxx</vssd:VirtualSystemIdentifier>\n");
		ovfSection.append("        <vssd:VirtualSystemType>" + hypervisor + "</vssd:VirtualSystemType>\n");
		ovfSection.append("      </System>\n");
	  }

	  /**
	   * Fill the stringbuffer with an OVF section describing the cpu.  
	   * @param ovfSection fill a stringbufer with an OVF section describing the cpu.
	   */
	  private void generateOVFCPUSection(StringBuffer ovfSection) {
		String cpuValue = this.vmi.getArch();
		
		ovfSection.append("      <Item>\n");
		ovfSection.append("        <rasd:AllocationUnits>hertz * 10^6</rasd:AllocationUnits>\n");
		ovfSection.append("        <rasd:Description>Number of Virtual CPUs</rasd:Description>\n");
		ovfSection.append("        <rasd:ElementName>1 virtual CPU(s)</rasd:ElementName>\n");
		ovfSection.append("        <rasd:InstanceID>1</rasd:InstanceID>\n");
		ovfSection.append("        <rasd:ResourceType>3</rasd:ResourceType>\n");
		ovfSection.append("        <rasd:Quantity>" + cpuValue + "</rasd:Quantity>\n");
		ovfSection.append("      </Item>\n");
	  }

	  /**
	   * Fill the stringbuffer with an OVF section describing the memory parameters.  
	   * @param ovfSection fill a stringbufer with an OVF section describing the memory parameters.
	   */
	  private void generateOVFMemory(StringBuffer ovfSection) {
		String memValue = "512";		
	
			
		ovfSection.append("      <Item>\n");
		ovfSection.append("        <rasd:AllocationUnits>byte * 2^20</rasd:AllocationUnits>\n");
		ovfSection.append("        <rasd:Description>Memory Size</rasd:Description>\n");
		ovfSection.append("        <rasd:ElementName>" + memValue + " MB of memory</rasd:ElementName>\n");
		ovfSection.append("        <rasd:InstanceID>2</rasd:InstanceID>\n");
		ovfSection.append("        <rasd:ResourceType>4</rasd:ResourceType>\n");
		ovfSection.append("        <rasd:Quantity>" +  memValue + "</rasd:Quantity>\n");
		ovfSection.append("      </Item>\n");
	  }

	  /**
	   * Fill the stringbuffer with an OVF section describing the disk parameters.  
	   * @param ovfSection fill a stringbufer with an OVF section describing the disk parameters.
	   */
	  private void generateOVFDiskSection(StringBuffer ovfSection) {
		String diskValue = this.vmi.getDiskSize();

		ovfSection.append("      <Item>\n");
		ovfSection.append("        <rasd:AddressOnParent>0</rasd:AddressOnParent>\n");
		ovfSection.append("        <rasd:ElementName>disk1</rasd:ElementName>\n");
		ovfSection.append("        <rasd:HostResource>ovf:/disk/vmdisk1</rasd:HostResource>\n");
		ovfSection.append("        <rasd:InstanceID>6</rasd:InstanceID>\n");
		ovfSection.append("        <rasd:Parent>4</rasd:Parent>\n");
		ovfSection.append("        <rasd:ResourceType>17</rasd:ResourceType>\n");
		ovfSection.append("        <rasd:Quantity>" + diskValue + "</rasd:Quantity>\n");
		ovfSection.append("      </Item>\n");
	  }

	  /**
	   * Fill the stringbuffer with an OVF section describing the application requirements.  
	   * @param ovfSection fill a stringbufer with an OVF section describing the application requirements.
	   */
	  private void generateOVFApplicationSection(StringBuffer ovfSection) {
	    //Vector<ReqApplicationData> vApplications = new Vector<ReqApplicationData>();

		Set<Application> s = this.vmi.getApplications();
		  
	   
		for (Application app : s) {
		 		 		 
		  ovfSection.append("    <ProductSection>\n");
		  ovfSection.append("      <Info> </Info>\n");
		  ovfSection.append("      <Product>" + app.getName() + "</Product>\n");
		  ovfSection.append("      <Version>" + app.getVersion() + "</Version>\n");
		  ovfSection.append("      <AppUrl>" + app.getPath() + "</AppUrl>\n");
		  ovfSection.append("    </ProductSection>\n");
		}
	  }

}
