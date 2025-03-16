import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from '../core/footer/footer.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-auth-required',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './auth-required.component.html',
  styleUrl: './auth-required.component.css'
})
export class AuthRequiredComponent {
    constructor(private router: Router) {}

    goToLogin(): void {
        this.router.navigate(['/users/login']);
    }
}
