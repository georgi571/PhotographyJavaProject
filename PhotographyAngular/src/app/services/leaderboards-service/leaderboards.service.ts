import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment.development';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class LeaderboardsService {
    private apiUrl = environment.leaderboardUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getLeaderboardsPage() {
        return this.http.get<any[]>(`${this.apiUrl}/v1/leaderboards`);
    }

    getCountries() {
        return this.http.get<string[]>(`${this.apiUrl}/v1/leaderboards/countries-choice`);
    }

    getChallengeTypes() {
        return this.http.get<string[]>(`${this.apiUrl}/v1/leaderboards/challenge-types`);
    }

    getTopUsersByPoints(country: string) {
        return this.http.get(`${this.apiUrl}/v1/leaderboards/country`);
    }

    getTopUsersByChallenges(challengeType: string) {
        return this.http.get(`${this.apiUrl}/v1/leaderboards/challenges`);
    }

    getPhotographersOfMonth(year: number, month: string) {
        const params = new HttpParams()
            .set('year', year)
            .set('month', month);
        return this.http.get(`${this.apiUrl}/v1/leaderboards/month`, { params });
    }

    getRisingStars() {
        return this.http.get(`${this.apiUrl}/v1/leaderboards/rising`);
    }

    getUserStatistics(userId: string) {
        const token = this.authService.getToken();
        return this.http.get(`${this.apiUrl}/v1/leaderboards/rank/${userId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
