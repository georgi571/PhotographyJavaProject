import {Component, ViewChild} from '@angular/core';
import {HeaderComponent} from '../core/header/header.component';
import {FooterComponent} from '../core/footer/footer.component';
import {FormsModule, NgForm} from '@angular/forms';
import {Router} from '@angular/router';
import {ApiService} from '../services/api-service/api.service';
import {AuthService} from '../services/auth-service/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [
        HeaderComponent,
        FooterComponent,
        FormsModule
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {

    @ViewChild('loginForm') form: NgForm | undefined;

    constructor(
        private apiService: ApiService,
        private router: Router,
        private authService: AuthService
    ) {
    }

    // login user

    formSubmit() {

        const form = this.form!;

        if (form?.invalid) {
            console.log('This form is invalid!');
            return;
        }

        const formValue = form.value;

        this.apiService.loginUser(formValue).subscribe({
            next: (response) => {
                console.log(response);
                if (response.jwtToken) {
                    const jwtToken = response.jwtToken;
                    this.authService.setToken(jwtToken);
                    this.router.navigate(['home']);
                } else {
                    alert('Invalid credentials, please try again.');
                }
            },
            error: (error) => {
                alert('Invalid credentials, please try again.');
            }
        });
    }
}
