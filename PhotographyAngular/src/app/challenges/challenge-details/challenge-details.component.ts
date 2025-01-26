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
    challengeDetails: any = {};
    errorMessage: string | null = null;
    showDetails = false;
    showAddPictureModal = false;
    selectedPicture: any = null;
    selectedComments: any[] = [];
    newComment: string = '';
    showReportModal: boolean = false;
    reportReason: string = '';
    reportType: string = '';
    reportTarget: any = null;
    showDeleteModal = false;
    deleteType = '';
    showEditModal = false;

    itemToDelete: any = null;

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
                    console.log(this.challengeDetails);
                } else {
                    this.errorMessage = 'Challenge not found.';
                }
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
            const formData = new FormData();
            formData.append('file', this.newPicture.file);
            formData.append('caption', this.newPicture.caption);
            formData.append('story', this.newPicture.story);

            this.challengeService.uploadPicture(this.challengeId, formData).subscribe(
                (response) => {
                    if (response) {
                        console.log('Picture uploaded successfully:', response);
                        this.toggleAddPictureModal();
                    } else {
                        this.errorMessage = 'An error occurred while uploading the picture.';
                    }
                }
            );
        } else {
            this.errorMessage = 'Please fill out all fields and select a picture.';
        }
    }

    selectPicture(picture: any): void {
        this.selectedPicture = picture;
        this.selectedComments = (picture.comments || []).map((comment: { id: any; }, index: any) => ({
            ...comment,
            id: comment.id || `generated-id-${index}`,
        }));

        this.selectedComments.sort((a, b) => {
            return new Date(a.dateTime).getTime() - new Date(b.dateTime).getTime();
        });

        console.log('Selected Comments:', this.selectedComments);
    }

    closePictureDetails(): void {
        this.selectedPicture = null;
    }

    likePicture(event: Event, picture: any): void {
        event.stopPropagation();

        picture.liked = !picture.liked;
        picture.likes += picture.liked ? 1 : -1;

        this.challengeService.toggleLikePicture(this.challengeId, picture.id).subscribe(
            (response) => {
                picture.liked = response.liked;
                picture.likes = response.likes;

                console.log('Updated like status from server:', response);
            }
        );
    }

    addComment() {
        if (this.newComment.trim()) {
            const newComment = {
                user: {username: 'Current User'},
                text: this.newComment,
            };

            this.selectedComments.push(newComment);

            this.challengeService.addCommentToPicture(
                this.challengeId,
                this.selectedPicture.id,
                this.newComment
            ).subscribe(
                (response) => {
                    if (response) {
                        const typedResponse = response as {
                            text: string;
                            author: { username: string };
                            dateTime: string;
                        };

                        const commentIndex = this.selectedComments.findIndex(
                            (comment) => comment.text === this.newComment
                        );
                        if (commentIndex > -1) {
                            this.selectedComments[commentIndex] = typedResponse;
                        }

                        console.log('Comment added by:', typedResponse.author.username);
                        console.log('Comment text:', typedResponse.text);

                        this.newComment = '';
                    } else {
                        console.error('Invalid response structure:', response);
                        this.errorMessage = 'Error: Invalid comment response';
                    }
                }
            );
        }
    }

    openReportModalForPicture(picture: any): void {
        this.showReportModal = true;
        this.reportType = 'Picture';
        this.reportTarget = picture;
        this.reportReason = '';
    }

    openReportModalForComment(comment: any): void {
        this.showReportModal = true;
        this.reportType = 'Comment';
        this.reportTarget = comment;
        this.reportReason = '';
    }

    closeReportModal(): void {
        this.showReportModal = false;
        this.reportReason = '';
        this.reportTarget = null;
    }

    submitReport(): void {
        if (!this.reportReason.trim()) {
            console.error('Report reason is required.');
            return;
        }

        if (this.reportType === 'Picture') {
            this.reportPicture(this.reportTarget, this.reportReason);
        } else if (this.reportType === 'Comment') {
            this.reportComment(this.reportTarget, this.reportReason);
        }

        this.closeReportModal();
    }

    reportPicture(picture: any, reason: string): void {
        this.challengeService.reportPicture(this.challengeId, picture.id, reason).subscribe(
            (response) => {
                console.log('Picture reported successfully:', response);
            }
        );
    }

    reportComment(comment: any, reason: string): void {
        if (!this.selectedPicture) {
            console.error('No picture selected');
            return;
        }

        const picture = this.selectedPicture;

        if (!picture.comments || !picture.comments.some((c: any) => c.id === comment.id)) {
            console.log(picture)
            console.error('Comment not found in the selected picture');
            return;
        }

        this.challengeService.reportComment(this.challengeId, picture.id, comment.id, reason).subscribe(
            (response) => {
                console.log('Comment reported successfully:', response);
            }
        );
    }

    openDeleteConfirmation(type: string, item: any): void {
        this.deleteType = type === 'picture' ? 'Picture' : 'Comment';
        this.itemToDelete = item;
        this.showDeleteModal = true;
    }

    closeDeleteModal(): void {
        this.showDeleteModal = false;
        this.itemToDelete = null;
    }

    confirmDelete(): void {
        if (this.deleteType === 'Picture') {
            this.deletePicture(this.itemToDelete);
        } else if (this.deleteType === 'Comment') {
            this.deleteComment(this.itemToDelete);
        }
        this.closeDeleteModal();
    }

    deletePicture(picture: any): void {
        console.log('Deleting picture:', picture);

        this.challengeService.deletePicture(this.challengeId, picture.id).subscribe(
            (response) => {
                console.log('Picture deleted successfully:', response);

                this.challengeDetails.pictures = this.challengeDetails.pictures.filter(
                    (p: any) => p.id !== picture.id
                );

                if (this.selectedPicture?.id === picture.id) {
                    this.selectedPicture = null;
                }

                console.log('Updated challengeDetails.pictures:', this.challengeDetails.pictures);
            }
        );
    }

    deleteComment(comment: any): void {
        console.log('Deleting comment:', comment);
        this.challengeService.deleteComment(this.challengeId, this.selectedPicture.id, comment.id).subscribe(
            (response) => {
                console.log('Comment deleted successfully:', response);
                this.selectedComments = this.selectedComments.filter(
                    (c: any) => c.id !== comment.id
                );
                console.log('Updated selectedComments:', this.selectedComments);
            }
        );
    }

    openEditModal(): void {
        // You can either open a modal or navigate to an edit form
        console.log('Edit button clicked!');
        this.showEditModal = true; // Example for modal
    }

    closeEditModal(): void {
        this.showEditModal = false;
    }

    submitEdit(): void {
        // if (!this.challengeDetails.title || !this.challengeDetails.activity || !this.challengeDetails.createdAt || !this.challengeDetails.endAt) {
        //     this.errorMessage = 'Please fill in all required fields.';
        //     return;
        // }
        //
        // // Convert date fields back to ISO format if required by backend
        // const updatedDetails = {
        //     ...this.challengeDetails,
        //     createdAt: new Date(this.challengeDetails.createdAt).toISOString(),
        //     endAt: new Date(this.challengeDetails.endAt).toISOString(),
        // };
        //
        // // Send the updated details to the backend
        // this.challengeService.updateChallenge(this.challengeId, updatedDetails).subscribe({
        //     next: (response) => {
        //         console.log('Challenge details updated successfully:', response);
        //         this.showEditModal = false; // Close the modal on success
        //     },
        //     error: (error) => {
        //         console.error('Error updating challenge details:', error);
        //     },
        // });
    }
}
