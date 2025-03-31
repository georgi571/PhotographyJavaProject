import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getUsers() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/change-roles`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    updateUserRole(userId: string, role: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/admin/change-roles/${userId}`, {role}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUsersForBan() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/ban-users`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    banUser(userId: string, reason: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/admin/ban-users/${userId}`, {
            action: 'ban',
            reason: reason
        }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    unbanUser(userId: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/admin/ban-users/${userId}`, {action: 'unban'}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUsersForApprove() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/approve-users`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    approveUser(userId: number): Observable<void> {
        const token = this.authService.getToken();
        return this.http.put<void>(`${this.apiUrl}/v1/admin/approve-users/${userId}`, {action: 'approve'}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    rejectUser(userId: number, reason: string): Observable<void> {
        const token = this.authService.getToken();
        return this.http.put<void>(`${this.apiUrl}/v1/admin/approve-users/${userId}`, {
            action: 'reject',
            reason: reason
        }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getAdminsWithPermissions() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/admin-permissions`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    updateAdminPermissions(adminId: string, permissionsToAdd: string[], permissionsToRemove: string[]): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put<any>(
            `${this.apiUrl}/v1/admin/admin-permissions/${adminId}`,
            {permissionsToAdd, permissionsToRemove},
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    getModeratorsWithPermissions() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/moderator-permissions`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    updateModeratorPermissions(moderatorId: string, permissionsToAdd: string[], permissionsToRemove: string[]): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put<any>(
            `${this.apiUrl}/v1/admin/moderator-permissions/${moderatorId}`,
            {permissionsToAdd, permissionsToRemove},
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    getPermissions() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/admin/permissions`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }
}
