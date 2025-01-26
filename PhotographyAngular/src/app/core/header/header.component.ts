import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../services/auth-service/auth.service';
import {JwtService} from '../../services/jwt-service/jwt.service';

@Component({
    selector: 'app-header',
    imports: [
        RouterLink,
        RouterLinkActive
    ],
    templateUrl: './header.component.html',
    styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
    username: string | null = null;
    role: string | null = null;

    constructor(
        private authService: AuthService,
        private jwtService: JwtService,
        private router: Router) {
    }

    // get username from jwt token

    ngOnInit(): void {
        const token = this.authService.getToken();
        if (token) {
            const decodedToken = this.jwtService.decodeToken(token);
            this.username = decodedToken?.username || null;
            this.role = decodedToken?.role ? decodedToken?.role.replace("ROLE_", "") : null;
        }
    }

    // logout and clear the jwt token

    logout() {
        this.authService.clearToken();
        this.router.navigate(['/']);
    }
}
