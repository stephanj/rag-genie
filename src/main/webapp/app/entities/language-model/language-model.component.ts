import {Component, OnDestroy, ViewChild} from '@angular/core';
import {JhiEventManager} from '@ng-jhipster/service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AbstractCrud} from '../../shared/crud/abstract-list.component';
import { LanguageModelService } from './language-model.service';
import { ILanguageModel } from '../../shared/model/language-model.model';
import { Table } from 'primeng/table';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import {Checkbox} from 'primeng/checkbox';

@Component({
  selector: 'genie-language-model',
  templateUrl: './language-model.component.html',
  styleUrls: ['./language-model.component.scss'],
})
export class LanguageModelComponent extends AbstractCrud<ILanguageModel, number> implements OnDestroy {

  itemsPerPage = 100;

  options: any;
  data: any;

  filterValue = '';
  costOnly = false;
  contextWindowOnly = false;
  commercialOnly = false;

  @ViewChild('checkbox') checkbox!: Checkbox;

  filterOptions: any[] = [];
  value: any;

  constructor(
    protected languageModelService: LanguageModelService,
    protected confirmationService: ConfirmationService,
    protected messageService: MessageService,
    protected eventManager: JhiEventManager) {
    super(
      languageModelService,
      confirmationService,
      messageService,
      eventManager);

    this.setupBarChartOptions();

    this.filterOptions = [
      { icon: 'pi pi-eye', name: 'all', description: 'Show all models' },
      { icon: 'pi pi-stop', name: 'context', description: 'Only show context window' },
      { icon: 'pi pi-dollar', name: 'cost', description: 'Only show cost' },
      { icon: 'pi pi-building', name: 'commercial', description: 'Only show commercial models which nee an API key'}
    ];
  }

  private setupBarChartOptions() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');

    this.options = {
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        tooltip: {
          mode: 'index',
          intersect: false
        },
        legend: {
          labels: {
            color: textColor
          }
        }
      },
      scales: {
        x: {
          stacked: true,
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
        y: {
          stacked: true,
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        }
      }
    };
  }

  mapDataToChartFormat(models: ILanguageModel[]): void {

    if (this.filterValue) {
      models = models.filter(model => model.name?.toLowerCase().includes(this.filterValue.toLowerCase()));
    }

    if (this.commercialOnly) {
      models = models.filter(model => model.apiKeyRequired);
    }

    if (this.costOnly) {
      // filter out models where costs are not defined
      models = models.filter(model => model.costInput1M !== undefined && model.costOutput1M !== undefined);

      // Sort by cost window
      models = models.sort((a, b) => {
        const totalCostA = (a.costInput1M || 0) + (a.costOutput1M || 0);
        const totalCostB = (b.costInput1M || 0) + (b.costOutput1M || 0);
        return totalCostA - totalCostB; // Sort models based on their total cost
      }).reverse();

    }  else if (this.contextWindowOnly) {
      // filter out models where costs and context window are not defined
      models = models.filter(model => model.contextWindow !== undefined && model.contextWindow > 0);

      // Sort by context window
      models = models.sort((a, b) => (a.contextWindow || 0) - (b.contextWindow || 0))
        .reverse();
    } else {
      // filter out models where costs and context window are not defined
      models = models.filter(model =>
        model.costInput1M !== undefined && model.costOutput1M !== undefined && model.contextWindow !== undefined);

      // Sort by context window
      models = models.sort((a, b) => (a.contextWindow || 0) - (b.contextWindow || 0))
                     .reverse();
    }

    this.data = {
      labels: models.map(model => this.getLabel(model) || 'Unknown Model'),
      datasets: [
        ...(this.costOnly ? [] : [{
          type: 'bar',
          label: 'Context Window',
          backgroundColor: 'yellow',
          data: models.map(model => model.contextWindow || 0),
        }]),
        ...(this.contextWindowOnly ? [] : [{
          type: 'bar',
          label: 'Cost Input per 1M Tokens',
          backgroundColor: 'blue', // Consider using CSS variables for colors
          data: models.map(model => model.costInput1M || 0)
        }]),
        ...(this.contextWindowOnly ? [] : [{
          type: 'bar',
          label: 'Cost Output per 1M Tokens',
          backgroundColor: 'green',
          data: models.map(model => model.costOutput1M || 0)
        }])
      ]
    }
  }

  private getLabel(model: ILanguageModel): string {
    if (this.filterValue) {
      return model.name + ' (' + model.modelType + ')' || 'Unknown Model';
    }
    return model.name || 'Unknown Model';
  }

  showFilterView($event: any): void {
    this.costOnly = $event.value.name === 'cost';
    this.contextWindowOnly = $event.value.name === 'context';
    this.commercialOnly = $event.value.name === 'commercial';
    this.mapDataToChartFormat(this.entities);
  }

  get eventNameToMonitor(): string {
    return 'languageModelListModification';
  }

  loadAll(): void {
    this.entityService
      .query({
        page: this.page,
        size: 100,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<Array<ILanguageModel>>) => {
          this.paginateEntities(res.body || [], res.headers);
          this.mapDataToChartFormat(res.body || []);
        },
        error: (res: HttpErrorResponse) => this.onError(res),
      });
  }

  /**
   * Load all models from Ollama when running on local machine
   * TODO: Only enable when we can check if running on localhost
   */
  getOllamaModels(): void {
    this.languageModelService.getOllamaModels().subscribe(() => {
      this.messageService.add({severity: 'success', summary: 'Success', detail: 'Ollama models loaded'});
      this.loadAll();
    });
  }

  onGlobalFilter(table: Table, event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value.toLowerCase();
    table.filterGlobal(this.filterValue, 'contains');

    this.mapDataToChartFormat(this.entities);
  }

  public onChange(event: any): void {
  }
}
