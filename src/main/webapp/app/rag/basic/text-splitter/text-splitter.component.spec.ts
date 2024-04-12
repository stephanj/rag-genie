import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextSplitterComponent } from './text-splitter.component';

describe('TextSplitterComponent', () => {
  let component: TextSplitterComponent;
  let fixture: ComponentFixture<TextSplitterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextSplitterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TextSplitterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
