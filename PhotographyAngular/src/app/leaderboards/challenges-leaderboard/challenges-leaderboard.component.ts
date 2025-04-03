import {Component, OnInit} from '@angular/core';
import {FooterComponent} from "../../core/footer/footer.component";
import {HeaderComponent} from "../../core/header/header.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LeaderboardsService} from '../../services/leaderboards-service/leaderboards.service';
import {forkJoin, map} from 'rxjs';
import {ProfileService} from '../../services/profile-service/profile.service';

@Component({
  selector: 'app-challenges-leaderboard',
    imports: [
        FooterComponent,
        HeaderComponent,
        ReactiveFormsModule,
        FormsModule
    ],
  templateUrl: './challenges-leaderboard.component.html',
  styleUrl: './challenges-leaderboard.component.css'
})
export class ChallengesLeaderboardComponent implements OnInit{
    rawUsersByChallenges: any[] = [];
    usersByChallenges: any[] = [];

    challengeTypes: string[] = [];
    selectedChallengeType: string = 'all';

    pageSize: number = 10;
    currentPage: number = 1;
    totalPagesNumber: number = 1;

    constructor(private leaderboardsService: LeaderboardsService,
                private profileService: ProfileService) {}

    ngOnInit(): void {
        this.fetchUsersByChallenges();
        this.loadChallengeTypes();
    }

    loadChallengeTypes(): void {
        this.leaderboardsService.getChallengeTypes().subscribe((challengeTypes: string[]) => {
            this.challengeTypes = challengeTypes;
        });
    }

    fetchUsersByChallenges(): void {
        this.leaderboardsService.getTopUsersByChallenges('').subscribe({
            next: (data: any) => {
                this.rawUsersByChallenges = data;

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

                        this.rawUsersByChallenges = usersWithDetails.map((user, index) => ({
                            ...user,
                            rank: index + 1
                        }));

                        this.applyChallengeTypeFilter();
                    },
                    error: (err: any) => {
                        console.error('Error fetching user details:', err);
                    }
                });

                this.applyChallengeTypeFilter();
            },
            error: (err) => {
                console.error(err);
            }
        });
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
        this.totalPagesNumber = this.totalPages;
        this.currentPage = 1;
    }

    get paginatedUsers(): any[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.usersByChallenges.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.usersByChallenges.length / this.pageSize);
    }

    changePage(page: number): void {
        this.currentPage = page;
    }
}
