import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';

@Component({
  selector: 'app-report-users',
  imports: [],
  templateUrl: './report-users.component.html',
  styleUrl: './report-users.component.css'
})
export class ReportUsersComponent implements OnInit{
    userReports: any[] = [];

    constructor(private reportService: ReportService) {}

    ngOnInit(): void {
        this.fetchUserReports();
    }

    fetchUserReports() {
        this.reportService.getAllUserReports().subscribe({
            next: (data) => this.userReports = data,
            error: (error) => console.error('Error fetching user reports:', error)
        });
    }

    deleteReport(reportId: string) {
        if (confirm('Are you sure you want to delete this report?')) {
            this.reportService.deleteReport(reportId).subscribe({
                next: () => this.fetchUserReports(),
                error: (error) => console.error('Error deleting report:', error)
            });
        }
    }
}
