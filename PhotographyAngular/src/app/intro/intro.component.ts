import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-intro',
  imports: [],
  templateUrl: './intro.component.html',
  styleUrl: './intro.component.css'
})
export class IntroComponent implements OnInit {

  showIntro = true;

  constructor(private router: Router) {}

  ngOnInit(): void {}

  onJourneyClick(): void {
    this.router.navigate(['/home']);
  }
}
