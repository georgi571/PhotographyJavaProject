import {Component} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';

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
export class ProfileEditComponent {
    constructor(private router: Router) {
        this.router.events.subscribe(event => console.log(event));
    }
}
