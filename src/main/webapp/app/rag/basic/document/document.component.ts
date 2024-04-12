import { Component } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DocumentService } from './document.service';
import { AbstractCrud } from '../../../shared/crud/abstract-list.component';
import { JhiEventManager } from '@ng-jhipster/service';
import { IDocument } from '../../../shared/model/document.model';

@Component({
  selector: 'genie-document',
  templateUrl: './document.component.html',
  styleUrl: './document.component.scss'
})
export class DocumentComponent extends AbstractCrud<IDocument, string> {

  constructor(protected documentService: DocumentService,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService,
              protected eventManager: JhiEventManager) {
    super(documentService, confirmationService, messageService, eventManager);
  }

  get eventNameToMonitor(): string {
    return "vectorDocumentListModification";
  }

}
