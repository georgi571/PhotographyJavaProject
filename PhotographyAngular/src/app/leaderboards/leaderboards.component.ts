import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {LeaderboardsService} from '../services/leaderboards-service/leaderboards.service';
import {FormsModule} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {catchError, of} from 'rxjs';

@Component({
    selector: 'app-leaderboards',
    standalone: true,
    imports: [
        HeaderComponent,
        FooterComponent,
        FormsModule,
        RouterLink
    ],
    templateUrl: './leaderboards.component.html',
    styleUrl: './leaderboards.component.css'
})
export class LeaderboardsComponent implements OnInit {
    isLoading = true;

    rawUsersByPoints: any[] = [];
    rawUsersByChallenges: any[] = [];
    usersByPoints: any[] = [];
    usersByChallenges: any[] = [];
    photographersOfMonth: any[] = [];
    risingStars: any[] = [];

    countries: string[] = [];
    challengeTypes: string[] = [];
    selectedCountry: string = 'all';
    selectedChallengeType: string = 'all';

    constructor(private leaderboardsService: LeaderboardsService,
                private router: Router) {}

    ngOnInit(): void {

        this.leaderboardsService.getLeaderboardsPage().pipe(
            catchError((error) => {
                this.router.navigate(['/server-down']);
                return of(null);
            })
        ).subscribe(() => {
            this.fetchUsersByPoints();
            this.fetchUsersByChallenges();
            this.fetchPhotographersOfMonth();
            this.fetchRisingStars();
            this.loadCountries();
            this.loadChallengeTypes();

            this.isLoading = false;
        });
    }

    loadCountries(): void {
        this.leaderboardsService.getCountries().subscribe((countries: string[]) => {
            this.countries = countries;
        });
    }

    loadChallengeTypes(): void {
        this.leaderboardsService.getChallengeTypes().subscribe((challengeTypes: string[]) => {
            this.challengeTypes = challengeTypes;
        });
    }

    fetchUsersByPoints(): void {
        this.leaderboardsService.getTopUsersByPoints('').subscribe({
            next: (data: any) => {
                this.rawUsersByPoints = data;
                this.applyCountryFilter();
            },
            error: (err) => {
                console.error(err);
            }
        });
    }

    fetchUsersByChallenges(): void {
        this.leaderboardsService.getTopUsersByChallenges('').subscribe({
            next: (data: any) => {
                this.rawUsersByChallenges = data;
                this.applyChallengeTypeFilter();
            },
            error: (err) => {
                console.error(err);
            }
        });
    }

    fetchPhotographersOfMonth(): void {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = currentDate.toLocaleString('en-US', { month: 'long' }).toUpperCase();
        this.leaderboardsService.getPhotographersOfMonth(year, month).subscribe({
            next: (data: any) => {
                this.photographersOfMonth = data.slice(0, 10);
            },
            error: (err) => {
                console.error(err);
            }
        });
    }

    fetchRisingStars(): void {
        this.leaderboardsService.getRisingStars().subscribe({
            next: (data: any) => {
                this.risingStars = data.slice(0, 10);
            },
            error: (err) => {
                console.error(err);
            }
        });
    }

    applyCountryFilter(): void {
        if (this.selectedCountry === 'all') {
            this.usersByPoints = [...this.rawUsersByPoints]
                .sort((a, b) => b.points - a.points)
                .slice(0, 10)
                .map((user, index) => ({ ...user, rank: index + 1 }));
        } else {
            this.usersByPoints = this.rawUsersByPoints
                .filter(user => user.country === this.selectedCountry)
                .slice(0, 10);
        }
    }

    applyChallengeTypeFilter(): void {
        if (this.selectedChallengeType === 'all') {
            this.usersByChallenges = this.rawUsersByChallenges
                .filter(user => user.challengeType === 'ALL')
                .sort((a, b) => b.numberOfWinChallenges - a.numberOfWinChallenges)
                .slice(0, 10)
                .map((user, index) => ({ ...user, rank: index + 1 }));
        } else {
            this.usersByChallenges = this.rawUsersByChallenges
                .filter(user => user.challengeType === this.selectedChallengeType)
                .slice(0, 10);
        }
    }
}
