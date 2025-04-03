import {Component, Input, OnInit} from '@angular/core';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ProfileService} from '../../services/profile-service/profile.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgClass} from '@angular/common';
import {forkJoin, map} from 'rxjs';
import {AdminService} from '../../services/admin-service/admin.service';
import {AuthService} from '../../services/auth-service/auth.service';
import {JwtService} from '../../services/jwt-service/jwt.service';

@Component({
  selector: 'app-all-pictures',
    imports: [
        HeaderComponent,
        FooterComponent,
        ReactiveFormsModule,
        NgClass,
        FormsModule
    ],
  templateUrl: './all-pictures.component.html',
  styleUrl: './all-pictures.component.css'
})
export class AllPicturesComponent implements OnInit {
    @Input() profileUsername!: string;

    userDetails: any = {};
    userId: string | null = null;

    pictures: any[] = [];
    selectedComments: any[] = [];
    newComment: string = '';
    selectedPicture: any = null;
    showPicturePopup: boolean = false;
    isBlockedMe: boolean = false;

    errorMessage: string | null = null;
    showDeleteModal = false;
    deleteType = '';
    itemToDelete: any = null;

    deletePicturePermission: boolean = false;
    deleteCommentPermission: boolean = false;

    loading: boolean = true;

    currentUser = {
        username: '',
        profilePicturePath: '',
    }


    constructor(private challengeService: ChallengeService,
                private route: ActivatedRoute,
                private profileService: ProfileService,
                private adminService: AdminService,
                private authService: AuthService,
                private jwtService: JwtService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.loadUserProfile();

        const token = this.authService.getToken();
        if (token) {
            const decodedToken = this.jwtService.decodeToken(token);
            this.userId = decodedToken?.userId;
        }

        this.profileService.getUserById(this.userId).subscribe(
            (userData) => {
                this.currentUser = userData;
            }
        );

        this.fetchPermissions();
    }

    loadUserProfile() {
        const username = this.route.snapshot.paramMap.get('username');
        if (username) {
            this.getUserInfo(username);
        } else {
            this.errorMessage = 'No username provided in the URL.';
        }
    }

    fetchPermissions() {
        this.adminService.getPermissions().subscribe({
            next: (data) => {
                if (data.includes('deleteMessage')) this.deleteCommentPermission = true;
                if (data.includes('deletePicture')) this.deletePicturePermission = true;
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
            }
        });
    }

    getUserInfo(username: string) {
        this.profileService.getUserDetails(username).subscribe({
            next: (data: any) => {
                this.userDetails.id = data.id;

                if (this.userDetails.id) {
                    this.getAllUsersPicture(this.userDetails.id);
                }

                this.profileService.isUserBlocked(username).subscribe((result) => {
                    this.isBlockedMe = result;
                    if (result) {
                        this.router.navigate(['page-not-found']);
                    }
                });
            },
            error: (error) => {
                this.errorMessage = 'Failed to load user information. Please try again later.';
                console.error(error);
            }
        });
    }

    getAllUsersPicture(userId: string): void {
        this.challengeService.getAllPicturesForUser(userId).subscribe(data => {
            if (Array.isArray(data)) {
                this.pictures = data
                    .sort((a, b) => b.likes - a.likes)
                    .slice(0, 3);
            }
        });
    }

    openPicturePopup(picture: any): void {
        this.selectedPicture = picture;
        this.showPicturePopup = true;
    }

    closePicturePopup(): void {
        this.showPicturePopup = false;
        this.selectedPicture = null;
    }

    likePicture(event: Event, picture: any): void {
        event.stopPropagation();

        picture.liked = !picture.liked;
        picture.likes += picture.liked ? 1 : -1;

        this.challengeService.toggleLikePicture(picture.id).subscribe(
            (response) => {
                picture.liked = response.liked;
                picture.likes = response.likes;

                console.log('Updated like status from server:', response);
            }
        );
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

    addComment() {
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
                console.log('Picture deleted successfully:', response);

                this.pictures = this.pictures.filter(
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

    closePictureDetails(): void {
        this.selectedPicture = null;
        window.location.reload();
    }
}
