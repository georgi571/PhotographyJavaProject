import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonthLeaderboardComponent } from './month-leaderboard.component';

describe('MonthLeaderboardComponent', () => {
  let component: MonthLeaderboardComponent;
  let fixture: ComponentFixture<MonthLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonthLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonthLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
