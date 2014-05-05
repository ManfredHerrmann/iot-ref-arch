package org.wso2.iot.refarch.rpi.agent;
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  WSO2
 * PROJECT       :  WSO2 IoT Reference Architecture - Raspberry Pi Agent
 * FILENAME      :  Agent.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.wso2.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2014 WSO2
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.iot.refarch.rpi.agent.connector.HttpService;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
    The Agent class that will run continuously
 */
public class Agent {

    HttpService httpService;
    {
        /*
            Init block that creates the http service from a config file
        */
        try{
            InputStream is = new FileInputStream("config.properties");
            Properties properties = new Properties();
            properties.load(is);
            System.out.println("Server ip "+properties.getProperty("serverpath"));
            httpService = new HttpService( properties.getProperty("serverpath"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /*
        Reads the information of the Pie and construct a JSON Object
     */
    public JSONObject createInfoObject(){
        JSONObject infoObject = new JSONObject();
        try {

            JSONObject hardwareObject = new JSONObject();
            /* Hardware Information */
            System.out.println("Start device reading");
            hardwareObject.put("cpu_revision", SystemInfo.getCpuRevision());

            System.out.println("Second");
            hardwareObject.put("serial_number", SystemInfo.getSerial());
            hardwareObject.put("cpu_architecture", SystemInfo.getCpuArchitecture());
            hardwareObject.put("cpu_part", SystemInfo.getCpuPart());
            hardwareObject.put("cpu_temperature", SystemInfo.getCpuTemperature());
            hardwareObject.put("cpu_core_voltage", SystemInfo.getCpuVoltage());
            hardwareObject.put("mips", SystemInfo.getSerial());
            hardwareObject.put("processor", SystemInfo.getProcessor());
            hardwareObject.put("hardware_revision", SystemInfo.getRevision());
            hardwareObject.put("is_hard_float_abi", SystemInfo.isHardFloatAbi());
            hardwareObject.put("board_type", SystemInfo.getBoardType().name());
            hardwareObject.put("processor", SystemInfo.getProcessor());
            infoObject.put("hardware_info", hardwareObject);

            /* Memory Information */
            JSONObject memoryObject = new JSONObject();
            memoryObject.put("total_memory", SystemInfo.getMemoryTotal());
            memoryObject.put("used_memory", SystemInfo.getMemoryUsed());
            memoryObject.put("free_memory", SystemInfo.getMemoryFree());
            memoryObject.put("shared_memory", SystemInfo.getMemoryShared());
            memoryObject.put("memory_buffers", SystemInfo.getMemoryBuffers());
            memoryObject.put("cached_memory",  SystemInfo.getMemoryCached());
            memoryObject.put("sdram_c_volate", SystemInfo.getMemoryVoltageSDRam_C());
            memoryObject.put("sdram_i_volate", SystemInfo.getMemoryVoltageSDRam_I());
            memoryObject.put("sdram_p_volate", SystemInfo.getMemoryVoltageSDRam_P());
            infoObject.put("memory_info", hardwareObject);

            /* OS information */
            JSONObject osObject = new JSONObject();
            osObject.put("os_name", SystemInfo.getOsName());
            osObject.put("os_version", SystemInfo.getOsVersion());
            osObject.put("os_architecture", SystemInfo.getOsArch());
            osObject.put("os_firmware_build", SystemInfo.getOsFirmwareBuild());
            osObject.put("os_firmware_date", SystemInfo.getOsFirmwareDate());
            infoObject.put("os_info", osObject);

            /* Java information */
            JSONObject javaObject = new JSONObject();
            javaObject.put("java_vendor", SystemInfo.getJavaVendor());
            javaObject.put("java_vendor_url", SystemInfo.getJavaVendorUrl());
            javaObject.put("java_version", SystemInfo.getJavaVersion());
            javaObject.put("java_vm", SystemInfo.getJavaVirtualMachine());
            javaObject.put("java_runtime", SystemInfo.getJavaRuntime());
            infoObject.put("java_info", javaObject);

            /* Network information */
            JSONObject networkObject = new JSONObject();
            networkObject.put("hostname", NetworkInfo.getHostname());
            JSONArray ipArray = new JSONArray();
            JSONArray fqdnArray = new JSONArray();
            JSONArray nameserverArray = new JSONArray();

            for (String ipAddress : NetworkInfo.getIPAddresses())
                ipArray.add(ipAddress);
            for (String fqdn : NetworkInfo.getFQDNs())
                fqdnArray.add(fqdn);
            for (String nameserver : NetworkInfo.getNameservers())
                nameserverArray.add(nameserverArray);

            networkObject.put("ip_address", ipArray);
            networkObject.put("fqdn", fqdnArray);
            networkObject.put("nameserver", nameserverArray);
            infoObject.put("network_info",networkObject);

            /* Clock information */
            JSONObject clockObject = new JSONObject();
            clockObject.put("arm_frequency", SystemInfo.getClockFrequencyArm());
            clockObject.put("core_frequency", SystemInfo.getClockFrequencyCore());
            clockObject.put("h264_frequency", SystemInfo.getClockFrequencyH264());
            clockObject.put("isp_frequency", SystemInfo.getClockFrequencyISP());
            clockObject.put("v3d_frequency", SystemInfo.getClockFrequencyV3D());
            clockObject.put("uart_frequency", SystemInfo.getClockFrequencyUART());
            clockObject.put("pwm_frequency", SystemInfo.getClockFrequencyPWM());
            clockObject.put("emmc_frequency", SystemInfo.getClockFrequencyEMMC());
            clockObject.put("pixel_frequency", SystemInfo.getClockFrequencyPixel());
            clockObject.put("vec_frequency", SystemInfo.getClockFrequencyVEC());
            clockObject.put("hdmi_frequency", SystemInfo.getClockFrequencyHDMI());
            clockObject.put("dpi_frequency", SystemInfo.getClockFrequencyDPI());
            infoObject.put("clock_info", clockObject);

        }catch(Exception e){
            e.printStackTrace();
        }
        return infoObject;
    }

    public static void main(String[] args) {
        System.out.println("Agent starting");
        Agent agent = new Agent();
        agent.run();
        System.out.println("Agent Process started");
    }
    /*
        Periodically send information using the HttpService
    */
    private class MonitoringDeamon implements Runnable{
        @Override
        public void run() {
            try {
                System.out.println("Agent sending data");
                JSONObject infoObject = createInfoObject();
                httpService.sendPayload(infoObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
        Start a scheduled task for monitoring
     */
    private void run() {
        ScheduledExecutorService dhtReaderScheduler = Executors.newScheduledThreadPool(1);
        dhtReaderScheduler.scheduleWithFixedDelay(new MonitoringDeamon(), 0, 20, TimeUnit.SECONDS);
    }
}
