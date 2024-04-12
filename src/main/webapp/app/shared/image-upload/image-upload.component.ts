import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HttpEventType } from '@angular/common/http';
import { ImageService } from './image-upload.service';
import { SignedUrl } from '../model/signed-url.model';
import { throwError } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { FileSelectEvent } from 'primeng/fileupload/fileupload.interface';

@Component({
  selector: 'genie-image-upload',
  template: `
    <p-fileUpload
      mode="basic"
      id="field_imageUpload"
      chooseLabel="Choose image"
      [disabled]="uploading"
      [maxFileSize]="500000"
      [customUpload]="true"
      [multiple]="false"
      accept="image/*"
      (onSelect)="uploadImage($event)"
    >
    </p-fileUpload>
  `,
})
export class ImageUploadComponent {
  @Input() imageURL?: string;
  @Input() message?: string;
  @Output() upload: EventEmitter<any> = new EventEmitter();
  @Output() uploadError: EventEmitter<string> = new EventEmitter();

  uploading = false;
  fileToUpload?: File;

  constructor(protected imageService: ImageService) {}

  uploadImage(imgUploadEvent: FileSelectEvent): void {
    this.fileToUpload = imgUploadEvent.files[0];

    // Check if file size exceeds the limit
    if (this.fileToUpload && this.fileToUpload.size > 500000) {
      this.uploadError.next('The image file size should be less than 500KB.');
      return;
    }

    if (this.fileToUpload != null) {
      this.imageService
        .getSignedUrl(this.fileToUpload.name, this.fileToUpload.type)
        .pipe(
          mergeMap((resp: any) => {
            if (resp !== null && resp.body !== null) {
              this.uploading = true;
              const signedUrl: SignedUrl = resp.body;

              if (signedUrl.value && signedUrl.storedFileName) {
                return this.imageService.uploadMedia(signedUrl.value, this.fileToUpload).pipe(
                  map((uploadResponse: any) => {
                    return { signedUrl, event: uploadResponse };
                  })
                );
              }
            }
            return throwError('Error while generating link');
          })
        )
        .subscribe(
          ({ signedUrl, event }) => {
            switch (event.type) {
              case HttpEventType.Response:
                this.imageURL = signedUrl.storedFileName;
                this.uploading = false;
                this.fileToUpload = undefined;
                this.upload.emit(this.imageURL);
            }
          },
          error => {
            console.warn(error);
            this.uploadError.next('Something went wrong, please file an issue (see link in footer).');
          }
        );
    }
  }
}
