import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { FileUploadModule } from 'primeng/fileupload';
import { TabViewModule } from 'primeng/tabview';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpParams } from '@angular/common/http';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { Content, ContentType } from '../../../shared/model/content.model';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { Router, RouterLink } from '@angular/router';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { IRestEndpoint, RestEndpoint } from '../../../shared/model/restendpoint.model';
import { CheckboxModule } from 'primeng/checkbox';
import { ImportService } from './import.service';
import {SwaggerField} from '../../../shared/model/swagger-field.model';
import {TooltipModule} from 'primeng/tooltip';
import {InputNumberModule} from 'primeng/inputnumber';
import {SearchQuery} from '../../../shared/model/search-query.model';

@Component({
  selector: 'genie-import-content',
  standalone: true,
  imports: [
    TableModule,
    FileUploadModule,
    TabViewModule,
    InputTextareaModule,
    InputTextModule,
    FormsModule,
    ReactiveFormsModule,
    ToastModule,
    ConfirmDialogModule,
    DatePipe,
    DialogModule,
    ScrollPanelModule,
    RouterLink,
    ContentListComponent,
    CheckboxModule,
    TooltipModule,
    InputNumberModule
  ],
  templateUrl: './import.component.html',
  styleUrl: './import.component.scss'
})
export class ImportComponent {

  content!: Content;
  entities!: Content[];

  selectedRestEndpoint!: RestEndpoint;
  selectedRestFields: SwaggerField[] = [];
  restTemplate = '';
  restUrl = '';
  restEndpoints: RestEndpoint[] = [];
  restEndpointFields: SwaggerField[] = [];
  showFieldsDialog = false;

  textForm!: FormGroup;
  urlForm!: FormGroup;
  searchForm!: FormGroup;

  totalItems = 0;
  page = 0;
  itemsPerPage = 10;

  importing = false;
  gettingFields = false;

  private urlRegex = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w.-]+)+[\w\-._~:/?#[\]@!$&'()*+,;=]+$/;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private importContentService: ImportService,
              protected messageService: MessageService) {

    this.textForm = this.formBuilder.group({
      name: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]),
      value: new FormControl('', [Validators.required, Validators.minLength(3)])
    });

    this.urlForm = this.formBuilder.group({
      name: new FormControl('', Validators.required),
      source: new FormControl('', [Validators.required, Validators.minLength(7), Validators.maxLength(255), Validators.pattern(this.urlRegex)]),
    });

    this.searchForm = this.formBuilder.group({
      search: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]),
      total: new FormControl(3, [Validators.min(1), Validators.max(10)]),
    });
  }

  uploadText() {
    if (this.textForm.valid) {
      this.importing = true;
      const content = new Content();
      content.name = this.textForm.get('name')?.value;
      content.source = 'user-input';
      content.description = this.textForm.get('description')?.value;
      content.contentType = ContentType.TEXT;
      content.value = this.textForm.get('value')?.value;

      this.importContentService.uploadText(content)
        .subscribe(() => {
          this.importing = false;
          this.router.navigate(['/text-splitter']);
        });
    }
  }

  uploadUrl() {
    if (this.urlForm.valid) {
      this.urlForm.disable();
      this.importing = true;
      const content = new Content();
      content.name = this.urlForm.get('name')?.value;
      content.source = this.urlForm.get('source')?.value;
      content.description = this.urlForm.get('description')?.value;
      content.contentType = ContentType.HTML;
      content.value = '';

      this.importContentService.uploadUrl(content)
        .subscribe(() => {
          this.importing = false;
          this.router.navigate(['/text-splitter']);
        });
    }
  }

  searchQuery() {
    if (this.searchForm.valid) {
      this.searchForm.disable();
      this.importing = true;

      const searchQuery =
        new SearchQuery(
              this.searchForm.get('search')?.value,
              this.searchForm.get('total')?.value
        );

      this.importContentService.search(searchQuery)
        .subscribe(() => {
          this.importing = false;
          this.router.navigate(['/text-splitter']);
        });
    }
  }

  uploadRestUrl() {
    if (this.urlForm.valid) {
      this.urlForm.disable();
      this.importing = true;
      let source = this.urlForm.get('source')?.value;

      // Remove the trailing slash from the URL
      if (source.endsWith('/')) {
        source = source.substring(0, source.length - 1);
      }
      this.restUrl = source;
      this.importContentService.getUrlMeta(new HttpParams().set('url', source))
        .subscribe({
          next: (res: IRestEndpoint[]) => {
            this.restEndpoints = res;
            this.urlForm.enable();
          },
          error: () => {
            this.messageService.add({ severity: 'error', detail: 'Invalid Open API URL'});
            this.importing = false
            this.urlForm.enable();
          },
          complete: () => this.importing = false
        });
    }
  }

  uploadGitHubProject() {
    if (this.urlForm.valid) {
      this.urlForm.disable();
      this.importing = true;
      const content = new Content();
      content.name = this.urlForm.get('name')?.value;
      content.source = this.urlForm.get('source')?.value;
      content.contentType = ContentType.CODE;
      content.value = '';

      this.importContentService.getGitHub(content)
        .subscribe(() => {
          this.importing = false;
          this.router.navigate(['/text-splitter']);
        });
    }
  }

  uploadGitHubProjectFile(event: any): void {
    this.urlForm.disable();

    const file: File = event.files[0];

    const formData = new FormData();
    formData.append('multipartFile', file);

    this.importContentService.uploadFile(formData).subscribe(() => {
      this.router.navigate(['/content']);
    });
  }

  /**
   * Get the documents for the selected REST endpoint(s)
   */
  createDocumentsFromRest(): void {
    this.showFieldsDialog = false;

    const content = new Content();
    content.name = this.selectedRestEndpoint.path
    content.source = this.restUrl + this.selectedRestEndpoint.path;
    content.description = "REST endpoint: " + this.selectedRestEndpoint.path;
    content.fields = this.selectedRestFields;
    content.contentType = ContentType.JSON;
    content.restTemplate = this.restTemplate;

    this.importContentService.uploadRestData(content)
      .subscribe(() => {
        this.router.navigate(['/text-splitter']);
      });
  }

  onRestTableRowSelect(event: any) {
    this.gettingFields = true;
    const params = new HttpParams()
      .set("url", this.restUrl)
      .set("endpoint", event.data.path);

    this.importContentService.getUrlFields(params)
      .subscribe( (res: SwaggerField[]) => {
        this.restEndpointFields = res;
        this.showFieldsDialog = true;
        this.gettingFields = false;
      })

  }

  addSelectedField(event: any) {
    this.selectedRestFields.push(event.data);
  }

  uploadFile(event: any): void {
    const file: File = event.files[0];

    const formData = new FormData();
    formData.append('multipartFile', file);

    this.importContentService.uploadFile(formData).subscribe(() => {
      this.router.navigate(['/text-splitter']);
    });
  }
}
