import { Component } from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from '../core/footer/footer.component';

@Component({
  selector: 'app-profile',
  imports: [
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
}
