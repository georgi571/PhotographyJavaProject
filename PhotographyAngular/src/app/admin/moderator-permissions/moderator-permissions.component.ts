import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AdminService} from '../../services/admin-service/admin.service';

@Component({
    selector: 'app-moderator-permissions',
    standalone: true,
    imports: [
        FormsModule
    ],
    templateUrl: './moderator-permissions.component.html',
    styleUrl: './moderator-permissions.component.css'
})
export class ModeratorPermissionsComponent implements OnInit {
    moderators: any[] = [];
    originalPermissions: { [moderatorId: string]: any } = {};

    constructor(private adminService: AdminService) {}

    ngOnInit(): void {
        this.getModerators();
    }

    initializePermissions(permissions: any[]): any {
        if (permissions.length === 0) {
            return {
                deleteMessage: false,
                deletePicture: false,
            };
        }
        return permissions.reduce((acc, permission) => {
            acc[permission] = true;
            return acc;
        }, {});
    }

    getModerators(): void {
        this.adminService.getModeratorsWithPermissions().subscribe((data) => {
            this.moderators = data.map((moderator) => {
                const permissions = this.initializePermissions(moderator.permissions);

                this.originalPermissions[moderator.id] = { ...permissions };

                return {
                    ...moderator,
                    permissions,
                };
            });
        });
    }

    saveModeratorPermissions(moderator: any): void {
        const permissionsToAdd = [];
        const permissionsToRemove = [];

        for (const permission in moderator.permissions) {
            if (moderator.permissions.hasOwnProperty(permission)) {
                if (moderator.permissions[permission] && !this.originalPermissions[moderator.id][permission]) {
                    permissionsToAdd.push(permission);
                } else if (!moderator.permissions[permission] && this.originalPermissions[moderator.id][permission]) {
                    permissionsToRemove.push(permission);
                }
            }
        }

        this.adminService
            .updateModeratorPermissions(moderator.id, permissionsToAdd, permissionsToRemove)
            .subscribe({
                next: (updatedModerator) => {
                    for (const permission in updatedModerator.permissions) {
                        if (updatedModerator.permissions.hasOwnProperty(permission)) {
                            moderator.permissions[permission] = updatedModerator.permissions[permission];
                        }
                    }
                    this.originalPermissions[moderator.id] = { ...moderator.permissions };

                    alert('Permissions saved!');
                },
                error: () => {
                    alert('Failed to update permissions. Please try again later.');
                },
            });
    }
}
