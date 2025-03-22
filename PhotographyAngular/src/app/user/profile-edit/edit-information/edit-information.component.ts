import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-edit-information',
    standalone: true,
    imports: [
        FormsModule,
        ReactiveFormsModule
    ],
    templateUrl: './edit-information.component.html',
    styleUrl: './edit-information.component.css'
})
export class EditInformationComponent implements OnInit {
    errorMessage: string | null = null;

    userDetails = {
        realName: '',
        city: '',
        birthDate: '',
        picture: ''
    };

    fieldErrors: { [key: string]: string } = {};

    constructor(private http: HttpClient,
                private profile: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.profile.getUserEditDetails().subscribe({
            next: (data: any) => {
                this.userDetails = data;
            }
        });
    }

    onSubmitInformation() {
        this.profile.editUserDetail(this.userDetails).subscribe({
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
