import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
    selector: 'app-profile',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

    userDetails: any = {};
    friends: any[] = [];
    followers: any[] = [];
    trophies: any[] = [];
    events: any[] = [];
    pictures: any[] = [];
    errorMessage: string | null = null;


    constructor(private profileService: ProfileService) {
    }

    ngOnInit() {
        this.getUserInfo();
    }

    getUserInfo() {
        this.profileService.getUserDetails().subscribe({
            next: (data: any) => {
                this.userDetails.username = data.username;
                this.userDetails.name = data.realName;
                this.userDetails.country = data.country;
                this.userDetails.city = data.city;
                this.userDetails.gender = data.gender;
                this.userDetails.age = data.age;
                this.userDetails.rank = data.rank;
                this.userDetails.points = data.points;
                this.userDetails.picture = 'http://localhost:8080' + data.profilePicturePath;
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

}
