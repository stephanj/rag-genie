import { Component, Input, Output } from '@angular/core';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { Content } from '../model/content.model';
import { SplitterInfo } from '../model/splitter-info.model';

@Component({
  selector: 'genie-splitter-form',
  standalone: true,
  imports: [
    InputTextareaModule,
    InputTextModule,
    FormsModule,
    ReactiveFormsModule,
    DatePipe,
    RouterLink,
    ButtonModule,
    InputNumberModule,
    DropdownModule,
    ToastModule
  ],
  templateUrl: './splitter-form.component.html'
})
export class SplitterFormComponent {
  @Input() showSplitButton = true;
  @Input() contentSelection : Content[] = []

  @Output() splitText = new Subject<SplitterInfo>();

  splitterForm!: FormGroup;

  chunkSize = 550;
  chunkOverlap = 25;
  splitterInfo!: SplitterInfo;

  showChunkSizing = false;
  showRegValue = false;

  splitterOptions = [
    { label: 'Character', value: 'CHARACTER', description: 'Split text into characters'},
    { label: 'Word', value: 'WORD', description: 'Split text into words'},
    { label: 'Sentence', value: 'SENTENCE', description: 'Split text into sentences'},
    { label: 'Line', value: 'LINE', description: 'Split text by line breaks'},
    { label: 'Paragraph', value: 'PARAGRAPH', description: 'Split text into paragraphs'},
    { label: 'Recursive', value: 'RECURSIVE', description: 'Split text recursively into words, sentences, lines and paragraphs'},
    { label: 'JSON', value: 'JSON', description: 'Split text by JSON fields'},
    { label: 'Regex', value: 'REGEX', description: 'Split text using a regular expression'},
    { label: 'TODO: getSeparatorsForLanguage(xxx)', value: 'CODE', description: 'Split code by language for example Java'},
    { label: 'TODO: Semantic', value: 'SEMANTIC', description: 'Split text using semantic analysis'},
  ];

  constructor(private formBuilder: FormBuilder,
              private messageService: MessageService) {
    this.splitterForm = this.formBuilder.group({
      strategy: new FormControl('', Validators.required),
      chunkSize: new FormControl(this.chunkSize, [Validators.min(1)]),
      chunkOverlap: new FormControl(this.chunkOverlap, [Validators.min(0)]),
      regValue: new FormControl('')
    });
  }

  changeStrategy($event: any): void {
    this.showChunkSizing =
      $event.value === 'CHARACTER' ||
      $event.value === 'WORD' ||
      $event.value === 'SENTENCE' ||
      $event.value === 'PARAGRAPH' ||
      $event.value === 'LINE' ||
      $event.value === 'RECURSIVE' ||
      $event.value === 'REGEX';

    this.showRegValue = $event.value === 'REGEX';
  }

  handleSplitText(): void {
    this.splitterInfo = new SplitterInfo();

    if (this.contentSelection && this.contentSelection.length > 0) {
      this.splitterInfo.contentIds = [];
      this.contentSelection.map(content => {
        if (content.id) {
          this.splitterInfo.contentIds?.push(content.id);
        }
      });
    }

    this.splitterInfo.strategy = this.splitterForm.get('strategy')?.value;
    this.splitterInfo.chunkSize = this.splitterForm.get('chunkSize')?.value;
    this.splitterInfo.chunkOverlap = this.splitterForm.get('chunkOverlap')?.value;
    this.splitterInfo.value = this.splitterForm.get('regValue')?.value;

    alert('Splitting text with the following parameters: ' + JSON.stringify(this.splitterInfo));

    if (!this.contentSelection) {
      this.messageService.add({severity: 'error',
        summary: 'First select a document',
        sticky: true,
        detail: 'No content selected'});
    }

    if (this.splitterInfo.chunkOverlap > this.splitterInfo.chunkSize) {
      this.messageService.add({severity: 'error',
        summary: 'Error in text sizing',
        sticky: true,
        detail: 'Chunk overlap cannot be greater than text size'});
    }

    this.splitText.next(this.splitterInfo);
  }
}
