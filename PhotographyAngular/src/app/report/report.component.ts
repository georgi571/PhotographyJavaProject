import {Component, OnInit} from '@angular/core';
import {FooterComponent} from '../core/footer/footer.component';
import {HeaderComponent} from '../core/header/header.component';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AdminService} from '../services/admin-service/admin.service';
import {AuthService} from '../services/auth-service/auth.service';

@Component({
    selector: 'app-report',
    imports: [
        FooterComponent,
        HeaderComponent,
        RouterLink,
        RouterLinkActive,
        RouterOutlet
    ],
    templateUrl: './report.component.html',
    styleUrl: './report.component.css'
})
export class ReportComponent implements OnInit {
    ban: boolean = false;
    comments: boolean = false;
    picture: boolean = false;

    constructor(private router: Router,
                private adminService: AdminService,
                private authService: AuthService) {
        this.router.events.subscribe();
    }

    ngOnInit(): void {
        this.fetchPermissions();
    }

    fetchPermissions() {
        if (!this.authService.isAuthenticated()) {
            this.router.navigate(['page-not-found']);
        }

        this.adminService.getPermissions().subscribe({
            next: (data) => {
                if (data.length === 0) {
                    console.warn('No permissions found. Redirecting...');
                    this.router.navigate(['page-not-found']);
                    return;
                }

                if (data.includes('banUsers')) this.ban = true;
                if (data.includes('deleteMessage')) this.comments = true;
                if (data.includes('deletePicture')) this.picture = true;
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
            }
        });
    }
}
