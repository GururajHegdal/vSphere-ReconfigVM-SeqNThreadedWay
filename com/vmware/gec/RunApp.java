/**
 * Utility program to reconfigure VM (Serial & multi threaded way).
 *
 * Serial way:
 * Retrieves all VMs in inventory and perform sequential VM reconfiguration operation.
 *
 * Multi Threaded way:
 * Retrieves all Hosts in inventory. From each host, all registered VMs are retrieved and
 * reconfiguration operation on all VMs is done in parallel (Multi-Threaded manner).
 * Once done, next host in the list would be picked up, for reconfiguring its registered VMs.
 *
 * VM Reconfiguration operation :
 * It’s just a plain reconfigure VM operation without any changes pushed to the VM.
 * Empty VM config spec is created and applied to VM- which does not affect the source vm configuration.
 * Based on the need or requirement, the VM config spec can be tweaked to apply specific changes on VM(s).
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

public class RunApp {

    public static void main(String[] args) {

        String configFileLocation = args[0];

        // Invoke serial way of reconfiguring VMs
        SerialReconfigVM scriptObj = new SerialReconfigVM(configFileLocation);

        // Invoke parallel-threaded way of reconfiguring VMs
        // ThreadedReconfigVM scriptObj= new ThreadedReconfigVM(configFileLocation);

        scriptObj.executeScriptFlow();

    }
}
