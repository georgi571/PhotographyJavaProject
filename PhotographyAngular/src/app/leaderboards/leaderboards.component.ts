import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../core/header/header.component";
import {FooterComponent} from "../core/footer/footer.component";
import {LeaderboardsService} from '../services/leaderboards-service/leaderboards.service';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';

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
    rawUsersByPoints: any[] = [];
    rawUsersByChallenges: any[] = [];
    usersByPoints: any[] = [];
    usersByChallenges: any[] = [];
    photographersOfMonth: any[] = [];
    activeUsers: any[] = [];
    risingStars: any[] = [];

    countries: string[] = [];
    challengeTypes: string[] = [];
    selectedCountry: string = 'all';
    selectedChallengeType: string = 'all';

    constructor(private leaderboardsService: LeaderboardsService) {}

    ngOnInit(): void {
        this.fetchUsersByPoints();
        this.fetchUsersByChallenges();
        this.fetchPhotographersOfMonth();
        this.fetchActiveUsers();
        this.fetchRisingStars();
        this.loadCountries();
        this.loadChallengeTypes();
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
        this.leaderboardsService.getPhotographersOfMonth().subscribe({
            next: (data: any) => {
                this.photographersOfMonth = data.slice(0, 10);
            },
            error: (err) => {
                console.error(err);
            }
        });
    }

    fetchActiveUsers(): void {
        this.leaderboardsService.getActiveUsers().subscribe({
            next: (data: any) => {
                this.activeUsers = data.slice(0, 10);
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
            this.usersByChallenges = [...this.rawUsersByChallenges]
                .sort((a, b) => b.challengesWon - a.challengesWon)
                .slice(0, 10)
                .map((user, index) => ({ ...user, rank: index + 1 }));
        } else {
            this.usersByChallenges = this.rawUsersByChallenges
                .filter(user => user.challengeType === this.selectedChallengeType)
                .slice(0, 10);
        }
    }
}
