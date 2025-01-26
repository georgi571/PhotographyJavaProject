import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiveFriendRequestComponent } from './receive-friend-request.component';

describe('ReceiveFriendRequestComponent', () => {
  let component: ReceiveFriendRequestComponent;
  let fixture: ComponentFixture<ReceiveFriendRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceiveFriendRequestComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReceiveFriendRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
