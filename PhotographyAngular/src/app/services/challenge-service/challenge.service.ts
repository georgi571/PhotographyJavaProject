import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {catchError, of} from 'rxjs';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {

    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient, private router: Router) { }

    getAllChallenges() {
        return this.http.get<any[]>(`${this.apiUrl}/challenges/list`);
    }

    getChallengeDetails(challengeId: string) {
        return this.http.get<any>(`${this.apiUrl}/challenges/${challengeId}`).pipe(
            catchError((error) => {
                this.router.navigate(['/challenges']);
                return of(null);
            })
        );
    }
}
