import {Component} from '@angular/core';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';

@Component({
    selector: 'app-friends-followers',
    standalone: true,
    imports: [
        HeaderComponent,
        FooterComponent,
        RouterOutlet,
        RouterLink,
        RouterLinkActive
    ],
    templateUrl: './friends-followers.component.html',
    styleUrl: './friends-followers.component.css'
})
export class FriendsFollowersComponent {

}
