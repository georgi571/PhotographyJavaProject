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

    getUserDetails(username: string) {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/v1/users/profile/username/${username}`, {
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
        return this.http.get<string[]>(`${this.apiUrl}/v1/users/profile/edit`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUserDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/users/profile/edit`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUserOldUsernameDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/v1/users/profile/edit/username`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUsernameDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/users/profile/edit/username`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getUserOldEmailDetails() {
        const token = this.authService.getToken();
        return this.http.get<string[]>(`${this.apiUrl}/v1/users/profile/edit/email`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        }).pipe(
            catchError(this.handleError)
        );
    }

    editUserEmailDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/users/profile/edit/email`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    editUserPasswordDetail(data: any) {
        const token = this.authService.getToken();
        return this.http.put(`${this.apiUrl}/v1/users/profile/edit/password`, data, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    addFriend(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/add-friend`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    followUser(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/follow-user`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    acceptFriendRequest(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/accept-friend-request`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    rejectFriendRequest(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/reject-friend-request`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getFriends() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/friends`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getFollowers() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/followers`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getFollowing() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/following`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getSentFriendRequests() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/sent-requests`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getReceivedFriendRequests() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/received-requests`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getBlockedUsers() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/blocked-users`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    removeFriend(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/remove-friend`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    cancelFriendRequest(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/cancel-friend-request`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    unfollowUser(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/unfollow-user`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    removeFollower(username: string) {
        const token = this.authService.getToken();
        return this.http.post(`${this.apiUrl}/v1/users/remove-follower`, {username}, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    checkIfFriends(targetUsername: string) {
        const token = this.authService.getToken();
        return this.http.get<boolean>(`${this.apiUrl}/v1/users/are-friends?targetUsername=${targetUsername}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    checkIfFriendRequestSent(targetUsername: string) {
        const token = this.authService.getToken();
        return this.http.get<boolean>(`${this.apiUrl}/v1/users/has-sent-friend-request?targetUsername=${targetUsername}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    checkIfFollowing(targetUsername: string) {
        const token = this.authService.getToken();
        return this.http.get<boolean>(`${this.apiUrl}/v1/users/is-following?targetUsername=${targetUsername}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    blockUser(username: string) {
        const token = this.authService.getToken();
        return this.http.post<string>(`${this.apiUrl}/v1/users/block`, username, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    unblockUser(username: string) {
        const token = this.authService.getToken();
        return this.http.post<string>(`${this.apiUrl}/v1/users/unblock`, username, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    isUserBlocked(username: string) {
        const token = this.authService.getToken();
        return this.http.get<boolean>(`${this.apiUrl}/v1/users/is-blocked/${username}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getFriendsForProfile(username: string) {
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/curr/friends?username=${username}`);
    }

    getFollowersForProfile(username: string) {
        return this.http.get<any[]>(`${this.apiUrl}/v1/users/curr/followers?username=${username}`);
    }

}
