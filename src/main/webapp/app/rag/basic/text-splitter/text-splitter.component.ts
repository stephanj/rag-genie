import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { SplitterFormComponent } from '../../../shared/splitter-form/splitter-form.component';
import { Content, IContent } from '../../../shared/model/content.model';
import { DocumentResult } from '../../../shared/model/document-result.model';
import { SplitterInfo } from '../../../shared/model/splitter-info.model';
import { TextSplittingService } from '../../../shared/text-splitting/text-splitting.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DocumentService } from '../document/document.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import {Router} from '@angular/router';
import {IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

@Component({
  selector: 'genie-text-splitter',
  templateUrl: './text-splitter.component.html',
  styleUrl: './text-splitter.component.scss'
})
export class TextSplitterComponent implements OnInit, OnDestroy {

  @Input() summarizeEnabled = false;
  @Output() availableChunks = new EventEmitter<string[]>();
  @Output() contentSelected = new EventEmitter<IContent>();

  @ViewChild('contentList') contentListComponent!: ContentListComponent;
  @ViewChild('splitter') splitter!: SplitterFormComponent;

  contentSelection: Content[] = [];
  styledChunks : DocumentResult[] = [];
  chunks: string[] = [];

  saving = false;

  splitterPanelClosed = true;
  visualPanelClosed = true;
  displayStoreDialog = false;

  embeddingModels: IEmbeddingModel[] = [];
  selectedEmbedding!: IEmbeddingModel;

  // TODO move this to dedicated service
  styles = ['box1', 'box2', 'box3', 'box4', 'box5', 'box6'];
  stylesCounter = 0;

  maxChunkLength = 0;

  private readonly onDestroy = new Subject<void>();

  constructor(private textSplittingService: TextSplittingService,
              private vectorDocumentService: DocumentService,
              protected embeddingService: EmbeddingModelService,
              protected confirmationService: ConfirmationService,
              protected router: Router,
              protected messageService: MessageService) {}

  ngOnInit(): void {
    this.getSupportedEmbeddingModels();
  }

  ngOnDestroy(): void {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  getSupportedEmbeddingModels(): void {
    this.embeddingService
      .query()
      .subscribe((response) => {
        this.embeddingModels = response.body || [];
      });
  }

  splitText(splitterInfo: SplitterInfo): void {
    this.maxChunkLength = 0;
    if (splitterInfo) {
      this.textSplittingService
        .getChunks(splitterInfo)
        .subscribe(chunks => {
          this.chunks = chunks;
          this.getMaxChunkLength();

          this.styledChunks = chunks.map((text) => new DocumentResult('', text, this.getStyle(), 0));
          this.visualPanelClosed = false;
          if (this.summarizeEnabled) {
            this.availableChunks.emit(chunks);
          }
      })
    } else {
      console.error("Invalid form values");
    }
  }

  private getMaxChunkLength() {
    // get the max length chars of the chunks
    this.chunks.forEach(chunk => {
      if (chunk.length > this.maxChunkLength) {
        this.maxChunkLength = chunk.length;
      }
    });
  }

  onContentSelect(content: IContent): void {
    this.contentSelection = [];
    this.contentSelection.push(content);
    this.contentSelected.emit(content);
    this.splitterPanelClosed = false;
  }

  getPanelTitleWithChunkCount(): string {
    if (this.chunks && this.chunks.length > 0) {
      return 'Chunks (' + this.chunks.length + ')';
    }
    return 'Chunks';
  }

  public storeChunksInDB(): void {
    this.saving = true;
    this.displayStoreDialog = false;

    if (this.contentSelection.length === 0 || this.contentSelection[0].id === undefined) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'No content selected'});
      return;
    }

    if (this.selectedEmbedding === undefined || this.selectedEmbedding.id === undefined) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'No embedding model selected'});
      return;
    }

    this.vectorDocumentService.storeChunksWithRef(this.contentSelection[0].id, this.chunks, this.selectedEmbedding.id)
      .pipe(takeUntil(this.onDestroy))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Documents imported in vector database'
          });
          this.router.navigate(['/document']);
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to import documents in vector database'
          });
        }, complete: () => this.saving = false
      });
  }

  private getStyle(): string {
    if (this.stylesCounter === this.styles.length) {
      this.stylesCounter = 0;
    }
    return this.styles[this.stylesCounter++];
  }

  hasChunkWarning(): boolean {
    if (this.selectedEmbedding && this.selectedEmbedding.maxTokens) {
      return (this.maxChunkLength / 3) > this.selectedEmbedding.maxTokens;
    } else {
      return false;
    }
  }
}
