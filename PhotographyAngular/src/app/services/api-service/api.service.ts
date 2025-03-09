import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient) {
    }

    getCountries() {
        return this.http.get<string[]>(`${this.apiUrl}/v1/auth/register`);
    }

    registerUser(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/v1/auth/register`, data);
    }

    loginUser(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/v1/auth/login`, data);
    }


}
