import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllFollowersComponent } from './all-followers.component';

describe('AllFollowersComponent', () => {
  let component: AllFollowersComponent;
  let fixture: ComponentFixture<AllFollowersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllFollowersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllFollowersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
