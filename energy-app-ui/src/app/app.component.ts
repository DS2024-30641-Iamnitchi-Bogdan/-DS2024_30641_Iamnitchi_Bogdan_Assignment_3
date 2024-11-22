import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MatFormFieldModule} from "@angular/material/form-field";
import {HighchartsChartModule} from "highcharts-angular";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    MatFormFieldModule,
    HighchartsChartModule,
    FormsModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'energy-app-ui';
}
