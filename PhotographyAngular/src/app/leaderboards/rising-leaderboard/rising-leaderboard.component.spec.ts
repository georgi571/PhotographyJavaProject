import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RisingLeaderboardComponent } from './rising-leaderboard.component';

describe('RisingLeaderboardComponent', () => {
  let component: RisingLeaderboardComponent;
  let fixture: ComponentFixture<RisingLeaderboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RisingLeaderboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RisingLeaderboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
