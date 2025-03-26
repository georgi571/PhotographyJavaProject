import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class ContactService {
    private apiUrl = environment.apiUrl;
    private contactUrl = environment.contactUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getAllContactMessage() {
        const token = this.authService.getToken();
        return this.http.get(`${this.contactUrl}/v1/contacts/receive`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    sendContactMessage(data: any): Observable<any> {
        return this.http.post(`${this.contactUrl}/v1/contacts/receive`, data);
    }

    sendReply(data: { id: string; answer: string }): Observable<any> {
        const token = this.authService.getToken();
            return this.http.patch(`${this.contactUrl}/v1/contacts/reply`, data , {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    deleteMessage(id: string): Observable<any> {
        const token = this.authService.getToken();
        return this.http.delete(`${this.contactUrl}/v1/contacts/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUserInfo() {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.apiUrl}/v1/users/user-info`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
