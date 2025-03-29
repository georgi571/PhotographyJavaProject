import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report/report.service';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ProfileService} from '../../services/profile-service/profile.service';
import {AdminService} from '../../services/admin-service/admin.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-report-users',
    imports: [
        FormsModule
    ],
  templateUrl: './report-users.component.html',
  styleUrl: './report-users.component.css'
})
export class ReportUsersComponent implements OnInit{
    userReports: any[] = [];
    selectedUser: any = null;
    reason: string = '';
    userName: string = '';
    userId: string = '';
    reportedBy: string = '';
    isBanModalOpen: boolean = false;
    banReason: string = '';

    constructor(private reportService: ReportService,
                private adminService: AdminService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        this.fetchUserReports();
    }

    fetchUserReports() {
        this.reportService.getAllUserReports().subscribe({
            next: (data) => this.userReports = data,
            error: (error) => console.error('Error fetching user reports:', error)
        });
    }

    getUserDetails(reason: string, userId: string, reportedBy: string) {
        this.profileService.getUserById(userId).subscribe({
            next: (data) => {
                console.log('Received picture details:', data);
                this.userName = data.username;
                this.userId = userId;
                this.reason = reason;
            },
            error: (error) => console.error('Error fetching picture details:', error)
        });

        this.profileService.getUserById(reportedBy).subscribe({
            next: (data) => {
                console.log('Received picture details:', data);
                this.reportedBy = data.username;
            },
            error: (error) => console.error('Error fetching picture details:', error)
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

    closeModal() {
        this.selectedUser = null;
    }

    openBanModal(user: any): void {
        this.selectedUser = user;
        this.isBanModalOpen = true;
    }

    closeBanModal(): void {
        this.isBanModalOpen = false;
        this.selectedUser = null;
        this.banReason = '';
    }

    confirmBan(): void {
        if (this.banReason.trim() === '') {
            alert('Please provide a reason for banning the user.');
            return;
        }

        this.adminService.banUser(this.selectedUser.id, this.banReason).subscribe({
            next: () => {
                this.closeBanModal();
            },
            error: (err) => {
                console.error('Error banning user:', err);
            }
        });
    }

}
