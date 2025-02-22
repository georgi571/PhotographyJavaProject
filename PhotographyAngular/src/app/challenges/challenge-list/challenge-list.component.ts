import {Component, OnInit} from '@angular/core';
import {ChallengeService} from '../../services/challenge-service/challenge.service';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HeaderComponent} from '../../core/header/header.component';
import {FooterComponent} from '../../core/footer/footer.component';

@Component({
    selector: 'app-challenge-detail',
    standalone: true,
    imports: [
        DatePipe,
        FormsModule,
        HeaderComponent,
        FooterComponent,
        RouterLink
    ],
    templateUrl: './challenge-list.component.html',
    styleUrl: './challenge-list.component.css'
})
export class ChallengeListComponent implements OnInit {
    allChallenges: any[] = [];
    filteredChallenges: any[] = [];

    selectedStatus: string = 'all';
    selectedType: string = 'all';

    showCreateModal: boolean = false;
    newChallenge: any = {
        title: '',
        description: '',
        details: '',
        startAt: '',
        endAt: '',
        type: ''
    };

    tomorrow: string;

    constructor(
        private challengeService: ChallengeService,
        private route: ActivatedRoute
    ) {
        const tomorrowDate = new Date();
        tomorrowDate.setDate(tomorrowDate.getDate() + 1);
        this.tomorrow = tomorrowDate.toISOString().split('T')[0];
    }

    ngOnInit(): void {
        this.route.queryParams.subscribe((params) => {
            if (params['status']) {
                this.selectedStatus = params['status'];
            }
            if (params['type']) {
                this.selectedType = params['type'];
            }

            this.challengeService.getAllChallenges().subscribe((challenges) => {
                this.allChallenges = challenges;
                this.filteredChallenges = challenges;
                this.filterChallenges();
            });
        });
    }

    filterChallenges(): void {
        this.filteredChallenges = this.allChallenges.filter((challenge) => {
            const now = new Date();

            const isStatusMatch =
                this.selectedStatus === 'all' ||
                (this.selectedStatus === 'active' && challenge.activity.toLowerCase() === 'active') ||
                (this.selectedStatus === 'upcoming' && challenge.activity.toLowerCase() === 'upcoming') ||
                (this.selectedStatus === 'past' && challenge.activity.toLowerCase() === 'past');

            const isTypeMatch =
                this.selectedType === 'all' ||
                this.selectedType === challenge.type.toLowerCase();

            return isStatusMatch && isTypeMatch;
        });
    }

    openCreateModal(): void {
        this.showCreateModal = true;
    }

    closeCreateModal(): void {
        this.showCreateModal = false;
    }

    submitCreate(): void {
        this.challengeService.createChallenge(this.newChallenge).subscribe({
            next: () => {
                alert('Challenge created successfully!');
                this.showCreateModal = false;
                this.challengeService.getAllChallenges().subscribe((challenges) => {
                    this.allChallenges = challenges;
                    this.filterChallenges();
                });
            },
            error: (error) => {
                console.error('Error creating challenge:', error);
                alert('Failed to create challenge.');
            }
        });
    }
}
