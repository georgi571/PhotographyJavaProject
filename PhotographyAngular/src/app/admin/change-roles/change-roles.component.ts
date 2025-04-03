import {Component, OnInit} from '@angular/core';
import {AdminService} from '../../services/admin-service/admin.service';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-change-roles',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './change-roles.component.html',
    styleUrl: './change-roles.component.css'
})
export class ChangeRolesComponent implements OnInit {
    users: any[] = [];
    filteredUsers: any[] = [];
    searchTerm: string = '';
    loadingUsers: Set<string> = new Set();

    constructor(private adminService: AdminService) {
    }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsers().subscribe({
            next: (data) => {
                this.users = data;
                this.filteredUsers = data;
            },
            error: (err) => {
                console.error('Error fetching users:', err);
            }
        });
    }

    filterUsers(): void {
        const searchTermLower = this.searchTerm.toLowerCase();
        this.filteredUsers = this.users.filter(user =>
            user.username.toLowerCase().includes(searchTermLower)
        );
    }

    saveRole(userId: string, newRole: string): void {
        if (this.loadingUsers.has(userId)) return;

        this.loadingUsers.add(userId);

        this.adminService.updateUserRole(userId, newRole).subscribe({
            next: () => {
                const user = this.users.find(u => u.id === userId);
                if (user) {
                    user.role = newRole;
                }
                alert('Role updated successfully');
            },
            error: (err) => {
                console.error(`Failed to update role for user ${userId}:`, err);
                alert('Failed to update role. Please try again.');
            },
            complete: () => {
                this.loadingUsers.delete(userId);
            }
        });
    }

    isUpdating(userId: string): boolean {
        return this.loadingUsers.has(userId);
    }
}
