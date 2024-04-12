import {Component, OnDestroy, OnInit} from '@angular/core';
import {JhiEventManager} from '@ng-jhipster/service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {EvaluationResultService} from './evaluation-result.service';
import {AbstractCrud} from '../../../shared/crud/abstract-list.component';
import {IEvaluationResult} from '../../../shared/model/evaluation-result.model';
import {LanguageModelService} from '../../../entities/language-model/language-model.service';
import {Column, ExportColumn} from '../../../shared/model/export.model';

@Component({
  selector: 'genie-evaluation-results',
  templateUrl: './evaluation-results.component.html',
  styleUrls: ['./evaluation-results.component.scss']
})
export class EvaluationResultsComponent extends AbstractCrud<IEvaluationResult, number> implements OnInit, OnDestroy {

  data: any;
  options: any;

  cols!: Column[];
  exportColumns!: ExportColumn[];

  entities: IEvaluationResult[] = [];

  constructor(
    protected evaluationResultService: EvaluationResultService,
    protected languageModelService: LanguageModelService,
    protected confirmationService: ConfirmationService,
    protected messageService: MessageService,
    protected eventManager: JhiEventManager) {
    super(
      evaluationResultService,
      confirmationService,
      messageService,
      eventManager);
  }

  ngOnInit(): void {
    this.setupBarChartOptions();
    this.getEvaluationResults();

    this.cols = [
      { field: 'id', header: 'id'},
      { field: 'createdOn', header: 'Created On' },
      { field: 'evaluation.name', header: 'Name' },
      { field: 'languageModel.name', header: 'Model Name' },
      { field: 'keywordMatch', header: 'Keyword Match' },
      { field: 'similarityScore', header: 'Similarity Score' },
      { field: 'durationInMs', header: 'durationInMs' }
    ];

    this.exportColumns = this.cols.map((col) => ({ title: col.header, dataKey: col.field }));
  }

  private getEvaluationResults() {
    this.evaluationResultService
      .query()
      .subscribe((res) => {
        this.entities = res.body || [];
        this.mapDataToChartFormat();
      });
  }

  get eventNameToMonitor(): string {
    return 'evaluationListModification';
  }

  deleteAll(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete all selected items?',
      accept: () => {
        this.evaluationResultService.deleteAll().subscribe(() => {
          this.messageService.add({severity: 'success', summary: 'Deleted', detail: 'All results removed'});
          this.getEvaluationResults();
        });
      }
    });
  }

  getDurationInSeconds(item: IEvaluationResult): string {
    return item.durationInMs ? item.durationInMs / 1000 + 's' : '';
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
          stacked: false,
          type: 'linear',
          display: true,
          position: 'left',
          id: 'y-axis-time',
          ticks: {
            max: 100,
            min: 0,
            beginAtZero: true,
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

  mapDataToChartFormat(): void {
    if (this.entities) {
      // Sort by durationInMs
      const sortedResults = this.entities.sort((a, b) => {
        return (b.durationInMs || 0) - (a.durationInMs || 0);
      }).reverse();

      this.data = {
        labels: sortedResults.map(result => {
          if (result && result.languageModel) {
            return result.languageModel.name + ' (' + result.languageModel.modelType + ')' || 'Unknown';
          } else {
            return 'unknown';
          }
        }),
        datasets: [
          {
            type: 'bar',
            label: 'Duration in Seconds',
            backgroundColor: 'rgba(74,144,226,0.4)',
            borderColor: '#4A90E2',
            borderWidth: 1,
            hoverBackgroundColor: 'rgba(75,192,192,0.4)',
            hoverBorderColor: 'rgba(75,192,192,1)',
            data: sortedResults.map(result => (result.durationInMs || 0) / 1000)
          },
          {
            type: 'line',
            label: 'Similarity Score',
            backgroundColor: '#FF6B6B',
            borderColor: '#FF6B6B',
            borderWidth: 1,
            hoverBackgroundColor: 'black',
            hoverBorderColor: 'rgba(75,192,192,1)',
            data: sortedResults.map(result => (result.similarityScore || 0))
          },
          {
            type: 'line',
            label: 'Keyword Match',
            backgroundColor: '#7DBE8E',
            borderColor: '#7DBE8E',
            borderWidth: 1,
            hoverBackgroundColor: 'rgba(75,192,192,0.4)',
            hoverBorderColor: 'rgba(75,192,192,1)',
            data: sortedResults.map(result => (result.keywordMatch || 0))
          }
          ]
      };
    }
  }

}
