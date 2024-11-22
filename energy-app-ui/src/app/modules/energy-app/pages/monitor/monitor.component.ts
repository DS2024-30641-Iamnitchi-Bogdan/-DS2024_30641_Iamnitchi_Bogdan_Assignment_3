import {Component, CUSTOM_ELEMENTS_SCHEMA, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {DeviceService} from "../../../../services/device/device.service";
import {KeycloakService} from "keycloak-angular";
import {Role} from "../../../../domain/emus";
import {Device} from "../../../../domain/device-types";
import {User} from "../../../../domain/user-types";
import {WebsocketService} from "../../../../services/websocket/websocket.service";
import {MatDialog} from "@angular/material/dialog";
import {DialogComponent} from "../../components/dialog/dialog.component";
import {MatFormFieldModule} from "@angular/material/form-field";

import {MatDatepickerModule,} from "@angular/material/datepicker";
import {MatInputModule} from "@angular/material/input";
import Highcharts from "highcharts";
import {HighchartsChartModule} from "highcharts-angular";
import {FormsModule} from "@angular/forms";
import {GraphData} from "../../../../domain/monitor-types";
import {MonitorService} from "../../../../services/monitor/monitor.service";

@Component({
  selector: 'app-monitor',
  standalone: true,
  imports: [
    NgForOf,
    MatFormFieldModule, MatInputModule, MatDatepickerModule, NgIf, HighchartsChartModule, FormsModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './monitor.component.html',
  styleUrl: './monitor.component.scss'
})
export class MonitorComponent implements OnInit {

  devices: Device[] = [];
  user: User | undefined = undefined;
  currentDialog: any;

  selectedDevice: Device | undefined = undefined;
  selectedDate: Date = new Date();

  chartOptions: any;
  highcharts: typeof Highcharts = Highcharts;

  constructor(
    private keycloakService: KeycloakService,
    private deviceService: DeviceService,
    private monitorService: MonitorService,
    private websocketService: WebsocketService,
    private dialog: MatDialog
  ) {}

  async ngOnInit() {
    await this.keycloakService.getKeycloakInstance().loadUserProfile().then(
      (profile) => {
        this.user = {
          id: profile.id,
          email: profile.email,
          firstName: profile.firstName,
          lastName: profile.lastName,
          role: this.getSingleRole(this.keycloakService.getKeycloakInstance().realmAccess?.roles),
        };
        this.loadDevices(this.user.id || '');
        this.initWebsocket(this.user.id || '');
      });
  }

  selectDevice(device: Device) {
    this.selectedDevice = {...device};
    this.loadGraphData(this.selectedDevice.id, this.selectedDate);
  }

  selectDate() {
    if (this.selectedDate) {
      this.selectedDate = new Date(this.selectedDate.getFullYear(), this.selectedDate.getMonth(), this.selectedDate.getDate()); // Update the selectedDate to store date-only
      this.loadGraphData(this.selectedDevice?.id || '', this.selectedDate);
      console.log(this.selectedDate.toLocaleDateString()); // Logs the date in local format
    }
  }

  initWebsocket(userId: string): void {
    this.websocketService.init(userId);
    this.websocketService.message$.subscribe((message) => {
      if (this.currentDialog) {
        this.currentDialog.close();
      }

      this.currentDialog = this.dialog.open(DialogComponent, {
        data: { message },
        position: { top: '5%' }
      });
    });
  }

  private getSingleRole(roles?: string[]): Role | undefined {
    if (!roles) return undefined; // Return undefined if no roles provided
    const validRoles = roles.filter(role => role === Role.ADMIN || role === Role.USER);
    return validRoles.length > 0 ? validRoles[0] as Role : undefined; // Return the first valid role
  }

  loadDevices(userId: string): void {
    this.deviceService.getDevicesByUserId(userId).subscribe({
      next: (devices) => {
        this.devices = devices;
      },
      error: (error) => {
        console.error(error);
      }
    })
  }

  loadGraphData(deviceId: string, date: Date) {
    this.monitorService.getGraphDataByDate(deviceId, date).subscribe({
      next: (data) => {
        const formattedValues = data.values.map(value => parseFloat(value.toFixed(4)));
        const updatedData = {
          hours: data.hours,
          values: formattedValues
        };
        this.updateChart(updatedData);
      },
      error: (error) => {
        console.error(error);
      }
    })
  }


  updateChart(graphData: GraphData) {
    const formattedDate = this.selectedDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
    this.chartOptions = {
      chart: {
        type: 'spline'
      },
      title: {
        text: `Hourly Consumption for ${formattedDate}`
      },
      xAxis: {
        categories: graphData.hours
      },
      yAxis: {
        title: {
          text: 'Consumption (kWh)'
        }
      },
      plotOptions: {
        spline: {
          dataLabels: {
            enabled: true
          },
          enableMouseTracking: true
        }
      },
      series: [
        {
          name: 'Consumption',
          data: graphData.values
        }
      ]
    };
  }

}
