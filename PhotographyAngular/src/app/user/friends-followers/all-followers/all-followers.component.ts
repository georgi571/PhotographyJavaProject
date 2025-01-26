import {Component, OnInit} from '@angular/core';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-all-followers',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './all-followers.component.html',
    styleUrl: './all-followers.component.css'
})
export class AllFollowersComponent implements OnInit {
    // followers: string[] = [];

    followers = [
        { id: 1, username: 'john_doe', email: 'john.doe@example.com', profilePicture: 'path/to/john_image.jpg', blocked: false },
        { id: 2, username: 'jane_smith', email: 'jane.smith@example.com', profilePicture: 'path/to/jane_image.jpg', blocked: false },
        // More followers...
    ];

    filteredFollowers: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getAllFollowers();
        this.filterFollowers();
    }

    getAllFollowers(): void {
        this.profileService.getFollowers().subscribe(data => {
            this.followers = data;
        });
    }

    filterFollowers() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredFollowers = this.followers.filter(follower =>
            follower.username.toLowerCase().includes(lowerCaseSearchTerm) ||
            follower.email.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFollowerProfile(follower: any) {
        // Navigate to the follower's profile
        this.router.navigate(['/profile', follower.username]); // Navigate to /profile/:username
    }

    removeFollower(follower: any) {
        if (confirm(`Are you sure you want to remove follower ${follower.username}?`)) {
            this.followers = this.followers.filter(f => f.id !== follower.id);
        }
    }

    confirmBlock(follower: any) {
        if (confirm(`Are you sure you want to block follower ${follower.username}?`)) {
            this.followers = this.followers.filter(f => f.id !== follower.id);
        }
    }
}
