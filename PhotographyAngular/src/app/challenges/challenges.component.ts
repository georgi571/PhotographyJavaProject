import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";

@Component({
  selector: 'app-challenges',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './challenges.component.html',
  styleUrl: './challenges.component.css'
})
export class ChallengesComponent {

}
