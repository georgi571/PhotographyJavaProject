import {Component, OnInit} from '@angular/core';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ActivatedRoute} from '@angular/router';
import {DatePipe, NgClass} from '@angular/common';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-challenge-details',
    standalone: true,
    imports: [
        DatePipe,
        HeaderComponent,
        FooterComponent,
        FormsModule,
        NgClass
    ],
    templateUrl: './challenge-details.component.html',
    styleUrl: './challenge-details.component.css'
})
export class ChallengeDetailsComponent implements OnInit {
    challengeId!: string;
    challengeDetails: any;
    errorMessage: string | null = null;
    showDetails = false;
    showAddPictureModal = false;
    selectedPicture: any = null;
    selectedComments: any[] = [];
    newComment: string = '';

    newPicture = {
        file: null as File | null,
        caption: '',
        story: '',
    };

    constructor(
        private route: ActivatedRoute,
        private challengeService: ChallengeService
    ) {
    }

    ngOnInit(): void {
        this.challengeId = this.route.snapshot.paramMap.get('id')!;
        this.challengeService.getChallengeDetails(this.challengeId).subscribe(
            (details) => {
                if (details) {
                    this.challengeDetails = details;
                } else {
                    this.errorMessage = 'Challenge not found.';
                }
            },
            (error) => {
                this.errorMessage = 'An error occurred while fetching the challenge.';
            }
        );
    }

    toggleDetails(): void {
        this.showDetails = !this.showDetails;
    }

    toggleAddPictureModal(): void {
        this.showAddPictureModal = !this.showAddPictureModal;
    }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.newPicture.file = file;
        }
    }

    submitPicture(): void {
        if (this.newPicture.file && this.newPicture.caption && this.newPicture.story) {
            const newPictureEntry = {
                id: this.challengeDetails.pictures.length + 1,
                imageUrl: URL.createObjectURL(this.newPicture.file),
                likes: 0,
                user: {
                    profilePictureUrl: 'path-to-profile.jpg',
                    username: 'You',
                },
                caption: this.newPicture.caption,
                story: this.newPicture.story,
                comments: [],
            };

            this.challengeDetails.pictures.push(newPictureEntry);

            this.newPicture = {file: null, caption: '', story: ''};
            this.toggleAddPictureModal();
        } else {
            alert('Please select a file, caption, and story!');
        }
    }

    selectPicture(picture: any): void {
        this.selectedPicture = picture;
        this.selectedComments = picture.comments || [];
    }

    closePictureDetails(): void {
        this.selectedPicture = null;
    }

    likePicture(event: Event, picture: any): void {
        event.stopPropagation();
        picture.liked = !picture.liked;
        picture.likes += picture.liked ? 1 : -1;
    }

    addComment() {
        if (this.newComment.trim()) {
            this.selectedComments.push({
                user: {username: 'Current User'},
                text: this.newComment,
            });
            this.newComment = '';
        }
    }
}
