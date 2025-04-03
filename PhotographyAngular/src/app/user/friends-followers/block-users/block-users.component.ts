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

    blockedUsers: any[] = [];
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
            this.filterBlockedUsers();
        });
    }

    filterBlockedUsers() {
        const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
        this.filteredBlockedUsers = this.blockedUsers.filter(user =>
            user.username.toLowerCase().includes(lowerCaseSearchTerm)
        );
    }

    viewUserProfile(user: any) {
        this.router.navigate(['/profile/username', user.username]);
    }

    unblockUser(user: any) {
        if (confirm(`Are you sure you want to unblock ${user.username}?`)) {
            this.profileService.unblockUser(user.username).subscribe(() => {
                alert('User was successfully unblocked.');
                this.blockedUsers = this.blockedUsers.filter(user => user.username !== user.username);
                this.filterBlockedUsers();
            });
        }
        window.location.reload();
    }

}
