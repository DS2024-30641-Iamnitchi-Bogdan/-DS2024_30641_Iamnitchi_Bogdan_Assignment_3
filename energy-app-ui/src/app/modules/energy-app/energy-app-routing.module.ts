import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {HomeComponent} from "./pages/home/home.component";
import {AppAuthGuard} from "../../services/guard/auth.guard";
import {UsersComponent} from "./pages/users/users.component";
import {DevicesComponent} from "./pages/devices/devices.component";
import {MonitorComponent} from "./pages/monitor/monitor.component";
import {ChatComponent} from "./pages/chat/chat.component";

const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        component: HomeComponent
      },
      // just for user
      {
        path: 'monitor',
        component: MonitorComponent,
        canActivate: [AppAuthGuard],
        data: { roles: ['USER'] }
      },
      // just for admin
      {
        path: 'users',
        component: UsersComponent,
        canActivate: [AppAuthGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'devices',
        component: DevicesComponent,
        canActivate: [AppAuthGuard],
        data: { roles: ['ADMIN'] }
      },
      // both user and admin
      {
        path: 'chat',
        component: ChatComponent,
        canActivate: [AppAuthGuard],
        data: { roles: ['USER', 'ADMIN'] }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EnergyAppRoutingModule { }
