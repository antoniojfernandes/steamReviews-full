import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private criteriaSource = new BehaviorSubject([]);
  currentCriteria = this.criteriaSource.asObservable();

  changeCriteria(criteria: string[]) {
    // @ts-ignore
    this.criteriaSource.next(criteria);
  }
}
