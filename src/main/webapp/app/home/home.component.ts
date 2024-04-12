import { Component } from '@angular/core';
import { faForward } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'genie-home-dashboard',
  templateUrl: './home.component.html'
})
export class HomeComponent {

  readonly faForward = faForward;

  firstSteps = [
    {
      label: "Add API Keys",
      routerLink: '/api-keys',
      disabled: false,
    },
    {
      label: "Import Data",
      routerLink: '/import',
      disabled: false,
    },
    {
      label: "Text Splitting",
      routerLink: '/text-splitter',
      disabled: true,
    },
    {
      label: "Similarity Search",
      routerLink: '/similarity',
      disabled: true,
    },
    {
      label: "Q&A",
      routerLink: '/questions',
      disabled: true,
    }
  ];

  constructor() {}

}
