import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {AuthService} from '../../../services/auth-service/auth.service';

@Component({
    selector: 'app-edit-password',
    standalone: true,
    imports: [
        FormsModule,
        ReactiveFormsModule,
        NgClass
    ],
    templateUrl: './edit-password.component.html',
    styleUrl: './edit-password.component.css'
})
export class EditPasswordComponent {
    errorMessage: string | null = null;

    passwordDetails = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    fieldErrors: { [key: string]: string } = {};

    constructor(private http: HttpClient,
                private authService: AuthService,
                private profile: ProfileService,
                private router: Router) {
    }

    onSubmitPassword() {
        this.profile.editUserPasswordDetail(this.passwordDetails).subscribe({
            next: (response) => {
                this.errorMessage = null;
                this.fieldErrors = {};
                this.authService.clearToken();
                this.router.navigate(['/']);
            },
            error: (error) => {
                if (error.status === 400 && typeof error.error === 'object') {
                    this.fieldErrors = error.error;
                } else {
                    this.errorMessage = 'Wrong password please try again!';
                    alert("Wrong password please try again!");
                    window.location.reload();
                }
            }
        });
    }
}
