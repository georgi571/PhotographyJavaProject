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

    getUserEditDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/users/profile/edit`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUserDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/users/profile/edit`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUserOldUsernameDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/users/profile/edit/username`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUsernameDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/users/profile/edit/username`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUserOldEmailDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/users/profile/edit/email`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUserEmailDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/users/profile/edit/email`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    editUserPasswordDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/users/profile/edit/password`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
