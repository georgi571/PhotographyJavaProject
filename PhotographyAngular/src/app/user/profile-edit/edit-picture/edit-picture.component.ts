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

    selectedFile: File | null = null;

    fieldErrors: { [key: string]: string } = {};

    constructor(private http: HttpClient,
                private profile: ProfileService,
                private router: Router) {
    }

    onFileChange(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.selectedImage = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    }

    onSubmitPictureUpdate() {
        if (!this.selectedFile) {
            this.errorMessage = 'Please select a file to upload.';
            return;
        }

        const formData = new FormData();
        formData.append('file', this.selectedFile);

        this.profile.editUserPicture(formData).subscribe({
            next: () => {
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
