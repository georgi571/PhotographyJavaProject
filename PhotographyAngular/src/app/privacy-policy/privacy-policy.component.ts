import { Component } from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from '../core/footer/footer.component';

@Component({
  selector: 'app-privacy-policy',
    imports: [
        HeaderComponent,
        FooterComponent
    ],
  templateUrl: './privacy-policy.component.html',
  styleUrl: './privacy-policy.component.css'
})
export class PrivacyPolicyComponent {

}
