/**
 * Utility program to reconfigure VM - one VM at a time
 * Retrieves all VMs in inventory and perform sequential VM reconfiguration operation.
 *
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * @author Gururaja Hegdal (ghegdal@vmware.com)
 * @version 1.0
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.vmware.gec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class SerialReconfigVM
{

   private String configFileLocation;
   private String vcip;
   private String userName;
   private String password;
   private String url;
   private ServiceInstance si;

   // constants
   public static final String DC_MOR_TYPE = "Datacenter";
   public static final String CLUSTER_COMPRES_MOR_TYPE = "ClusterComputeResource";
   public static final String VC_ROOT_TYPE = "VCRoot";
   public static final String HOST_MOR_TYPE = "HostSystem";
   public static final String VM_MOR_TYPE = "VirtualMachine";

   public SerialReconfigVM(String propertiesFileLocation)
   {
      this.configFileLocation = propertiesFileLocation;
      makeProperties();
   }

   private void makeProperties()
   {

      InputStream inputStream = null;
      try {
         inputStream = new FileInputStream(configFileLocation);
      } catch (FileNotFoundException e1) {
         try {
            throw new FileNotFoundException("property file '"
                     + configFileLocation + "' not found in the classpath");
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         }
      }

      Properties prop = new Properties();

      if (inputStream != null) {
         try {
            prop.load(inputStream);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      // get the property value and print it out
      System.out.println("######################### Script execution STARTED #########################");
      System.out.println("Reading VC and Credentials data from property file");
      vcip = prop.getProperty("vcip");
      userName = prop.getProperty("username");
      password = prop.getProperty("password");

      if (vcip != null) {
         url = "https://" + vcip + "/sdk";
      }

   }

   public boolean executeScriptFlow()
   {

      // login to vcva
      si = loginVCVA(url);

      assert (si != null);
      System.out.println("Succesfully logged into VC: " + vcip);

      System.out.println("Please wait while VMs list is retrieved for reconfigure ...");
      System.out.println("(This may take time depending on the size of inventory)");
      reconfigVmG();
      try {
         Thread.sleep(1000 * 3);
      } catch (Exception e) {
         // eat out the exception
      }
      System.out.println("######################### Script execution completed #########################");

      return false;
   }

   private ServiceInstance loginVCVA(String url)
   {
      try {
         si = new ServiceInstance(new URL(url), userName, password, true);
      } catch (RemoteException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (MalformedURLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return si;
   }

   /**
    * Reconfigure VMs
    */
   private void reconfigVmG()
   {
      // get first datacenters in the environment.
      InventoryNavigator navigator = new InventoryNavigator(si.getRootFolder());
      ManagedEntity[] allVms = null;
      try {
         allVms = navigator.searchManagedEntities(VM_MOR_TYPE);
      } catch (Exception e) {
         System.err.println("[Error] Unable to retrive the VMs from inventory");
         e.printStackTrace();
         return;
      }
      String vmName;
      for (ManagedEntity tempVm : allVms) {
         try {
            vmName = tempVm.getName();
            VirtualMachine ivm = new VirtualMachine(si.getServerConnection(),
                     tempVm.getMOR());
            /*
             * Create empty vm config spec - we dont want to affect the source vm
             * configuration
             */
            VirtualMachineConfigSpec tempSpec = new VirtualMachineConfigSpec();
            System.out.println("----------------------------------------------");
            System.out.println("Reconfigure VM : \"" + vmName + "\"");
            Task taskWhole = ivm.reconfigVM_Task(tempSpec);
            TaskInfoState taskState = taskWhole.getTaskInfo().getState();

            while (!(taskState.equals(TaskInfoState.success))) {
               if ((taskState.equals(TaskInfoState.error))) {
                  System.err.println("[FAILED] VM:\"" + vmName
                           + "\" Reconfig Task errored out");
                  Thread.sleep(10);
                  break;
               } else {
                  System.out.println("VM:\"" + vmName
                           + "\" Reconfig Task is still running");
               }
               taskState = taskWhole.getTaskInfo().getState();
            }
            if (taskState.equals(TaskInfoState.success)) {
               System.out.println("** VM:\"" + vmName
                        + "\" Reconfig Task is Completed");
            }
         } catch (Exception e) {
            System.err.println("Reconfig task failed");
            e.printStackTrace();
         }
         System.out.println("----------------------------------------------");
      }
   }
}
