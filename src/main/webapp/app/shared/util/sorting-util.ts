import { Injectable } from '@angular/core';

@Injectable()
export class Sorting {
  public static byName(values: any[]): any[] {
    return values.sort((t1: any, t2: any) => {
      if (t1.name == null || t2.name == null) {
        return t1.name == null ? -1 : 1;
      }
      if (t1.name.toLowerCase() > t2.name.toLowerCase()) {
        return 1;
      }
      if (t1.name.toLowerCase() < t2.name.toLowerCase()) {
        return -1;
      }
      return 0;
    });
  }
}
