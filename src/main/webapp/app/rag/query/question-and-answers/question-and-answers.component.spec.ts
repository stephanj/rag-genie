import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionAndAnswersComponent } from './question-and-answers.component';

describe('SimilarityComponent', () => {
  let component: QuestionAndAnswersComponent;
  let fixture: ComponentFixture<QuestionAndAnswersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuestionAndAnswersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuestionAndAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
