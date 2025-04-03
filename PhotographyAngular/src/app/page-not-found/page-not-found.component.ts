import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from '../core/footer/footer.component';

@Component({
  selector: 'app-page-not-found',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.css'
})
export class PageNotFoundComponent {

    constructor(private router: Router) {}

    goToHomePage(): void {
        this.router.navigate(['/home']);
    }
}
