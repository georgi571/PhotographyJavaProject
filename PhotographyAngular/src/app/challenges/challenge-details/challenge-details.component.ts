import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DatePipe, NgClass} from '@angular/common';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {FormsModule} from '@angular/forms';
import {JwtService} from '../../services/jwt-service/jwt.service';
import {AuthService} from '../../services/auth-service/auth.service';
import {ProfileService} from '../../services/profile-service/profile.service';
import {forkJoin, map} from 'rxjs';
import {AdminService} from '../../services/admin-service/admin.service';

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
    manageChallenge: boolean = false;
    deletePicturePermission: boolean = false;
    deleteCommentPermission: boolean = false;

    itemToDelete: any = null;

    newPicture = {
        file: null as File | null,
        caption: '',
        story: '',
    };

    userId: string | null = null;
    loading: boolean = true;

    currentUser = {
        username: '',
        profilePicturePath: '',
    }

    tomorrow: string;

    constructor(
        private route: ActivatedRoute,
        private challengeService: ChallengeService,
        private profileService: ProfileService,
        private authService: AuthService,
        private jwtService: JwtService,
        private router: Router,
        private adminService: AdminService
    ) {
        const tomorrowDate = new Date();
        tomorrowDate.setDate(tomorrowDate.getDate() + 1);
        this.tomorrow = tomorrowDate.toISOString().split('T')[0];
    }

    ngOnInit(): void {
        this.challengeId = this.route.snapshot.paramMap.get('id')!;

        const token = this.authService.getToken();
        if (token) {
            const decodedToken = this.jwtService.decodeToken(token);
            this.userId = decodedToken?.userId;
        }

        if (this.userId === null) {
            this.router.navigate(['auth-required']);
            return;
        }

        this.challengeService.getChallengeDetails(this.challengeId).subscribe(
            (details) => {
                this.challengeDetails = details;
            }
        );

        this.profileService.getUserById(this.userId).subscribe(
            (userData) => {
                this.currentUser = userData;
            }
        );

        this.fetchPermissions();

    }

    fetchPermissions() {
        this.adminService.getPermissions().subscribe({
            next: (data) => {
                if (data.includes('manageChallenge')) this.manageChallenge = true;
                if (data.includes('deleteMessage')) this.deleteCommentPermission = true;
                if (data.includes('deletePicture')) this.deletePicturePermission = true;
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
            }
        });
    }

    toggleDetails(): void {
        this.showDetails = !this.showDetails;
    }

    toggleAddPictureModal(): void {
        this.showAddPictureModal = !this.showAddPictureModal;
    }

    onFileSelected(event: any): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.newPicture.file = input.files[0];
        }

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
                        this.toggleAddPictureModal();
                        window.location.reload();
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

        const userRequests = this.selectedComments.map((comment: any) =>
            this.profileService.getUserById(comment.authorId).pipe(
                map((userData: any) => {
                    comment.author = userData;
                    return comment;
                })
            )
        );

        forkJoin(userRequests).subscribe(
            (updatedComments) => {
                this.selectedComments = updatedComments;
                this.loading = false;
            }
        );

        this.profileService.getUserById(picture.authorId).subscribe(
            (userData) => {
                this.selectedPicture.user = userData;
            }
        );
    }


    closePictureDetails(): void {
        this.selectedPicture = null;
        window.location.reload();
    }

    likePicture(event: Event, picture: any): void {
        event.stopPropagation();

        picture.liked = !picture.liked;
        picture.likes += picture.liked ? 1 : -1;

        this.challengeService.toggleLikePicture(picture.id).subscribe(
            (response) => {
                picture.liked = response.liked;
                picture.likes = response.likes;
            }
        );
    }

    addComment() {
        console.log(this.currentUser.username)
        if (this.newComment.trim()) {
            const newComment = {
                author: {
                    username: this.currentUser.username,
                    profilePicturePath: this.currentUser.profilePicturePath || 'default-profile-pic-url'
                },
                text: this.newComment,
                dateTime: new Date().toISOString(),
            };

            this.selectedComments.push(newComment);

            this.challengeService.addCommentToPicture(
                this.selectedPicture.id,
                this.newComment
            ).subscribe(
                (response) => {
                    if (response) {
                        const typedResponse = response as {
                            text: string;
                            author: { username: string, profilePicturePath: string };
                            dateTime: string;
                        };

                        const commentIndex = this.selectedComments.findIndex(
                            (comment) => comment.text === this.newComment && comment.dateTime === typedResponse.dateTime
                        );

                        if (commentIndex > -1) {
                            this.selectedComments[commentIndex] = {
                                ...typedResponse,
                                author: typedResponse.author
                            };
                        }

                        this.newComment = '';

                        if (this.selectedComments.length === 1) {
                            window.location.reload();
                        }
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
        this.challengeService.reportPicture(this.challengeId, picture.id, reason, picture.authorId)
            .subscribe({
                next: () => {
                    alert("Picture reported successfully!");
                },
                error: (error) => {
                    alert("Failed to report the picture. Please try again.");
                }
            });
    }

    reportComment(comment: any, reason: string): void {
        if (!this.selectedPicture) {
            console.error('No picture selected');
            return;
        }

        const picture = this.selectedPicture;

        if (!picture.comments || !picture.comments.some((c: any) => c.id === comment.id)) {
            console.error('Comment not found in the selected picture');
            return;
        }

        this.challengeService.reportComment(this.challengeId, picture.id, comment.id, reason, comment.authorId)
            .subscribe({
                next: () => {
                    alert("Comment reported successfully!");
                },
                error: (error) => {
                    alert("Failed to report the comment. Please try again.");
                }
            });
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
        this.challengeService.deletePicture(picture.id).subscribe(
            (response) => {
                this.challengeDetails.pictures = this.challengeDetails.pictures.filter(
                    (p: any) => p.id !== picture.id
                );

                if (this.selectedPicture?.id === picture.id) {
                    this.selectedPicture = null;
                }
                alert('Picture deleted successfully!');
            },
            (error) => {
                if (error.status === 403) {
                    alert('You do not have permission to delete this picture.');
                } else {
                    alert('Something went wrong. Please try again later.');
                }
            }
        );
    }

    deleteComment(comment: any): void {
        this.challengeService.deleteComment(comment.id).subscribe(
            (response) => {
                this.selectedComments = this.selectedComments.filter(
                    (c: any) => c.id !== comment.id
                );
                alert('Comment deleted successfully!');
            },
            (error) => {
                if (error.status === 403) {
                    alert('You do not have permission to delete this comment.');
                } else {
                    alert('Something went wrong. Please try again later.');
                    window.location.reload();
                }
            }
        );
    }

    openEditModal(): void {
        console.log('Edit button clicked!');
        this.showEditModal = true;
    }

    closeEditModal(): void {
        this.showEditModal = false;
    }

    submitEdit(): void {
        if (!this.challengeDetails.title || !this.challengeDetails.activity || !this.challengeDetails.startAt || !this.challengeDetails.endAt) {
            this.errorMessage = 'Please fill in all required fields.';
            return;
        }

        const updatedDetails = {
            ...this.challengeDetails,
            createdAt: new Date(this.challengeDetails.startAt).toISOString(),
            endAt: new Date(this.challengeDetails.endAt).toISOString(),
        };

        this.challengeService.updateChallenge(this.challengeId, updatedDetails).subscribe({
            next: (response) => {
                this.showEditModal = false;
            },
            error: (error) => {
                console.error('Error updating challenge details:', error);
                alert('Failed to edit challenge.');
            },
        });
    }

    deleteChallenge(): void {
        if (confirm('Are you sure you want to delete this challenge?')) {
            this.challengeService.deleteChallenge(this.challengeId).subscribe({
                next: () => {
                    alert('Challenge deleted successfully!');
                    this.router.navigate(['/challenges']).then(() => {
                        window.location.reload();
                    });
                },
                error: (err) => {
                    console.error('Error deleting challenge:', err);
                    alert('Failed to delete challenge.');
                }
            });
        }
    }
}
