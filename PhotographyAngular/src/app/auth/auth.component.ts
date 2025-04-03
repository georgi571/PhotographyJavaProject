import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {Router} from '@angular/router';
import {AuthService} from '../services/auth-service/auth.service';

@Component({
  selector: 'app-auth',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent implements OnInit {

    constructor(private authService: AuthService,
                private router: Router) {
    }

    ngOnInit(): void {
        if (this.authService.isAuthenticated()) {
            this.router.navigate(['home']);
        }
    }

}
