import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {AuthService} from '../../../services/auth-service/auth.service';

@Component({
    selector: 'app-edit-email',
    standalone: true,
    imports: [
        FormsModule,
        ReactiveFormsModule
    ],
    templateUrl: './edit-email.component.html',
    styleUrl: './edit-email.component.css'
})
export class EditEmailComponent implements OnInit {
    errorMessage: string | null = null;

    userDetails = {
        oldEmail: '',
        newEmail: '',
        password: ''
    };

    fieldErrors: { [key: string]: string } = {};

    constructor(private http: HttpClient,
                private authService: AuthService,
                private profile: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.profile.getUserOldEmailDetails().subscribe({
            next: (data: any) => {
                console.log(data)
                this.userDetails = data;
            }
        });
    }

    onSubmitEmailUpdate() {
        this.profile.editUserEmailDetail(this.userDetails).subscribe({
            next: (response) => {
                this.errorMessage = null;
                this.fieldErrors = {};
                this.router.navigate(['/profile/edit']);
            },
            error: (error) => {
                if (error.status === 400 && typeof error.error === 'object') {
                    this.fieldErrors = error.error;
                } else {
                    this.errorMessage = 'An unexpected error occurred!';
                }
            }
        });
    }
}
