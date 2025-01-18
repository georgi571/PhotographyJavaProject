import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AdminService} from '../../services/admin-service/admin.service';

@Component({
    selector: 'app-admin-permissions',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './admin-permissions.component.html',
    styleUrl: './admin-permissions.component.css'
})
export class AdminPermissionsComponent implements OnInit {
    admins: any[] = [];
    originalPermissions: { [adminId: string]: any } = {};

    constructor(private adminService: AdminService) {
    }

    ngOnInit(): void {
        this.loadAdmins();
    }

    loadAdmins(): void {
        this.adminService.getAdminsWithPermissions().subscribe((admins) => {
            this.admins = admins.map(admin => {
                const permissions = this.initializePermissions(admin.permissions);
                this.originalPermissions[admin.id] = {...permissions};
                return {
                    ...admin,
                    permissions
                };
            });
        });
    }

    initializePermissions(permissions: any[]): any {
        if (permissions.length === 0) {
            return {
                approveUsers: false,
                changeUserRoles: false,
                banUsers: false,
                answerFeedback: false,
                deleteMessage: false,
                deletePicture: false
            };
        }
        return permissions.reduce((acc, permission) => {
            acc[permission] = true;
            return acc;
        }, {});
    }

    savePermissions(admin: any): void {
        const permissionsToAdd = [];
        const permissionsToRemove = [];

        for (const permission in admin.permissions) {
            if (admin.permissions.hasOwnProperty(permission)) {
                if (admin.permissions[permission] && !this.originalPermissions[admin.id][permission]) {
                    permissionsToAdd.push(permission);
                } else if (!admin.permissions[permission] && this.originalPermissions[admin.id][permission]) {
                    permissionsToRemove.push(permission);
                }
            }
        }

        this.adminService.updateAdminPermissions(admin.id, permissionsToAdd, permissionsToRemove).subscribe({
            next: (updatedAdmin) => {
                for (const permission in updatedAdmin.permissions) {
                    if (updatedAdmin.permissions.hasOwnProperty(permission)) {
                        admin.permissions[permission] = updatedAdmin.permissions[permission];
                    }
                }

                this.originalPermissions[admin.id] = {...admin.permissions};

                alert('Permissions saved!');
            },
            error: () => {
                alert('Failed to update permissions. Please try again later.');
            },
        });
    }
}
