import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'energy-app',
    pathMatch: 'full'
  },
  {
    path: 'energy-app',
    loadChildren: () => import('./modules/energy-app/energy-app.module').then(m => m.EnergyAppModule),
  },
  {
    path: '**',
    redirectTo: 'energy-app'
  }
];
