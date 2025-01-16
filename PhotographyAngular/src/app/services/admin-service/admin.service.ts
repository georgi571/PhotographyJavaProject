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
}
