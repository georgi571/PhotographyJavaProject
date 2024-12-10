import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";

@Component({
  selector: 'app-leaderboards',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './leaderboards.component.html',
  styleUrl: './leaderboards.component.css'
})
export class LeaderboardsComponent {

}
