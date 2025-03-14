import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengesLeaderboardComponent } from './challenges-leaderboard.component';

describe('ChallengesLeaderboardComponent', () => {
  let component: ChallengesLeaderboardComponent;
  let fixture: ComponentFixture<ChallengesLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChallengesLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChallengesLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
