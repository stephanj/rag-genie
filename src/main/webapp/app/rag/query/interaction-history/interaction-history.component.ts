import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePipe, DecimalPipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { RouterLink } from '@angular/router';
import { AbstractCrud } from '../../../shared/crud/abstract-list.component';
import { IInteraction, Interaction } from '../../../shared/model/interaction.model';
import { JhiEventManager } from '@ng-jhipster/service';
import { InteractionService } from './interaction-history.service';
import { TooltipModule } from 'primeng/tooltip';
import {Column, ExportColumn} from '../../../shared/model/export.model';
import {DropdownModule} from 'primeng/dropdown';
import {AutoCompleteModule} from 'primeng/autocomplete';
import {ModelSelectItem} from '../../../shared/model/model-select-item.model';
import {LanguageModelService} from '../../../entities/language-model/language-model.service';
import {
  LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';

@Component({
  selector: 'genie-interaction-history',
  standalone: true,
  imports: [
    TableModule,
    InputTextareaModule,
    InputTextModule,
    FormsModule,
    ReactiveFormsModule,
    ToastModule,
    ConfirmDialogModule,
    DatePipe,
    DialogModule,
    RouterLink,
    TooltipModule,
    DecimalPipe,
    DropdownModule,
    AutoCompleteModule,
    LanguageModelDropdownComponent
  ],
  templateUrl: './interaction-history.component.html',
  styleUrl: './interaction-history.component.scss'
})
export class InteractionHistoryComponent extends AbstractCrud<Interaction, number> implements OnInit {

  interaction!: Interaction;

  cols!: Column[];
  exportColumns!: ExportColumn[];

  selectedModel: ModelSelectItem | null = null;

  constructor( protected interactionService: InteractionService,
               protected confirmationService: ConfirmationService,
               protected languageModelService: LanguageModelService,
               protected messageService: MessageService,
               protected eventManager: JhiEventManager) {
    super(interactionService, confirmationService, messageService, eventManager);
  }

  public loadAll() {
    if (this.selectedModel && this.selectedModel.id) {
      const req = {
        filterLanguageModelId: this.selectedModel?.id,
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      };

      super.loadAll(req);
    } else {
      super.loadAll();
    }
  }
  ngOnInit(): void {
    this.cols = [
      { field: 'id', header: 'id'},
      { field: 'languageModel.name', header: 'Model Name' },
      { field: 'languageModel.modelType', header: 'Model Provider' },
      { field: 'inputTokens', header: 'inputTokens' },
      { field: 'outputTokens', header: 'outputTokens' },
      { field: 'cost', header: 'cost' },
      { field: 'durationInMs', header: 'durationInMs' },
      { field: 'createdOn', header: 'createdOn' },
      { field: 'question', header: 'question'},
      { field: 'answer', header: 'answer'}
    ];

    this.exportColumns = this.cols.map((col) => ({ title: col.header, dataKey: col.field }));
  }

  get eventNameToMonitor(): string {
    return 'interactionListModification';
  }

  getDurationInSeconds(item: IInteraction): string {
    return item.durationInMs ? item.durationInMs / 1000 + 's' : '';
  }

  deleteInteraction(id: number): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this interaction?',
      accept: () => {
        this.interactionService.delete(id).subscribe(() => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Interaction deleted successfully' });
          this.eventManager.broadcast({
            name: 'interactionListModification',
            content: 'Deleted an interaction'
          });
        });
      }
    });
  }

  deleteAllInteractions(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete all interactions?',
      accept: () => {
        this.interactionService.deleteAll().subscribe(() => {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'All interactions deleted successfully' });
          this.eventManager.broadcast({
            name: 'interactionListModification',
            content: 'Deleted all interactions'
          });
        });
      }
    });
  }

  public selectedLanguageModel($event: ModelSelectItem | null): void {
    this.selectedModel = $event;
    this.loadAll();
  }
}
