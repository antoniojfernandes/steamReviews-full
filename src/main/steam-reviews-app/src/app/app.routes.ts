import { Routes } from '@angular/router';
import {MainPageComponent} from './components/main-page/main-page.component';
import {SelectPageComponent} from './components/select-page/select-page.component';
import {FilterPageComponent} from './components/filter-page/filter-page.component';
import {ParamPageComponent} from './components/param-page/param-page.component';

export const routes: Routes = [
  { path: '', component: MainPageComponent },
  { path: 'search-game', component: SelectPageComponent},
  { path: 'search-game/:name/:id', component: SelectPageComponent},
  { path: 'select-params/:name/:id', component: FilterPageComponent},
  { path: 'customize-params/:name/:id', component: ParamPageComponent}

];
