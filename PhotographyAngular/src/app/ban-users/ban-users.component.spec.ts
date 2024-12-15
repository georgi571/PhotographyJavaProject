import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BanUsersComponent } from './ban-users.component';

describe('BanUsersComponent', () => {
  let component: BanUsersComponent;
  let fixture: ComponentFixture<BanUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BanUsersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BanUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
