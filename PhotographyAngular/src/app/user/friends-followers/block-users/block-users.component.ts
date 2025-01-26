import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {ProfileService} from '../../../services/profile-service/profile.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-block-users',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './block-users.component.html',
    styleUrl: './block-users.component.css'
})
export class BlockUsersComponent implements OnInit{

    // blockedUsers: string[] = [];

    blockedUsers = [
        { id: 1, username: 'JohnDoe', email: 'john@example.com', profilePicture: 'assets/images/john.png' },
        { id: 2, username: 'JaneSmith', email: 'jane@example.com', profilePicture: 'assets/images/jane.png' },
        // Add more blocked users here...
    ];

    filteredBlockedUsers: any[] = [];
    searchTerm: string = '';

    constructor(private profileService: ProfileService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.getAllBlockedUsers();
        this.filterBlockedUsers();
    }

    getAllBlockedUsers(): void {
        this.profileService.getBlockedUsers().subscribe(data => {
            this.blockedUsers = data;
        });
    }

    filterBlockedUsers() {
        this.filteredBlockedUsers = this.blockedUsers.filter(user => {
            return (
                user.username.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                user.email.toLowerCase().includes(this.searchTerm.toLowerCase())
            );
        });
    }

    viewUserProfile(user: any) {
        this.router.navigate(['/profile', user.username]);
    }

    // Unblock the user
    unblockUser(user: any) {
        const index = this.blockedUsers.indexOf(user);
        if (index !== -1) {
            this.blockedUsers.splice(index, 1);
            this.filterBlockedUsers();  // Re-filter after unblocking
        }
    }

}
