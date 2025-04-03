import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from "../core/footer/footer.component";
import {AuthService} from '../services/auth-service/auth.service';

@Component({
  selector: 'app-home',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

    decodedToken: any;

    constructor(
        private authService: AuthService,
    ) {

    }

    ngOnInit(): void {
        const token = this.authService.getToken();

        if (token) {
            const payload = token.split('.')[1];
            const decodedPayload = atob(payload);
            this.decodedToken = JSON.parse(decodedPayload);
        }
    }


}
