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
    followers: any[] = [];
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
            console.log(data);
            this.filterFollowers();
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
            this.profileService.removeFollower(follower.username).subscribe(
                response => {
                    console.log('Follower removed:', response);
                    this.followers = this.followers.filter(f => f.id !== follower.id);
                    this.filterFollowers();
                }
            );
        }
    }

    confirmBlock(follower: any) {
        if (confirm(`Are you sure you want to block follower ${follower.username}?`)) {
            this.profileService.blockUser(follower.username).subscribe(
                response => {
                    alert('User was successfully blocked.');
                    this.followers = this.followers.filter(f => f.username !== follower.username);
                    this.filterFollowers();
                }
            );
        }
        window.location.reload();
    }
}
