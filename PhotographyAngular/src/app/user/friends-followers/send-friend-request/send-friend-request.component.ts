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
    sentFriendRequests: any[] = [];
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
        this.profileService.getSentFriendRequests().subscribe({
            next: (data) => {
                this.sentFriendRequests = data;
                this.filterSentRequests();
            },
            error: (error) => console.error('Error fetching sent requests:', error)
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
        if (confirm(`Are you sure you want to cancel the friend request to ${friend.username}?`)) {
            this.profileService.cancelFriendRequest(friend.username).subscribe({
                next: () => {
                    this.sentFriendRequests = this.sentFriendRequests.filter(f => f.username !== friend.username);
                    this.filterSentRequests();
                    alert('Friend request canceled successfully.');
                },
                error: (error) => console.error('Error canceling friend request:', error)
            });
        }
    }
}
