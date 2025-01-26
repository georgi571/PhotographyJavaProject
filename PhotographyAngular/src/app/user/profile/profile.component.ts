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

    constructor(private profileService: ProfileService,
                private route: ActivatedRoute,
                private authService: AuthService,
                private jwtService: JwtService) {
    }

    ngOnInit() {
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
                this.friends = data.friends;
                this.followers = data.followers;
                this.trophies = data.trophies;
                this.events = data.events;
                this.pictures = data.pictures;
                console.log('Response:', data);
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

    addFriend() {
        if (this.profileUsername) {
            this.profileService.addFriend(this.profileUsername).subscribe(
                (response) => {
                    alert('Friend added successfully!');
                }
            );
        }
    }

    followUser() {
        if (this.profileUsername) {
            this.profileService.followUser(this.profileUsername).subscribe(
                (response) => {
                    alert('Followed user successfully!');
                }
            );
        }
    }
}
