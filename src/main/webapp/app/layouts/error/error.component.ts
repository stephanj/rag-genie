import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'genie-error',
  templateUrl: './error.component.html'
})
export class ErrorComponent implements OnInit {
  errorMessage?: string;
  error403 = false;
  error404 = false;
  error303 = false;
  platform: string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.platform = params.get('platform');
    });

    this.route.data.subscribe(routeData => {
      if (routeData.error303) {
        this.error303 = routeData.error303;
      }
      if (routeData.error403) {
        this.error403 = routeData.error403;
      }
      if (routeData.error404) {
        this.error404 = routeData.error404;
      }
      if (routeData.errorMessage) {
        this.errorMessage = routeData.errorMessage;
      }
    });
  }
}
