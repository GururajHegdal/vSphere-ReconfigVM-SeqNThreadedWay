# vSphere-ReconfigVM-SeqNThreadedWay
## 1. Details
Utility program to reconfigure ESXi Virtual Machines (Serial & multi threaded way).

##### Serial way:
Retrieves all VMs in inventory and perform sequential VM reconfiguration operation.
 
##### Multi Threaded way:
Retrieves all Hosts in inventory. From each host, all registered VMs are retrieved and
reconfiguration operation on all VMs is done in parallel (Multi-Threaded manner).
Once done, next host in the list would be picked up, for reconfiguring its registered VMs.

##### VM Reconfiguration operation :
It’s just a plain reconfigure VM operation without any changes pushed to the VM.
Empty VM config spec is created and applied to VM- which does not affect the source vm configuration.
Based on the need or requirement, the VM config spec can be tweaked to apply specific changes on VM(s).

## 2. How to run the utility program?
i. Import files under the com/vmware/gec folder into your IDE.  
ii. Edit 'config.properties' file and update it with your vCenter Server details (IP address & administrator credentials).  
iii. Run the utility from 'RunApp' program by providing 'config.properties' as an argument.  

Note:  
+ Based on whether you would like to carry out Serial/MultiThreaded Reconfig VM operation. Comment or uncomment the code in 'RunApp.java'
to invoke the respective program.  
* Further based on your requirement, VM Config Spec can be updated with exact configuration changes that you need to apply on each of the VMs.
(for example: change Memory, add vNic, vDisk and etc)  
