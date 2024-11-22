import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Device, DeviceUser} from "../../domain/device-types";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  private deviceHost = environment.deviceURL;
  private devicesPath = environment.devicesPath;
  private deviceUsersPath = environment.deviceUsersPath;

  constructor(private httpClient: HttpClient) { }

  getAllDeviceUsers(): Observable<DeviceUser[]> {
    const url = `${this.deviceHost}${this.deviceUsersPath}`;
    return this.httpClient.get<DeviceUser[]>(url);
  }

  getAllDevices(): Observable<Device[]> {
    const url = `${this.deviceHost}${this.devicesPath}`;
    return this.httpClient.get<Device[]>(url);
  }

  getDevicesByUserId(userId: string): Observable<Device[]> {
    const url = `${this.deviceHost}${this.devicesPath}/user/${userId}`;
    return this.httpClient.get<Device[]>(url);
  }

  addDevice(device: Device): Observable<Device> {
    const url = `${this.deviceHost}${this.devicesPath}`;
    return this.httpClient.post<Device>(url, device);
  }

  updateDevice(device: Device): Observable<Device> {
    const url = `${this.deviceHost}${this.devicesPath}/${device.id}`;
    return this.httpClient.put<Device>(url, device);
  }

  deleteDevice(deviceId: string): Observable<void> {
    const url = `${this.deviceHost}${this.devicesPath}/${deviceId}`;
    return this.httpClient.delete<void>(url);
  }

}
