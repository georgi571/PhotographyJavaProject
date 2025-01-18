import {Component, OnInit} from '@angular/core';
import {AdminService} from '../../services/admin-service/admin.service';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-approve-users',
    imports: [
        FormsModule
    ],
    templateUrl: './approve-users.component.html',
    styleUrl: './approve-users.component.css',
})
export class ApproveUsersComponent implements OnInit {
    users: any[] = [];
    selectedUser: any = null;
    isRejectModalOpen: boolean = false;
    rejectReason: string = '';

    constructor(private adminService: AdminService) {
    }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsersForApprove().subscribe((users) => {
            this.users = users;
        });
    }

    approveUser(user: any): void {
        this.adminService.approveUser(user.id).subscribe(() => {
            user.isApproved = true;

            this.users = this.users.filter(u => u.id !== user.id);

            console.log(`User ${user.username} approved.`);
            alert('User was approved.');
        });
    }

    openRejectModal(user: any): void {
        this.selectedUser = user;
        this.isRejectModalOpen = true;
    }

    closeRejectModal(): void {
        this.isRejectModalOpen = false;
        this.rejectReason = '';
        this.selectedUser = null;
    }

    rejectUser(): void {
        if (this.rejectReason.trim() === '') {
            alert('Please provide a reason for rejecting the user.');
            return;
        }

        this.adminService.rejectUser(this.selectedUser.id, this.rejectReason).subscribe(() => {
            this.users = this.users.filter(u => u.id !== this.selectedUser.id);
            console.log(`User ${this.selectedUser.username} rejected for reason: ${this.rejectReason}`);
            this.closeRejectModal();
        });
    }
}
