import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {ApiService} from '../../services/api-service/api.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {confirmPasswordValidator, dateNotInFutureValidator} from './validators';
import {FooterComponent} from '../../core/footer/footer.component';

@Component({
    selector: 'app-registration',
    standalone: true,
    imports: [
        HeaderComponent,
        FormsModule,
        ReactiveFormsModule,
        FooterComponent,
    ],
    templateUrl: './registration.component.html',
    styleUrl: './registration.component.css'
})
export class RegistrationComponent implements OnInit {
    countries: string[] = [];

    registerForm = new FormGroup({
            username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
            email: new FormControl('', [Validators.required, Validators.email]),
            password: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
            confirmPassword: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]),
            country: new FormControl('', [Validators.required]),
            city: new FormControl('', [Validators.required, Validators.minLength(1)]),
            gender: new FormControl('', [Validators.required]),
            birthDate: new FormControl('', [Validators.required, dateNotInFutureValidator]),
        },
        {validators: confirmPasswordValidator},
    );

    constructor(private apiService: ApiService, private router: Router,) {
    }

    // Get all country names from the BE

    ngOnInit() {
        this.apiService.getCountries().subscribe((response: any) => {
            if (response && response.countries) {
                this.countries = response.countries;
            }
        });
    }

    // Send data from form to BE
    // If response 200 - go to Login Page
    // If response 400 - show errors in Registration form

    onSubmit() {
        if (this.registerForm.valid) {
            this.apiService.registerUser(this.registerForm.value).subscribe({
                next: (response) => {
                    console.log('Registration successful:', response);
                    this.router.navigate(['users/login']);
                },
                error: (error) => {
                    if (error.status === 400 && error.error) {
                        const backendErrors = error.error;

                        Object.keys(backendErrors).forEach((field) => {
                            if (this.registerForm.get(field)) {
                                const currentErrors = this.registerForm.get(field)?.errors || {};
                                this.registerForm.get(field)?.setErrors({
                                    ...currentErrors,
                                    backendError: backendErrors[field],
                                });
                            }
                        });
                    } else {
                        console.error('Unexpected error:', error);
                    }
                },
            });
        } else {
            console.log('Form is invalid');
            this.registerForm.markAllAsTouched();
        }
    }
}
