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

    // friends: string[] = [];

    friends = [
        { id: 1, username: 'JohnDoe', email: 'john@example.com', profilePicture: 'assets/images/john.png' },
        { id: 2, username: 'JaneSmith', email: 'jane@example.com', profilePicture: 'assets/images/jane.png' },
        { id: 3, username: 'SamWilson', email: 'sam@example.com', profilePicture: null },
    ];

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
        });
    }

    filterFriends() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredFriends = this.friends.filter(friend =>
            friend.username.toLowerCase().includes(lowerCaseSearchTerm) ||
            friend.email.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFriendProfile(friend: any) {
        this.router.navigate(['/profile', 'georgi571']);
    }

    unfriend(friend: any) {
        if (confirm(`Are you sure you want to unfriend ${friend.username}?`)) {
            this.friends = this.friends.filter(f => f.id !== friend.id);
        }
    }

    confirmBlock(friend: any) {
        if (confirm(`Are you sure you want to block ${friend.username}?`)) {
            this.friends = this.friends.filter(f => f.id !== friend.id);
        }
    }
}
