import {Component, OnInit} from '@angular/core';
import {FooterComponent} from "../../core/footer/footer.component";
import {HeaderComponent} from "../../core/header/header.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LeaderboardsService} from '../../services/leaderboards-service/leaderboards.service';
import {ProfileService} from '../../services/profile-service/profile.service';
import {forkJoin, map} from 'rxjs';

@Component({
  selector: 'app-month-leaderboard',
    imports: [
        FooterComponent,
        HeaderComponent,
        ReactiveFormsModule,
        FormsModule
    ],
  templateUrl: './month-leaderboard.component.html',
  styleUrl: './month-leaderboard.component.css'
})
export class MonthLeaderboardComponent implements OnInit {

    photographersOfMonth: any[] = [];
    years: number[] = [];
    months: string[] = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];
    selectedYear!: number;
    selectedMonth!: string;

    pageSize: number = 10;
    currentPage: number = 1;
    totalPagesNumber: number = 1;

    constructor(private leaderboardsService: LeaderboardsService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        const currentYear = new Date().getFullYear();
        this.years = Array.from({ length: currentYear - 2025 + 1 }, (_, i) => 2025 + i);

        this.selectedYear = currentYear;
        this.selectedMonth = this.months[new Date().getMonth()];
        this.fetchPhotographersOfMonth();
    }

    fetchPhotographersOfMonth(): void {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = currentDate.toLocaleString('en-US', { month: 'long' }).toUpperCase();
        this.leaderboardsService.getPhotographersOfMonth(year, month).subscribe({
            next: (data: any) => {
                this.photographersOfMonth = data.slice(0, 10);

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

                        this.photographersOfMonth = usersWithDetails.map((user, index) => ({
                            ...user,
                            rank: index + 1
                        }));
                    },
                    error: (err: any) => {
                        console.error('Error fetching user details:', err);
                    }
                });
            },
            error: (err) => {
                console.error(err);
            }
        });
        this.totalPagesNumber = this.totalPages;
        this.currentPage = 1;
    }

    fetchLeaderboard(): void {
        this.leaderboardsService.getPhotographersOfMonth(this.selectedYear, this.selectedMonth).subscribe({
            next: (data: any) => {
                this.photographersOfMonth = data.slice(0, 10);
            },
            error: (err) => {
                console.error(err);
            }
        });
        this.totalPagesNumber = this.totalPages;
        this.currentPage = 1;
    }

    get paginatedUsers(): any[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.photographersOfMonth.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.photographersOfMonth.length / this.pageSize);
    }

    changePage(page: number): void {
        this.currentPage = page;
    }
}
