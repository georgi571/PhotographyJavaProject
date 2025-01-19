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

    constructor(
        private challengeService: ChallengeService,
        private route: ActivatedRoute
    ) {
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
                (this.selectedStatus === 'active' && new Date(challenge.endAt) > now) ||
                (this.selectedStatus === 'past' && new Date(challenge.endAt) <= now);

            const isTypeMatch =
                this.selectedType === 'all' ||
                this.selectedType === challenge.type.toLowerCase();

            return isStatusMatch && isTypeMatch;
        });
    }
}
