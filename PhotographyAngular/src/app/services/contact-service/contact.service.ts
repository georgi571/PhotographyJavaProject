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

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getAllContactMessage() {
        return this.http.get(`${this.apiUrl}/contacts/receive`);
    }

    sendContactMessage(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/contacts/receive`, data);
    }

    sendReply(data: { id: string; answer: string }): Observable<any> {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/contacts/reply`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
