import { Component, OnInit } from '@angular/core';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Content, IContent } from '../../../shared/model/content.model';
import { DatePipe } from '@angular/common';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { GenieSharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'genie-import-content',
  standalone: true,
  imports: [
    InputTextareaModule,
    InputTextModule,
    FormsModule,
    ReactiveFormsModule,
    DatePipe,
    ScrollPanelModule,
    ButtonModule,
    ConfirmDialogModule,
    ToastModule,
    GenieSharedModule
  ],
  templateUrl: './content-update.component.html'
})
export class UpdateContentComponent implements OnInit {

  content!: Content;
  contentForm!: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private http: HttpClient,
              private route: ActivatedRoute,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService) {

    this.contentForm = this.formBuilder.group({
      createdOn: new FormControl('', Validators.required),
      name: new FormControl('', Validators.required),
      description: new FormControl(''),
      source: new FormControl('', Validators.required),
      contentType: new FormControl('', Validators.required),
      value: new FormControl('', Validators.required),
      userId: new FormControl(''),
      tokenCount: new FormControl(''),
    });
  }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        // Adjust the URL as needed for your API endpoint
        return this.http.get<IContent>(`api/content/${id}`);
      })
    ).subscribe({
      next: (content: IContent) => {
        this.content = content;
        this.updateForm(content);
      },
      error: (error) => {
        console.error('Error fetching content:', error);
      }
    });
  }

  protected updateForm(content: IContent): void {
    this.contentForm.patchValue({
      createdOn: content.createdOn,
      name: content.name,
      description: content.description,
      source: content.source,
      contentType: content.contentType,
      value: content.value,
      userId: content.userId,
      tokenCount: content.tokenCount
    });
  }

  createFromForm(): IContent {
    return {
      ...new Content(),
      id: this.content.id,
      createdOn: this.contentForm.get(['createdOn'])!.value,
      name: this.contentForm.get(['name'])!.value,
      description: this.contentForm.get(['description'])!.value,
      source: this.contentForm.get(['source'])!.value,
      contentType: this.contentForm.get(['contentType'])!.value,
      value: this.contentForm.get(['value'])!.value,
      userId: this.contentForm.get(['userId'])!.value,
      tokenCount: this.contentForm.get(['tokenCount'])!.value
    };
  }

  updateContent(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to update this content?',
      accept: () => {
        this.content = this.createFromForm();
        this.subscribeToSaveResponse(this.http.put<IContent>('api/content', this.content));
      }
    });
  }

  subscribeToSaveResponse(result: any): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      (res: HttpErrorResponse) => this.onSaveError(res)
    );
  }

  onSaveSuccess(): void {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Content updated successfully' });
    window.history.back();
  }

  onSaveError(res: HttpErrorResponse): void {
    this.messageService.add({ severity: 'error', summary: 'Error', detail: res.message });
  }

  previousPage(): void {
    window.history.back();
  }
}
