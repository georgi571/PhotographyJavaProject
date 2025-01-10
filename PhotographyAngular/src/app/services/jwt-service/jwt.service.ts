import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class JwtService {

    decodeToken(token: string): any {
        try {
            const payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch (error) {
            console.error('Invalid JWT token', error);
            return null;
        }
    }
}
