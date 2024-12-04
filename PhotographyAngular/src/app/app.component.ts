import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HeaderComponent} from './core/header/header.component';
import {IntroComponent} from './intro/intro.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, IntroComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PhotographyAngular';
}
