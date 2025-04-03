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
    selectedUser: any = null;
    banReason: string = '';
    isBanModalOpen: boolean = false;
    isUnbanModalOpen: boolean = false;

    constructor(private adminService: AdminService) {}

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsersForBan().subscribe({
            next: (data) => {
                this.users = data;
                this.filteredUsers = data;
            },
            error: (err) => {
                console.error('Error fetching users:', err);
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

    openBanModal(user: any): void {
        this.selectedUser = user;
        this.isBanModalOpen = true;
    }

    closeBanModal(): void {
        this.isBanModalOpen = false;
        this.selectedUser = null;
        this.banReason = '';
    }

    openUnbanModal(user: any): void {
        this.selectedUser = user;
        this.isUnbanModalOpen = true;
    }

    closeUnbanModal(): void {
        this.isUnbanModalOpen = false;
        this.selectedUser = null;
    }

    confirmBan(): void {
        if (this.banReason.trim() === '') {
            alert('Please provide a reason for banning the user.');
            return;
        }

        this.adminService.banUser(this.selectedUser.id, this.banReason).subscribe({
            next: () => {
                this.selectedUser.banned = true;
                this.closeBanModal();
            },
            error: (err) => {
                console.error('Error banning user:', err);
            }
        });
    }

    unbanUser(): void {
        this.adminService.unbanUser(this.selectedUser.id).subscribe({
            next: () => {
                this.selectedUser.banned = false;
                this.closeUnbanModal();
            },
            error: (err) => {
                console.error('Error unbanning user:', err);
            }
        });
    }

    private updateUserStatus(userId: string, isBanned: boolean): void {
        const user = this.users.find(u => u.id === userId);
        if (user) {
            user.banned = isBanned;
        }
    }
}
