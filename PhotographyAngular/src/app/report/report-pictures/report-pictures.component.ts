import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';

@Component({
  selector: 'app-report-pictures',
  imports: [],
  templateUrl: './report-pictures.component.html',
  styleUrl: './report-pictures.component.css'
})
export class ReportPicturesComponent implements OnInit{
    pictureReports: any[] = [];

    constructor(private reportService: ReportService) {}

    ngOnInit(): void {
        this.fetchPictureReports();
    }

    fetchPictureReports() {
        this.reportService.getAllPictureReports().subscribe({
            next: (data) => this.pictureReports = data,
            error: (error) => console.error('Error fetching picture reports:', error)
        });
    }

    deleteReport(reportId: string) {
        if (confirm('Are you sure you want to delete this report?')) {
            this.reportService.deleteReport(reportId).subscribe({
                next: () => this.fetchPictureReports(),
                error: (error) => console.error('Error deleting report:', error)
            });
        }
    }
}
