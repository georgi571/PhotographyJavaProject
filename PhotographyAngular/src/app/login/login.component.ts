import { Component } from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from '../core/footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [
    HeaderComponent,
    FooterComponent,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

}
