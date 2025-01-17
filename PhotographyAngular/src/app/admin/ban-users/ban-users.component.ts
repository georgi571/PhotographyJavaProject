import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AdminService} from '../../services/admin-service/admin.service';

@Component({
    selector: 'app-ban-users',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './ban-users.component.html',
    styleUrl: './ban-users.component.css'
})
export class BanUsersComponent implements OnInit {
    users: any[] = [];
    filteredUsers: any[] = [];
    searchTerm: string = '';

    constructor(private adminService: AdminService) {}

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsersForBan().subscribe({
            next: (data) => {
                this.users = data;
                this.filteredUsers = data;
                console.log('Loaded users:', this.filteredUsers); // Debug log
            },
            error: (err) => {
                console.error('Error fetching users:', err);
                alert('Failed to load users. Please try again later.');
            },
        });
    }

    filterUsers(): void {
        const searchTermLower = this.searchTerm.toLowerCase();
        this.filteredUsers = this.users.filter(user =>
            user.username.toLowerCase().includes(searchTermLower) ||
            user.email.toLowerCase().includes(searchTermLower)
        );
    }

    banUser(userId: string): void {
        this.adminService.banUser(userId).subscribe({
            next: () => {
                console.log('User banned successfully');
                this.updateUserStatus(userId, true);
            },
            error: (err) => console.error('Error banning user:', err),
        });
    }

    unbanUser(userId: string): void {
        this.adminService.unbanUser(userId).subscribe({
            next: () => {
                console.log('User unbanned successfully');
                this.updateUserStatus(userId, false);
            },
            error: (err) => console.error('Error unbanning user:', err),
        });
    }

    private updateUserStatus(userId: string, isBanned: boolean): void {
        const user = this.users.find((u) => u.id === userId);
        if (user) {
            user.banned = isBanned;
        }
    }
}
