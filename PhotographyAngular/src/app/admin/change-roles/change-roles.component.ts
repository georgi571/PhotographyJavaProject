import {Component, OnInit} from '@angular/core';
import {AdminService} from '../../services/admin-service/admin.service';

@Component({
  selector: 'app-change-roles',
  imports: [],
  templateUrl: './change-roles.component.html',
  styleUrl: './change-roles.component.css'
})
export class ChangeRolesComponent implements OnInit{
    users: any[] = [];
    loadingUsers: Set<string> = new Set();

    constructor(private adminService: AdminService) {}

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsers().subscribe({
            next: (data) => {
                this.users = data;
            },
            error: (err) => {
                console.error('Error fetching users:', err);
                alert('Failed to load users. Please try again later.');
            }
        });
    }

    saveRole(userId: string, newRole: string): void {
        if (this.loadingUsers.has(userId)) return;

        this.loadingUsers.add(userId);

        this.adminService.updateUserRole(userId, newRole).subscribe({
            next: (response) => {
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
