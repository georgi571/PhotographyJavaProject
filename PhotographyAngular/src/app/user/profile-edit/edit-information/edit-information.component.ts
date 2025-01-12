import {Component, OnInit} from '@angular/core';
import {FooterComponent} from '../../../core/footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HeaderComponent} from '../../../core/header/header.component';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {NgClass} from '@angular/common';

@Component({
    selector: 'app-edit-information',
    standalone: true,
    imports: [
        FooterComponent,
        FormsModule,
        HeaderComponent,
        ReactiveFormsModule,
        NgClass
    ],
    templateUrl: './edit-information.component.html',
    styleUrl: './edit-information.component.css'
})
export class EditInformationComponent implements OnInit {
    errorMessage: string | null = null;

    userDetails = {
        realName: '',
        city: '',
        age: null,
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
                this.router.navigate(['/profile']);
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
