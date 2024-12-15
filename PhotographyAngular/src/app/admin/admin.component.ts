import {Component} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FooterComponent} from '../core/footer/footer.component';

@Component({
  selector: 'app-admin',
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
export class AdminComponent {
  constructor(private router: Router) {
    this.router.events.subscribe(event => console.log(event));
  }
}
