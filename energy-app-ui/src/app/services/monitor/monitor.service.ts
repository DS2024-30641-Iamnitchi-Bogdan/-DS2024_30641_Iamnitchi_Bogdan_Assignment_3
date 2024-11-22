import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {GraphData} from "../../domain/monitor-types";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class MonitorService {

  private monitorURL = environment.monitorURL;
  private monitorPath = environment.monitorPath;

  constructor(private http: HttpClient) { }

  getGraphDataByDate(deviceId: string, date: Date): Observable<GraphData> {
    const formattedDate = this.formatDate(date);
    return this.http.get<GraphData>(
      `${this.monitorURL}${this.monitorPath}/graph?deviceId=${deviceId}&date=${formattedDate}`
    );
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

}
