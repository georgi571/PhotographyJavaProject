import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {Router, RouterLink} from '@angular/router';
import {ChallengeService} from '../services/challenge-service/challenge.service';
import {catchError, of} from 'rxjs';

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
export class ChallengesComponent implements OnInit {

    isLoading = true;

    constructor(private challengeService: ChallengeService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.challengeService.getChallengePage().pipe(
            catchError((error) => {
                this.router.navigate(['/server-down']);
                return of(null);
            })
        ).subscribe(() => {
            this.isLoading = false;
        });

    }



}
