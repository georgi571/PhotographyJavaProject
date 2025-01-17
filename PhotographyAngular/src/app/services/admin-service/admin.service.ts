import { Injectable } from '@angular/core';
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
        return this.http.get<any[]>(`${this.apiUrl}/admin/change-roles`);
    }

    updateUserRole(userId: string, role: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/admin/change-roles/${userId}`, { role }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUsersForBan() {
        return this.http.get<any[]>(`${this.apiUrl}/admin/ban-users`);
    }

    banUser(userId: string, reason: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/admin/ban-users/${userId}`, {
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
        return this.http.put(`${this.apiUrl}/admin/ban-users/${userId}`, { action: 'unban' }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUsersForApprove() {
        return this.http.get<any[]>(`${this.apiUrl}/admin/approve-users`);
    }

    approveUser(userId: number): Observable<void> {
        const token = this.authService.getToken();
        return this.http.put<void>(`${this.apiUrl}/admin/approve-users/${userId}`, { action: 'approve' }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    rejectUser(userId: number, reason: string): Observable<void> {
        const token = this.authService.getToken();
        return this.http.put<void>(`${this.apiUrl}/admin/approve-users/${userId}`, {
            action: 'reject',
            reason: reason
        }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
