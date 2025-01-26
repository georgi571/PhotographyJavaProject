import {Component, OnInit} from '@angular/core';
import {ProfileService} from '../../../services/profile-service/profile.service';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
    selector: 'app-send-friend-request',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './send-friend-request.component.html',
    styleUrl: './send-friend-request.component.css'
})
export class SendFriendRequestComponent implements OnInit {
    // sentFriendRequests: string[] = [];

    sentFriendRequests = [
        { id: 1, username: 'john_doe', profilePicture: 'path/to/john_image.jpg' },
        { id: 2, username: 'jane_smith', profilePicture: 'path/to/jane_image.jpg' },
        // Add more sent friend requests here
    ];

    filteredFriendRequests: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getSentFriendRequests();
        this.filterSentRequests();
    }

    getSentFriendRequests(): void {
        this.profileService.getSentFriendRequests().subscribe(data => {
            this.sentFriendRequests = data;
        });
    }

    filterSentRequests() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredFriendRequests = this.sentFriendRequests.filter(friend =>
            friend.username.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFriendProfile(friend: any) {
        this.router.navigate(['/profile', friend.username]);
    }

    cancelRequest(friend: any) {
        if (confirm(`Are you sure you want to cansel friend request to user ${friend.username}?`)) {
            this.sentFriendRequests = this.sentFriendRequests.filter(f => f.id !== friend.id);
        }
    }
}
