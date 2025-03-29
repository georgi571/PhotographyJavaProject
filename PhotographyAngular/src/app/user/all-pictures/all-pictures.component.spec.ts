import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllPicturesComponent } from './all-pictures.component';

describe('AllPicturesComponent', () => {
  let component: AllPicturesComponent;
  let fixture: ComponentFixture<AllPicturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllPicturesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllPicturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
