import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";

@Component({
  selector: 'app-about',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css'
})
export class AboutComponent {

}
