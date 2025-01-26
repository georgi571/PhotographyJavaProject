import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FooterComponent} from '../core/footer/footer.component';
import {AdminService} from '../services/admin-service/admin.service';

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
                console.log('Permissions:', data);
                // Process permissions logic
            },
            error: (error) => {
                console.error('Error fetching permissions:', error);
                // Show an error message to the user if needed
            }
        });
    }
}
