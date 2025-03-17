import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportPicturesComponent } from './report-pictures.component';

describe('ReportPicturesComponent', () => {
  let component: ReportPicturesComponent;
  let fixture: ComponentFixture<ReportPicturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportPicturesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportPicturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
