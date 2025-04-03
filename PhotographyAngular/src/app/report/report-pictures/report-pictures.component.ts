import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
  selector: 'app-report-pictures',
  imports: [],
  templateUrl: './report-pictures.component.html',
  styleUrl: './report-pictures.component.css'
})
export class ReportPicturesComponent implements OnInit{
    pictureReports: any[] = [];
    selectedPicture: any = null;
    reason: string = '';
    authorName: string = '';
    reportedBy: string = '';
    showPicturePopup: boolean = false;

    constructor(private reportService: ReportService,
                private challengeService: ChallengeService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        this.fetchPictureReports();
    }

    fetchPictureReports() {
        this.reportService.getAllPictureReports().subscribe({
            next: (data) => this.pictureReports = data,
            error: (error) => console.error('Error fetching picture reports:', error)
        });
    }

    getPictureDetails(pictureId: string, reason: string, authorId: string, reportedBy: string) {

        this.challengeService.getReportedPicture(pictureId).subscribe({
            next: (data) => {
                this.selectedPicture = data;
                this.reason = reason;
            },
            error: (error) => console.error('Error fetching picture details:', error)
        });

        this.profileService.getUserById(authorId).subscribe({
            next: (data) => {
                this.authorName = data.username;
            },
            error: (error) => console.error('Error fetching picture details:', error)
        });

        this.profileService.getUserById(reportedBy).subscribe({
            next: (data) => {
                this.reportedBy = data.username;
            },
            error: (error) => console.error('Error fetching picture details:', error)
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

    closeModal() {
        this.selectedPicture = null;
    }

    deletePicture(pictureId: string) {
        if (confirm('Are you sure you want to delete this picture?')) {
            this.challengeService.deletePicture(pictureId).subscribe({
                next: () => {
                    this.fetchPictureReports();
                    alert('Picture deleted successfully.');
                }
            });
        }
    }

    openPicturePopup() {
        this.showPicturePopup = true;
    }

    closePicturePopup() {
        this.showPicturePopup = false;
    }
}
