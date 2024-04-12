import {Directive, ElementRef, OnInit} from '@angular/core';

@Directive({
  selector: '[genieGrabFocus]'
})
export class GrabFocusDirective implements OnInit{
  private element: HTMLInputElement;

  constructor(elementRef: ElementRef<HTMLInputElement>) {
    this.element = elementRef.nativeElement;
  }

  ngOnInit(): void {
    this.element.focus();
  }
}
