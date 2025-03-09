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
    followings: any[] = [];
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
            this.filterFollowing();
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

    unfollow(followingUser: any) {
        if (confirm(`Are you sure you want to remove following user ${followingUser.username}?`)) {
            this.profileService.unfollowUser(followingUser.username).subscribe(
                response => {
                    console.log('Unfollowed user:', response);
                    this.followings = this.followings.filter(f => f.username !== followingUser.username);
                    this.filterFollowing();
                }
            );
        }
    }

    confirmBlock(followingUser: any) {
        if (confirm(`Are you sure you want to block following user ${followingUser.username}?`)) {
            this.profileService.blockUser(followingUser.username).subscribe(
                response => {
                    alert('User was successfully blocked.');
                    this.followings = this.followings.filter(f => f.username !== followingUser.username);
                    this.filterFollowing();
                }
            );
        }
        window.location.reload();
    }
}
