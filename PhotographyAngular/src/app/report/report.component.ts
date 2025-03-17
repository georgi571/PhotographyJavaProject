import {Component, OnInit} from '@angular/core';
import {FooterComponent} from '../core/footer/footer.component';
import {HeaderComponent} from '../core/header/header.component';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AdminService} from '../services/admin-service/admin.service';

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
                private adminService: AdminService) {
        this.router.events.subscribe(event => console.log(event));
    }

    ngOnInit(): void {
        this.fetchPermissions();
    }

    fetchPermissions() {
        console.log('Fetching permissions...');
        this.adminService.getPermissions().subscribe({
            next: (data) => {
                if (data.includes('banUsers')) this.ban = true;
                if (data.includes('deleteMessage')) this.comments = true;
                if (data.includes('deletePicture')) this.picture = true;
                console.log(data);
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
            }
        });
    }
}
