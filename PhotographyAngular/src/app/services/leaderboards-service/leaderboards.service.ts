import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class LeaderboardsService {
    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getCountries() {
        return this.http.get<string[]>(`${this.apiUrl}/leaderboards/countries-choice`);
    }

    getChallengeTypes() {
        return this.http.get<string[]>(`${this.apiUrl}/leaderboards/challenge-types`);
    }

    getTopUsersByPoints(country: string) {
        return this.http.get(`${this.apiUrl}/leaderboards/country`);
    }

    getTopUsersByChallenges(challengeType: string) {
        return this.http.get(`${this.apiUrl}/leaderboards/challenges`);
    }

    getPhotographersOfMonth() {
        return this.http.get(`${this.apiUrl}/leaderboards/month`);
    }

    getActiveUsers() {
        return this.http.get(`${this.apiUrl}/leaderboards/active`);
    }

    getRisingStars() {
        return this.http.get(`${this.apiUrl}/leaderboards/rising`);
    }
}
