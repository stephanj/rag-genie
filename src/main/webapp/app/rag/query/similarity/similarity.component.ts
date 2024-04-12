import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { DocumentResult } from '../../../shared/model/document-result.model';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { DocumentService } from '../../basic/document/document.service';
import { SimilarityService } from './similarity.service';
import {IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

@Component({
  selector: 'genie-similarity',
  templateUrl: './similarity.component.html',
  styleUrl: './similarity.component.scss'
})
export class SimilarityComponent implements OnInit {

  chunks: DocumentResult[] = [];
  usedChunks: DocumentResult[] = [];

  embeddingModels: IEmbeddingModel[] = [];

  similarityPanelClosed = true;

  // TODO move this to dedicated service
  styles = ['box1', 'box2', 'box3', 'box4', 'box5', 'box6'];
  stylesCounter = 0;

  params: HttpParams = new HttpParams();
  answer = '';

  queryForm!: FormGroup;
  duration: Date = new Date();
  similarityDuration = 0;

  hasDocuments = false;
  rerank = false;

  constructor(private formBuilder: FormBuilder,
              protected messageService: MessageService,
              protected similarityService: SimilarityService,
              protected embeddingModelService: EmbeddingModelService,
              protected vectorDocumentService: DocumentService,
              protected confirmationService: ConfirmationService) {
    this.queryForm = this.formBuilder.group({
      query: new FormControl('', Validators.required),
      embeddingModel: new FormControl('', Validators.required),
      maxResults: new FormControl(5, Validators.required),
      minScore: new FormControl(60, Validators.required),
      rerankAnswers: new FormControl(false)
    });
  }

  ngOnInit(): void {
    this.hasUserDocuments();
    this.getEmbeddingModels();
  }

  getEmbeddingModels(): void {
    this.embeddingModelService.usedModels()
      .subscribe((res) => {
        if (res.body) {
          this.embeddingModels = res.body;
        }
      });
  }

  private hasUserDocuments() {
    this.vectorDocumentService.getTotalDocumentsForUser().subscribe({
      next: (res: any) => {
        this.hasDocuments = Number(res.headers.get('X-Total-Count') || '0') > 0;
      }
    });
  }

  doQuery(): void {
    this.params = new HttpParams()
      .set('question', this.queryForm.get('query')?.value)
      .set('embedId', this.queryForm.get('embeddingModel')?.value.id)
      .set('maxResults', this.queryForm.get('maxResults')?.value)
      .set('minScore', (this.queryForm.get('minScore')?.value) / 100)
      .set('rerank', this.queryForm.get('rerankAnswers')?.value);
    this.rerank = this.queryForm.get('rerankAnswers')?.value;
    this.similarityCall(this.params);
  }

  toggleRerank(): void {
    this.rerank = !this.rerank;
  }


  private similarityCall(params: HttpParams) {
    this.duration = new Date();
    this.similarityService.query(params).subscribe({
      next: (res: DocumentResult[]) => {
        if (res.length === 0) {
          this.usedChunks = [];
          this.messageService.add({ severity: 'info', summary: 'No results', detail: 'No similar documents found' });
          return;
        }
        this.similarityDuration = new Date().getTime() - this.duration.getTime();
        this.usedChunks = res.map((chunk) => ({
          id: chunk.id,
          content: chunk.content + '\n',
          style: this.getStyle(),
          score: chunk.score
        }));
        this.similarityPanelClosed = false;
      },
      error: (res: HttpErrorResponse) => this.messageService.add({ severity: 'error', summary: 'Error', detail: res.message })
    });
  }

  private getStyle(): string {
    if (this.stylesCounter === this.styles.length) {
      this.stylesCounter = 0;
    }
    return this.styles[this.stylesCounter++];
  }

  deleteDocument(documentId: string): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this vector item?',
      accept: () => {
        if (documentId !== undefined) {
          this.similarityService.delete(documentId).subscribe(() => {
            this.similarityCall(this.params);
          });
        }
      }
    });
  }
}
