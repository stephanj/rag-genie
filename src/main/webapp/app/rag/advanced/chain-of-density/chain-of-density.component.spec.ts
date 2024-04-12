import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChainOfDensityComponent } from './chain-of-density.component';

describe('RefinementComponent', () => {
  let component: ChainOfDensityComponent;
  let fixture: ComponentFixture<ChainOfDensityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChainOfDensityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChainOfDensityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
