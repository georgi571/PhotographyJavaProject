import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-edit-picture',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './edit-picture.component.html',
    styleUrl: './edit-picture.component.css'
})
export class EditPictureComponent {
    errorMessage: string | null = null;

    selectedImage: string | ArrayBuffer | null = null;

    userDetails = {
        realName: '',
        city: '',
        age: null,
        picture: ''
    };

    passwordDetails = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
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

    onFileChange(event: any): void {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.selectedImage = e.target.result; // stores the image data as a URL
            };
            reader.readAsDataURL(file); // reads the image as a base64-encoded URL
        }
    }

    onSubmitPictureUpdate() {
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

    onSubmitPassword() {
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
