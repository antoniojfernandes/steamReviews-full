import {Component} from '@angular/core';
import {CommonModule,} from '@angular/common';
import {BackgroundComponent} from '../background/background.component';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {DataService} from '../../services/data-service';

@Component({
  selector: 'app-filter-page',
  imports: [CommonModule, BackgroundComponent, RouterLink],
  templateUrl: './filter-page.component.html',
  standalone: true,
  styleUrl: './filter-page.component.css'
})
export class FilterPageComponent {
  gameName: string = "";
  gameId: number = 0;

  //TODO need a better way to send this to backend
  criteriaList: string[] = [
    "Author's playtime forever", "Author's playtime at review", "Author's last played", "Author's playtime last two weeks",
    "Review creation date", "Review update date", "Review voted up", "Votes funny", "Review votes up", "Helpfulness score",
    "Reviewer purchased the game on Steam", "Reviewer got the game for free", "Review during early access",
    "Developer's response", "Developer's response date", "Does the reviewer play mostly on steam deck"
  ]

  constructor(private route: ActivatedRoute, private dataService: DataService) {
  }

  selectedCriteria: string[] = [];

  toggleSelection(criteria: string) {
    if (this.isSelected(criteria)) {
      this.selectedCriteria = this.selectedCriteria.filter(item => item !== criteria);
    } else {
      this.selectedCriteria.push(criteria);
    }
  }

  isSelected(criteria: string): boolean {
    return this.selectedCriteria.includes(criteria);
  }

  allSelected(): boolean {
    return this.selectedCriteria === this.criteriaList;
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.gameName = params['name'];
      this.gameId = params['id'];
    });
  }

  move() {
    if (this.gameName == undefined || this.gameName === '') {
      return;
    }
    this.dataService.changeCriteria(this.selectedCriteria);
  }

  selectAll() {
    if (this.allSelected()) {
      this.selectedCriteria = []
    } else {
      this.selectedCriteria = this.criteriaList;
    }
  }
}
