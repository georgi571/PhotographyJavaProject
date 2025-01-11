import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {AuthService} from '../auth-service/auth.service';
import {catchError, throwError} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ProfileService {

    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getUserDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/users/profile`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    private handleError(error: HttpErrorResponse) {
        console.error('ProfileService Error:', error);
        let message = 'An unknown error occurred!';
        if (error.error instanceof ErrorEvent) {
            message = `Client-side error: ${error.error.message}`;
        } else if (error.status === 401) {
            message = 'Unauthorized! Please log in again.';
        } else if (error.status === 404) {
            message = 'User profile not found!';
        }
        return throwError(() => new Error(message));
    }
}
