import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AuthService} from '../../services/auth-service/auth.service';

@Component({
    selector: 'app-profile-edit',
    standalone: true,
    imports: [
        HeaderComponent,
        RouterOutlet,
        FooterComponent,
        RouterLink,
        RouterLinkActive
    ],
    templateUrl: './profile-edit.component.html',
    styleUrl: './profile-edit.component.css'
})
export class ProfileEditComponent implements OnInit {
    constructor(private router: Router,
                private authService: AuthService) {
        this.router.events.subscribe();
    }

    ngOnInit(): void {
        if (!this.authService.isAuthenticated()) {
            this.router.navigate(['page-not-found']);
        }
    }


}
