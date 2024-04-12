import { Component } from '@angular/core';
import { faBug, faCloud, faCopyright } from '@fortawesome/free-solid-svg-icons';
import { VERSION, GIT_COMMIT_ID } from '../../app.constants';

@Component({
  selector: 'genie-footer',
  templateUrl: './genie-footer.component.html',
  styleUrls: ['./genie-footer.component.scss'],
})
export class GenieFooterComponent {
  readonly faBug = faBug;
  readonly faCopyright = faCopyright;

  version: string;
  git_sha1: string;
  currentYear = new Date().getFullYear();

  constructor() {
    this.version = VERSION ? 'v' + VERSION : '';
    this.git_sha1 = GIT_COMMIT_ID ? '(#' + GIT_COMMIT_ID + ')' : '';
  }

  protected readonly faCloud = faCloud;
}
