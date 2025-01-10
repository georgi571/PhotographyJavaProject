import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private readonly TOKEN_KEY = 'JWT';

    constructor() {
    }

    // Save the token to localStorage
    setToken(token: string): void {
        localStorage.setItem(this.TOKEN_KEY, token);
    }

    // Retrieve the token from localStorage
    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    // Clear the token from localStorage
    clearToken(): void {
        localStorage.removeItem(this.TOKEN_KEY);
    }

    // Check if the user is authenticated
    isAuthenticated(): boolean {
        return !!this.getToken();
    }
}
