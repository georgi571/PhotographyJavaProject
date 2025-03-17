import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment.development';
import {HttpClient} from '@angular/common/http';
import {AuthService} from '../auth-service/auth.service';

@Injectable({
    providedIn: 'root'
})
export class ReportService {

    private apiUrl = environment.reportUrl;

    constructor(private http: HttpClient,
                private authService: AuthService) {
    }

    getAllPictureReports() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/reports/pictures`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getAllCommentReports() {
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/reports/comments`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    getAllUserReports(){
        const token = this.authService.getToken();
        return this.http.get<any[]>(`${this.apiUrl}/v1/reports/users`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    deleteReport(reportId: string) {
        const token = this.authService.getToken();
        return this.http.delete<void>(`${this.apiUrl}/v1/reports/report/${reportId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
}
