import {Component, OnInit} from '@angular/core';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
    selector: 'app-all-following',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './all-following.component.html',
    styleUrl: './all-following.component.css'
})
export class AllFollowingComponent implements OnInit {
    // following: string[] = [];

    followings = [
        { id: 1, username: 'john_doe', email: 'john.doe@example.com', profilePicture: 'path/to/john_image.jpg', blocked: false },
        { id: 2, username: 'jane_smith', email: 'jane.smith@example.com', profilePicture: 'path/to/jane_image.jpg', blocked: false },
        // More following users...
    ];

    filteredFollowing: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getAllFollowing();
        this.filterFollowing();
    }

    getAllFollowing(): void {
        this.profileService.getFollowing().subscribe(data => {
            this.followings = data;
        });
    }

    filterFollowing() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredFollowing = this.followings.filter(user =>
            user.username.toLowerCase().includes(lowerCaseSearchTerm) ||
            user.email.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFollowingProfile(followingUser: any) {
        this.router.navigate(['/profile', followingUser.username]);
    }

    unfollow(follower: any) {
        if (confirm(`Are you sure you want to remove following user ${follower.username}?`)) {
            this.followings = this.followings.filter(f => f.id !== follower.id);
        }
    }

    confirmBlock(follower: any) {
        if (confirm(`Are you sure you want to block following user ${follower.username}?`)) {
            this.followings = this.followings.filter(f => f.id !== follower.id);
        }
    }
}
