import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {BackgroundComponent} from "../background/background.component";

@Component({
  selector: 'app-main-page',
  imports: [
    RouterLink,
    BackgroundComponent
  ],
  templateUrl: './main-page.component.html',
  standalone: true,
  styleUrl: './main-page.component.css'
})
export class MainPageComponent {

}
