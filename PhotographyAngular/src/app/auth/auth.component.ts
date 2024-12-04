import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";

@Component({
  selector: 'app-auth',
    imports: [
        HeaderComponent
    ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {

}
