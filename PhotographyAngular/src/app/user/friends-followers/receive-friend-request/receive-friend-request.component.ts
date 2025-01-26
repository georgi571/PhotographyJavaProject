import {Component, OnInit} from '@angular/core';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
    selector: 'app-receive-friend-request',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './receive-friend-request.component.html',
    styleUrl: './receive-friend-request.component.css'
})
export class ReceiveFriendRequestComponent implements OnInit {
    // receivedFriendRequests: string[] = [];

    receivedFriendRequests = [
        { id: 1, username: 'john_doe', profilePicture: 'path/to/john_image.jpg' },
        { id: 2, username: 'jane_smith', profilePicture: 'path/to/jane_image.jpg' },
        // Add more received friend requests here
    ];

    // Array to hold the filtered friend requests based on search input
    filteredReceivedRequests: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getReceivedFriendRequests();
        this.filterReceivedRequests();
    }

    getReceivedFriendRequests(): void {
        this.profileService.getReceivedFriendRequests().subscribe(data => {
            this.receivedFriendRequests = data;
        });
    }

    filterReceivedRequests() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredReceivedRequests = this.receivedFriendRequests.filter(friend =>
            friend.username.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFriendProfile(followingUser: any) {
        this.router.navigate(['/profile', followingUser.username]);
    }

    acceptRequest(username: string): void {
        this.profileService.acceptFriendRequest(username).subscribe(
            response => {
                console.log('Friend request accepted:', response);
                this.getReceivedFriendRequests();
            }
        );
    }

    rejectRequest(username: string): void {
        this.profileService.rejectFriendRequest(username).subscribe(
            response => {
                console.log('Friend request rejected:', response);
                this.getReceivedFriendRequests();
            }
        );
    }

    confirmBlock(friend: any) {
        if (confirm(`Are you sure you want to block following user ${friend.username}?`)) {
            this.receivedFriendRequests = this.receivedFriendRequests.filter(f => f.id !== friend.id);
        }
    }
}
