import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../../services/auth-service/auth.service';

@Component({
    selector: 'app-edit-username',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './edit-username.component.html',
    styleUrl: './edit-username.component.css'
})
export class EditUsernameComponent implements OnInit {
    errorMessage: string | null = null;

    userDetails = {
        oldUsername: '',
        newUsername: '',
        password: ''
    };

    fieldErrors: { [key: string]: string } = {};

    constructor(private http: HttpClient,
                private authService: AuthService,
                private profile: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.profile.getUserOldUsernameDetails().subscribe({
            next: (data: any) => {
                this.userDetails = data;
            }
        });
    }

    onSubmitUsernameUpdate() {
        this.profile.editUsernameDetail(this.userDetails).subscribe({
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
                    this.errorMessage = 'An unexpected error occurred!';
                    alert("Wrong password please try again!");
                    window.location.reload();
                }
            }
        });
    }
}
