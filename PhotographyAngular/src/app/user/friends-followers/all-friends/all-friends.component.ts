import {Component, OnInit} from '@angular/core';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
    selector: 'app-all-friends',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './all-friends.component.html',
    styleUrl: './all-friends.component.css'
})
export class AllFriendsComponent implements OnInit {

    friends: any[] = [];
    filteredFriends: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getAllFriends();
        this.filterFriends();
    }

    getAllFriends(): void {
        this.profileService.getFriends().subscribe(data => {
            this.friends = data;
            this.filterFriends();
        });
    }

    filterFriends() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredFriends = this.friends.filter(friend =>
            friend.username.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFriendProfile(friend: any) {
        this.router.navigate(['/profile/username', friend.username]);
    }

    unfriend(friend: any) {
        if (confirm(`Are you sure you want to unfriend ${friend.username}?`)) {
            this.profileService.removeFriend(friend.username).subscribe(
                response => {
                    this.friends = this.friends.filter(f => f.username !== friend.username);
                    this.filterFriends(); // Update filtered list
                }
            );
        }
    }

    confirmBlock(friend: any) {
        if (confirm(`Are you sure you want to block ${friend.username}?`)) {
            this.profileService.blockUser(friend.username).subscribe(
                response => {
                    alert('User was successfully blocked.');
                    this.friends = this.friends.filter(f => f.username !== friend.username);
                    this.filterFriends();
                }
            );
        }
        window.location.reload();
    }
}
