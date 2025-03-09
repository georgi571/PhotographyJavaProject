import {Component, Input, OnInit} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {ProfileService} from '../../services/profile-service/profile.service';
import {ActivatedRoute} from '@angular/router';
import {AuthService} from '../../services/auth-service/auth.service';
import {JwtService} from '../../services/jwt-service/jwt.service';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [
        HeaderComponent,
        FooterComponent
    ],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

    @Input() profileUsername!: string;

    showPopup: boolean = false;

    userDetails: any = {};
    friends: any[] = [];
    followers: any[] = [];
    trophies: any[] = [];
    events: any[] = [];
    pictures: any[] = [];
    errorMessage: string | null = null;
    username: string | null = null;
    role: string | null = null;

    isFriend: boolean = false;
    hasPendingRequest: boolean = false;
    isFollowing: boolean = false;

    currFriends: any[] = [];
    currFollowers: any[] = [];

    constructor(private profileService: ProfileService,
                private route: ActivatedRoute,
                private authService: AuthService,
                private jwtService: JwtService) {
    }

    ngOnInit() {
        this.loadUserProfile(); // Load profile on initialization
        this.listenToRouteChanges(); // Listen for route changes
        this.getAllFriends();
        this.getAllFollowers();
        this.getAllFriendsForUser();
        this.getAllFollowersForUser();

        // const username = this.route.snapshot.paramMap.get('username');
        // if (username) {
        //     this.getUserInfo(username);
        // } else {
        //     this.errorMessage = 'No username provided in the URL.';
        // }
        //
        // const token = this.authService.getToken();
        // if (token) {
        //     const decodedToken = this.jwtService.decodeToken(token);
        //     this.username = decodedToken?.username || null;
        //     this.role = decodedToken?.role ? decodedToken?.role.replace("ROLE_", "") : null;
        // }
    }

    getAllFriends(): void {
        this.profileService.getFriends().subscribe(data => {
            this.friends = data;
        });
    }

    getAllFollowers(): void {
        this.profileService.getFollowers().subscribe(data => {
            this.followers = data;
        });
    }

    getAllFriendsForUser(): void {
        const username = this.route.snapshot.paramMap.get('username') ?? '';
        this.profileService.getFriendsForProfile(username).subscribe(data => {
            this.currFriends = data;
        });
    }

    getAllFollowersForUser(): void {
        const username = this.route.snapshot.paramMap.get('username') ?? '';
        this.profileService.getFollowersForProfile(username).subscribe(data => {
            this.currFollowers = data;
        });
    }

    loadUserProfile() {
        const username = this.route.snapshot.paramMap.get('username');
        if (username) {
            this.getUserInfo(username);
        } else {
            this.errorMessage = 'No username provided in the URL.';
        }

        const token = this.authService.getToken();
        if (token) {
            const decodedToken = this.jwtService.decodeToken(token);
            this.username = decodedToken?.username || null;
            this.role = decodedToken?.role ? decodedToken?.role.replace("ROLE_", "") : null;
        }
    }

    listenToRouteChanges() {
        this.route.params.subscribe(params => {
            const newUsername = params['username'];
            if (newUsername && newUsername !== this.userDetails.username) {
                this.getUserInfo(newUsername); // Load new user info on route change
            }
        });
    }

    getUserInfo(username: string) {
        this.profileService.getUserDetails(username).subscribe({
            next: (data: any) => {
                this.userDetails.username = data.username;
                this.userDetails.name = data.realName;
                this.userDetails.country = data.country;
                this.userDetails.city = data.city;
                this.userDetails.gender = data.gender;
                this.userDetails.age = data.age;
                this.userDetails.rank = data.rank;
                this.userDetails.points = data.points;
                this.userDetails.picture = data.profilePicturePath;
                this.trophies = data.trophies;
                this.events = data.events;
                this.pictures = data.pictures;

                this.checkFriendshipStatus();
            },
            error: (error) => {
                this.errorMessage = 'Failed to load user information. Please try again later.';
                console.error(error);
            }
        });
    }

    openPopup() {
        this.showPopup = true;
    }

    closePopup() {
        this.showPopup = false;
    }

    checkFriendshipStatus(): void {
        this.profileService.checkIfFriends(this.userDetails.username).subscribe((result) => {
            this.isFriend = result;
        });

        this.profileService.checkIfFriendRequestSent(this.userDetails.username).subscribe((result) => {
            this.hasPendingRequest = result;
        });

        this.profileService.checkIfFollowing(this.userDetails.username).subscribe((result) => {
            this.isFollowing = result;
        });
    }

    addFriend() {
        if (this.userDetails.username) {
            this.profileService.addFriend(this.userDetails.username).subscribe({
                next: (response) => {
                    console.log('Friend added successfully:', response);
                    alert('Friend added successfully!');
                    this.checkFriendshipStatus();
                },
                error: (error) => {
                    console.error('Error adding friend:', error);
                    alert('Failed to add friend.');
                }
            });
        } else {
            console.error('No profile username found');
        }
    }

    followUser() {
        if (this.userDetails.username) {
            this.profileService.followUser(this.userDetails.username).subscribe({
                next: (response) => {
                    console.log('Followed user successfully:', response);
                    alert('Followed user successfully!');
                    this.checkFriendshipStatus();  // Refresh follow status
                },
                error: (error) => {
                    console.error('Error following user:', error);
                    alert('Failed to follow user.');
                }
            });
        } else {
            console.error('No profile username found');
        }
    }

    removeFriend() {
        this.profileService.removeFriend(this.userDetails.username).subscribe({
            next: (response) => {
                console.log('Friend removed successfully:', response);
                alert('Friend removed successfully!');
                this.checkFriendshipStatus();  // Refresh friend status
            },
            error: (error) => {
                console.error('Error removing friend:', error);
                alert('Failed to remove friend.');
            }
        });
    }

    cancelFriendRequest() {
        this.profileService.cancelFriendRequest(this.userDetails.username).subscribe({
            next: (response) => {
                console.log('Friend request cancelled successfully:', response);
                alert('Friend request cancelled successfully!');
                this.checkFriendshipStatus();  // Refresh friend status
            },
            error: (error) => {
                console.error('Error cancelling friend request:', error);
                alert('Failed to cancel friend request.');
            }
        });
    }

    unfollowUser() {
        this.profileService.unfollowUser(this.userDetails.username).subscribe({
            next: (response) => {
                console.log('Unfollowed user successfully:', response);
                alert('Unfollowed user successfully!');
                this.checkFriendshipStatus();  // Refresh follow status
            },
            error: (error) => {
                console.error('Error unfollowing user:', error);
                alert('Failed to unfollow user.');
            }
        });
    }

    confirmBlock(userDetails: any) {
        if (confirm(`Are you sure you want to block following user ${userDetails.username}?`)) {
            this.profileService.blockUser(userDetails.username).subscribe(
                response => {
                    alert('User was successfully blocked.');
                }
            );
        }
    }
}
