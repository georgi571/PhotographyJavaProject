import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
    selector: 'app-report-comments',
    imports: [],
    templateUrl: './report-comments.component.html',
    styleUrl: './report-comments.component.css'
})
export class ReportCommentsComponent implements OnInit {
    commentReports: any[] = [];
    selectedComment: any = null;
    reason: string = '';
    authorName: string = '';
    reportedBy: string = '';

    constructor(private reportService: ReportService,
                private challengeService: ChallengeService,
                private profileService: ProfileService) {
    }

    ngOnInit(): void {
        this.fetchCommentReports();
    }

    fetchCommentReports() {
        this.reportService.getAllCommentReports().subscribe({
            next: (data) => this.commentReports = data
        });
    }

    getCommentDetails(commentId: string, reason: string, authorId: string, reportedBy: string) {
        console.log('Fetching reported comment with ID:', commentId);

        this.challengeService.getReportedComment(commentId).subscribe({
            next: (data) => {
                console.log('Received comment details:', data);
                this.selectedComment = data;
                this.reason = reason;
            },
            error: (error) => console.error('Error fetching comment details:', error)
        });

        this.profileService.getUserById(authorId).subscribe({
            next: (data) => {
                console.log('Received comment details:', data);
                this.authorName = data.username;
            },
            error: (error) => console.error('Error fetching comment details:', error)
        });

        this.profileService.getUserById(reportedBy).subscribe({
            next: (data) => {
                console.log('Received comment details:', data);
                this.reportedBy = data.username;
            },
            error: (error) => console.error('Error fetching comment details:', error)
        });
    }

    deleteReport(reportId: string) {
        if (confirm('Are you sure you want to delete this report?')) {
            this.reportService.deleteReport(reportId).subscribe({
                next: () => this.fetchCommentReports()
            });
        }
    }

    closeModal() {
        this.selectedComment = null;
    }

    deleteComment(commentId: string) {
        if (confirm('Are you sure you want to delete this comment?')) {
            this.challengeService.deleteComment(commentId).subscribe({
                next: () => this.fetchCommentReports()
            });
        }
    }
}
