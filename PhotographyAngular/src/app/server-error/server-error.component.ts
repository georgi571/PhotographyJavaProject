import { Component } from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from '../core/footer/footer.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-server-error',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './server-error.component.html',
  styleUrl: './server-error.component.css'
})
export class ServerErrorComponent {
    constructor(private router: Router) {}

    goToHomePage() {
        this.router.navigate(['/home']);
    }
}
