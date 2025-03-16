import { Component } from '@angular/core';
import {FooterComponent} from "../core/footer/footer.component";
import {HeaderComponent} from "../core/header/header.component";
import {Router} from '@angular/router';

@Component({
  selector: 'app-server-down',
    imports: [
        FooterComponent,
        HeaderComponent
    ],
  templateUrl: './server-down.component.html',
  styleUrl: './server-down.component.css'
})
export class ServerDownComponent {
    constructor(private router: Router) {}

    reloadPage() {
        window.location.reload();
    }

    goToHomePage() {
        this.router.navigate(['/home']);
    }
}
