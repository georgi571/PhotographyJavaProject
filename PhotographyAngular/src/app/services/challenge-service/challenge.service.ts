import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {catchError, of} from 'rxjs';
import {Router} from '@angular/router';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {

    private apiUrl = environment.apiUrl;

    constructor(private http: HttpClient, private router: Router, private authService: AuthService) { }

    getAllChallenges() {
        return this.http.get<any[]>(`${this.apiUrl}/challenges/list`);
    }

    getChallengeDetails(challengeId: string) {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.apiUrl}/challenges/${challengeId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError((error) => {
                this.router.navigate(['/challenges']);
                return of(null);
            })
        );
    }

    uploadPicture(challengeId: string, formData: FormData) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/challenges/${challengeId}/pictures`, formData, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    toggleLikePicture(challengeId: string, pictureId: string) {
        const token = this.authService.getToken();
        return this.http.put<any>(`${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/toggle-like`, {}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    addCommentToPicture(challengeId: string, pictureId: string, commentText: string) {
        const token = this.authService.getToken();
        const commentData = { text: commentText };
        return this.http.post(`${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/comments`, commentData, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    reportPicture(challengeId: string, pictureId: string, reason: string) {
        const token = this.authService.getToken();
        return this.http.post(
            `${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/report`,
            { text: reason },
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    reportComment(challengeId: string, pictureId: string, commentId: string, reason: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/comments/${commentId}/report`,
            { text: reason },
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    deletePicture(challengeId: string, pictureId: string) {
        const token = this.authService.getToken();
        return this.http.delete(
            `${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/delete`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    deleteComment(challengeId: string, pictureId: string, commentId: string) {
        const token = this.authService.getToken();
        return this.http.delete<any>(
            `${this.apiUrl}/challenges/${challengeId}/pictures/${pictureId}/comments/${commentId}/delete`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }
}
