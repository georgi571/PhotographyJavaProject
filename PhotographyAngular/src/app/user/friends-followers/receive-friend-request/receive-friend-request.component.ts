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
    receivedFriendRequests: any[] = [];
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
        this.profileService.getReceivedFriendRequests().subscribe({
            next: (data) => {
                this.receivedFriendRequests = data;
                this.filterReceivedRequests();
                console.log(data);
            },
            error: (error) => console.error('Error fetching sent requests:', error)
        });
    }

    filterReceivedRequests() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredReceivedRequests = this.receivedFriendRequests.filter(friend =>
            friend.username.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewFriendProfile(friend: any) {
        this.router.navigate(['/profile', friend.username]);
    }

    acceptRequest(friend: any): void {
        this.profileService.acceptFriendRequest(friend.username).subscribe(
            response => {
                alert('Friend request accepted!');
                this.filterReceivedRequests();
            }
        );
        window.location.reload();
    }

    rejectRequest(friend: any): void {
        this.profileService.rejectFriendRequest(friend.username).subscribe(
            response => {
                alert('Friend request rejected!');
                this.filterReceivedRequests();
            }
        );
    }

    confirmBlock(friend: any) {
        if (confirm(`Are you sure you want to block the user ${friend.username}?`)) {
            this.profileService.blockUser(friend.username).subscribe(
                response => {
                    alert('User was successfully blocked.');
                    this.receivedFriendRequests = this.receivedFriendRequests.filter(f => f.username !== friend.username);
                    this.filterReceivedRequests();
                }
            );
        }
        window.location.reload();
    }
}
