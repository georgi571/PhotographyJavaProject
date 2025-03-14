import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PointsLeaderboardComponent } from './points-leaderboard.component';

describe('PointsLeaderboardComponent', () => {
  let component: PointsLeaderboardComponent;
  let fixture: ComponentFixture<PointsLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PointsLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PointsLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
