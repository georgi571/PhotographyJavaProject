import {Component, OnInit} from '@angular/core';
import {FooterComponent} from "../../core/footer/footer.component";
import {HeaderComponent} from "../../core/header/header.component";
import {ReactiveFormsModule} from "@angular/forms";
import {LeaderboardsService} from '../../services/leaderboards-service/leaderboards.service';
import {forkJoin, map} from 'rxjs';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
  selector: 'app-rising-leaderboard',
    imports: [
        FooterComponent,
        HeaderComponent,
        ReactiveFormsModule
    ],
  templateUrl: './rising-leaderboard.component.html',
  styleUrl: './rising-leaderboard.component.css'
})
export class RisingLeaderboardComponent implements OnInit {
    risingStars: any[] = [];

    pageSize: number = 10;
    currentPage: number = 1;
    totalPagesNumber: number = 1;

    constructor(private leaderboardsService: LeaderboardsService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        this.fetchRisingStars();
    }

    fetchRisingStars(): void {
        this.leaderboardsService.getRisingStars().subscribe({
            next: (data: any) => {
                this.risingStars = data.slice(0, 10);

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
                        this.risingStars = usersWithDetails.map((user, index) => ({
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

    get paginatedUsers(): any[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.risingStars.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.risingStars.length / this.pageSize);
    }

    changePage(page: number): void {
        this.currentPage = page;
    }
}
