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
            const payload = token.split('.')[1]; // Get the payload part of the token
            const decodedPayload = atob(payload); // Decode it using atob (base64 to string)
            this.decodedToken = JSON.parse(decodedPayload); // Parse it to JSON

            console.log(this.decodedToken); // Show the decoded payload
        } else {
            console.log('No token found');
        }
        console.log(this.authService.getToken()?.valueOf());
    }


}
