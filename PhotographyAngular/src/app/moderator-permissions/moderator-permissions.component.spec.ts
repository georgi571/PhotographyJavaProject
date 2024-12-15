import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModeratorPermissionsComponent } from './moderator-permissions.component';

describe('ModeratorPermissionsComponent', () => {
  let component: ModeratorPermissionsComponent;
  let fixture: ComponentFixture<ModeratorPermissionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModeratorPermissionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModeratorPermissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
