import {Component, OnInit} from '@angular/core';
import {JhiEventManager} from '@ng-jhipster/service';
import {ConfirmationService, MessageService} from 'primeng/api';
import { EmbeddingModelService } from './embedding-model.service';
import { ILanguageModel } from '../../shared/model/language-model.model';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import {IEmbeddingModel} from '../../shared/model/embedding-model.model';
import {AbstractCrud} from '../../shared/crud/abstract-list.component';

@Component({
  selector: 'genie-embedding-model',
  templateUrl: './embedding-model.component.html',
  styleUrls: ['./embedding-model.component.scss'],
})
export class EmbeddingModelComponent extends AbstractCrud<IEmbeddingModel, number> {

  sortField = 'id';
  reverse = 'asc';

  constructor(
    protected embeddingModelService: EmbeddingModelService,
    protected confirmationService: ConfirmationService,
    protected messageService: MessageService,
    protected eventManager: JhiEventManager) {
    super(embeddingModelService, confirmationService, messageService, eventManager);
  }

  public onChange(event: any): void {
  }

  get eventNameToMonitor(): string {
    return "embeddingModelListModification";
  }
}
