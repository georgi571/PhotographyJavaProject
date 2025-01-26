import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsFollowersComponent } from './friends-followers.component';

describe('FriendsFollowersComponent', () => {
  let component: FriendsFollowersComponent;
  let fixture: ComponentFixture<FriendsFollowersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendsFollowersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendsFollowersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
