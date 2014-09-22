import requests
import json
import platform


class Manager:

    '''
            The Core Manager is responsible for managing platform indepenant management tasks
            also the core manager switches the manager based on the platform type
    '''

    def enroll(self, token):
        '''
                Enrollment process for Device involves calling an API of a server through HTTP
                passing the token. At this time a challenge token will be generated based on hardware.
        '''
        challenge = self.generate_challege()
        properties = self.flatten_device_info(self.device_info())
        properties["platform"] = self.platform()
        properties["version"] = self.version()
        payload = {
            "auth": "token",
            "auth_params": {
                "token": token
            },
            "properties": properties
        }

        payload = json.dumps(payload)
        headers = {'content-type': "application/json"}
        print payload
        response = requests.post(
            "https://10.100.0.151:9443/emm/api/devices/iot/register", headers=headers, data=payload, verify=False)
        print response.text

    def device_properties(self):
        '''
            Device Properties are sent to the Device Manager in each monitoring interval 
        '''
        info = {
            "mac": self.mac()
        }
        return info

    def flatten_device_info(self, device_info):
        '''
            Currently the server side is not robust to display rich information obtained from different types of devices.
            This method flattens the device info. 
        '''
        props = {
            "Python Version": device_info["python_info"]["version"],
            "Python Compiler": device_info["python_info"]["compiler"],
            "Python Build Name": device_info["python_info"]["build"][0],
            "Python Build Date": device_info["python_info"]["build"][1],
            "Kernal Name": device_info["platform"]["normal"],
            "Platform Name": platform_name(),
            "Node": device_info["hardware"]["node"],
            "System": device_info["hardware"]["system"],
            "Machine": device_info["hardware"]["machine"]
        }
        return props
    def device_info(self):
        '''
            Device Info is sent only when the device is getting registered to the Device Manager. This
            method should be used to send the static information about the device. Device Manager can 
            decide to invoke this method from server side if needed
        '''
        props = {
            "python_info": {
                "version": platform.python_version(),
                "version_tuple": platform.python_version_tuple(),
                "compiler": platform.python_compiler(),
                "build": platform.python_build()
            },
            "platform":{
                "normal": platform.platform(),
                "alias": platform.platform(aliased=True),
                "terse":platform.platform(terse=True)
            },
            "os":{
                "name": platform.uname()
            },
            "hardware":{
                "system": platform.system(),
                "node": platform.node(),
                "release": platform.release(),
                "version": platform.version(),
                "machine": platform.machine(),
                "processor": platform.processor()
            }
        }
        return props

def platform_name():
    '''
    The logic of getting the second index item from uname() is incorrect in Mac
    '''
    return platform.uname()[1]

def get_device_manager():
    '''
        TODO: Obtain the device type bundles by reading the dm module. 
    '''
    platform = platform_name()
    platform = "beaglebone"
    if platform=="raspberrypi":
        return RaspberryPiManager()
    elif platform=="beaglebone":
        return BeagleBoneManager()
    return
# Avoiding circular depenency [refer -
# http://effbot.org/zone/import-confusion.htm]
from dm.RaspberryPiManager import RaspberryPiManager
from dm.BeagleBoneManager import BeagleBoneManager
