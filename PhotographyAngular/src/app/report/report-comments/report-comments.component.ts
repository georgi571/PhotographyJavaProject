import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';

@Component({
  selector: 'app-report-comments',
  imports: [],
  templateUrl: './report-comments.component.html',
  styleUrl: './report-comments.component.css'
})
export class ReportCommentsComponent implements OnInit{
    commentReports: any[] = [];

    constructor(private reportService: ReportService) {}

    ngOnInit(): void {
        this.fetchCommentReports();
    }

    fetchCommentReports() {
        this.reportService.getAllCommentReports().subscribe({
            next: (data) => this.commentReports = data,
            error: (error) => console.error('Error fetching comment reports:', error)
        });
    }

    deleteReport(reportId: string) {
        if (confirm('Are you sure you want to delete this report?')) {
            this.reportService.deleteReport(reportId).subscribe({
                next: () => this.fetchCommentReports(),
                error: (error) => console.error('Error deleting report:', error)
            });
        }
    }
}
