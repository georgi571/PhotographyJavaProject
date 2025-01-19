import {Component} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {RouterLink} from '@angular/router';

@Component({
    selector: 'app-challenges',
    standalone: true,
    imports: [
        HeaderComponent,
        FooterComponent,
        RouterLink
    ],
    templateUrl: './challenges.component.html',
    styleUrl: './challenges.component.css'
})
export class ChallengesComponent {

}
