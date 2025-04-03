import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FooterComponent} from '../core/footer/footer.component';
import {AdminService} from '../services/admin-service/admin.service';
import {AuthService} from '../services/auth-service/auth.service';

@Component({
    selector: 'app-admin',
    standalone: true,
    imports: [
        HeaderComponent,
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        FooterComponent
    ],
    templateUrl: './admin.component.html',
    styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit{

    approve: boolean = false;
    roles: boolean = false;
    ban: boolean = false;
    feedback: boolean = false;
    admin: boolean = false;
    moderator: boolean = false;


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

                if (data.includes('approveUsers')) this.approve = true;
                if (data.includes('changeUserRoles')) this.roles = true;
                if (data.includes('banUsers')) this.ban = true;
                if (data.includes('answerFeedback')) this.feedback = true;
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
            }
        });
    }
}
