import {Component, OnInit} from '@angular/core';
import {FooterComponent} from "../../core/footer/footer.component";
import {HeaderComponent} from "../../core/header/header.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LeaderboardsService} from '../../services/leaderboards-service/leaderboards.service';
import {forkJoin, map} from 'rxjs';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
  selector: 'app-points-leaderboard',
    imports: [
        FooterComponent,
        HeaderComponent,
        ReactiveFormsModule,
        FormsModule
    ],
  templateUrl: './points-leaderboard.component.html',
  styleUrl: './points-leaderboard.component.css'
})
export class PointsLeaderboardComponent implements OnInit {
    rawUsersByPoints: any[] = [];
    usersByPoints: any[] = [];

    countries: string[] = [];
    selectedCountry: string = 'all';

    pageSize: number = 10;
    currentPage: number = 1;
    totalPagesNumber: number = 1;

    constructor(private leaderboardsService: LeaderboardsService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        this.fetchUsersByPoints();
        this.loadCountries();
    }

    loadCountries(): void {
        this.leaderboardsService.getCountries().subscribe((countries: string[]) => {
            this.countries = countries;
        });
    }

    fetchUsersByPoints(): void {
        this.leaderboardsService.getTopUsersByPoints('').subscribe({
            next: (data: any) => {
                this.rawUsersByPoints = data;

                const userRequests = data.map((user: any) =>
                    this.profileService.getUserById(user.userId).pipe(
                        map((userData: any) => ({
                            ...user,
                            username: userData.username,
                            profilePic: userData.profilePic,
                        }))
                    )
                );

                forkJoin<any[]>(userRequests).subscribe({
                    next: (usersWithDetails: any[]) => {

                        this.rawUsersByPoints = usersWithDetails.map((user, index) => ({
                            ...user,
                            rank: index + 1
                        }));

                        this.applyCountryFilter();
                    },
                    error: (err: any) => {
                        console.error('Error fetching user details:', err);
                    }
                });

                this.applyCountryFilter();
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

        this.totalPagesNumber = this.totalPages;
        this.currentPage = 1;
    }

    get paginatedUsers(): any[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.usersByPoints.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.usersByPoints.length / this.pageSize);
    }

    changePage(page: number): void {
        this.currentPage = page;
    }
}
