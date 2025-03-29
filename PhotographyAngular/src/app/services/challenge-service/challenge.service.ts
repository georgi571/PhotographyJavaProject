import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment.development';
import {Router} from '@angular/router';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {

    private apiUrl = environment.apiUrl;
    private challengeUrl = environment.challengeUrl;
    private reportUrl = environment.reportUrl;

    constructor(private http: HttpClient, private router: Router, private authService: AuthService) { }

    getChallengePage() {
        return this.http.get<any[]>(`${this.challengeUrl}/v1/challenges`);
    }

    getAllChallenges() {
        return this.http.get<any[]>(`${this.challengeUrl}/v1/challenges/list`);
    }

    getChallengeDetails(challengeId: string) {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.challengeUrl}/v1/challenges/${challengeId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    uploadPicture(challengeId: string, formData: FormData) {
        const token = this.authService.getToken();
        return this.http.post(`${this.challengeUrl}/v1/challenges/${challengeId}/pictures`, formData, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    toggleLikePicture(pictureId: string) {
        const token = this.authService.getToken();
        return this.http.patch<any>(`${this.challengeUrl}/v1/challenges/pictures/${pictureId}/toggle-like`, {}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    addCommentToPicture(pictureId: string, commentText: string) {
        const token = this.authService.getToken();
        const commentData = { text: commentText };
        return this.http.post(`${this.challengeUrl}/v1/challenges/pictures/${pictureId}/comments`, commentData, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    reportPicture(challengeId: string, pictureId: string, reason: string, authorId: string) {
        const token = this.authService.getToken();
        return this.http.post(
            `${this.reportUrl}/v1/reports/pictures`,
            { challengeId: challengeId, pictureId: pictureId, reason: reason, authorId: authorId },
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    reportComment(challengeId: string, pictureId: string, commentId: string, reason: string, authorId: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.reportUrl}/v1/reports/comments`,
            { challengeId: challengeId, pictureId: pictureId, commentId: commentId, reason: reason, authorId: authorId },
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    deletePicture(pictureId: string) {
        const token = this.authService.getToken();
        return this.http.delete(
            `${this.challengeUrl}/v1/challenges/pictures/${pictureId}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    deleteComment(commentId: string) {
        const token = this.authService.getToken();
        return this.http.delete<any>(
            `${this.challengeUrl}/v1/challenges/comments/${commentId}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
    }

    createChallenge(challenge: any) {
        const token = this.authService.getToken();
        return this.http.post(`${this.challengeUrl}/v1/challenges/create-challenge`, challenge, {
            headers: { Authorization: `Bearer ${token}` }
        });
    }

    updateChallenge(challengeId: string, updatedDetails: any) {
        const token = this.authService.getToken();
        return this.http.put<any>(`${this.challengeUrl}/v1/challenges/${challengeId}`, updatedDetails, {
            headers: { Authorization: `Bearer ${token}` }
        });
    }

    deleteChallenge(challengeId: string) {
        const token = this.authService.getToken();
        return this.http.delete<void>(`${this.challengeUrl}/v1/challenges/${challengeId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
    }

    getReportedPicture(pictureId: string) {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.challengeUrl}/v1/challenges/reported/picture/${pictureId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getReportedComment(commentId: string) {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.challengeUrl}/v1/challenges/reported/comment/${commentId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getAllPicturesForUser(id: string) {
        const token = this.authService.getToken();
        return this.http.get<any>(`${this.challengeUrl}/v1/challenges/pictures/all/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
