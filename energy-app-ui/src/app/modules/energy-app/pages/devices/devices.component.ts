import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {User} from "../../../../domain/user-types";
import {UserService} from "../../../../services/user/user.service";
import {Device, DeviceUser} from "../../../../domain/device-types";
import {DeviceService} from "../../../../services/device/device.service";
import {NgForOf} from "@angular/common";
import {Router} from "@angular/router";

@Component({
  selector: 'app-devices',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf
  ],
  templateUrl: './devices.component.html',
  styleUrl: './devices.component.scss'
})
export class DevicesComponent implements OnInit {

  private defaultDevice: Device = {
    id: '',
    name: '',
    location: '',
    description: '',
    max_consumption: 0,
    avg_consumption: 0,
    user_id: '',
  }
  private defaultDeviceUser: DeviceUser = {
    id: '',
    name: '',
    email: ''
  }

  users: DeviceUser[] = [];
  selectedDeviceUser: DeviceUser = this.defaultDeviceUser;
  devices: Device[] = [];
  selectedDevice: Device = this.defaultDevice;

  deviceUserId: string = '';

  constructor(
    private deviceService: DeviceService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadDeviceUsers();
    this.loadDevices()
  }

  async navigateToDevices() {
    await this.router.navigate(['/energy-app/users']);
  }

  selectUser(deviceUser: any) {
    this.selectedDeviceUser = { ...deviceUser };
    this.deviceUserId = deviceUser.id;
    this.selectedDevice.user_id = deviceUser.id;
  }

  selectDevice(device: any) {
    this.selectedDevice = { ...device };
    this.deviceUserId = device.user_id;
  }

  loadDeviceUsers(): void {
    this.deviceService.getAllDeviceUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  loadDevices(): void {
    this.deviceService.getAllDevices().subscribe({
      next: (devices) => {
        this.devices = devices;
        console.log(devices);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  refresh() {
    this.selectedDevice = this.defaultDevice;
    this.loadDeviceUsers();
  }

  addDevice() {
    this.deviceService.addDevice(this.selectedDevice).subscribe({
      next: (device) => {
        this.devices.push(device);
        this.selectedDevice = this.defaultDevice;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  updateDevice() {
    this.deviceService.updateDevice(this.selectedDevice).subscribe({
      next: (device) => {
        this.devices = this.devices.map((d) => {
          if (d.id === device.id) {
            return device;
          }
          return d;
        });
        this.selectedDevice = this.defaultDevice;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  deleteDevice() {
    this.deviceService.deleteDevice(this.selectedDevice.id || '').subscribe({
      next: () => {
        this.devices = this.devices.filter((d) => d.id !== this.selectedDevice.id);
        this.selectedDevice = this.defaultDevice;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
