import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'genie-menu',
  templateUrl: './genie-menu.component.html'
})
export class GenieMenuComponent implements OnInit {
  model: MenuItem[] = [];

  constructor() {
  }

  ngOnInit() {
    this.updateMenu();
  }

  public updateMenu(): void {

    this.model.push({
      items: [
        {
          label: 'Dashboard',
          icon: 'pi pi-fw pi-home',
          routerLink: ['/']
        },
        {
          label: 'API Keys',
          icon: 'pi pi-key',
          routerLink: ['/api-keys']
        },
        {
          label: 'Language Models',
          icon: 'pi pi-language',
          routerLink: ['/language-model']
        },
        {
          label: 'Embedding Models',
          icon: 'pi pi-box',
          routerLink: ['/embedding-model']
        }
      ]
    });

    this.model.push({
      label: 'Basic RAG Steps',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Import Data',
          icon: 'pi pi-fw pi-file-import',
          routerLink: ['/import']
        },
        {
          label: 'Text Splitting',
          icon: 'pi pi-fw pi-copy',
          routerLink: ['/text-splitter']
        },
        {
          label: 'Vector Documents',
          icon: 'pi pi-fw pi-database',
          routerLink: ['/document']
        }
      ]
    });

    this.model.push({
      label: 'Query Documents',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Similarity Search',
          icon: 'pi pi-angle-double-right',
          routerLink: ['/similarity']
        },
        {
          label: 'Q & A',
          icon: 'pi pi-question',
          routerLink: ['/questions']
        },
        {
          label: 'ChatBot',
          icon: 'pi pi-comments',
          routerLink: ['/chatbot']
        }
      ]
    });

    this.model.push({
      label: 'Document Enrichment',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Summarize Documents',
          icon: 'pi pi-fw pi-list',
          routerLink: ['/summary']
        },
        {
          label: 'Chain of Density (CoD)',
          icon: 'pi pi-fw pi-copy',
          routerLink: ['/chain-of-density']
        }
      ]
    });

    this.model.push({
      label: 'Evaluations',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Eval Questions',
          icon: 'pi pi-angle-double-right',
          routerLink: ['/evaluation-questions']
        },
        {
          label: 'Eval Results',
          icon: 'pi pi-percentage',
          routerLink: ['/evaluation-results']
        }

      ]
    });

    this.model.push({
      label: 'History',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Interactions',
          icon: 'pi pi-fw pi-history',
          routerLink: ['/interaction-history']
        }
      ]
    });

    this.model.push({
      label: 'Experimental Agents',
      icon: 'pi pi-th-large',
      items: [
        {
          label: 'Agent Researcher',
          icon: 'fa-solid fa-flask',
          routerLink: ['/agent-researcher']
        }
      ]
    });
  }
}
