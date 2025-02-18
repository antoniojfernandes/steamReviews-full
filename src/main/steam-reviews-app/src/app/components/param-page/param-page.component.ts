import {Component} from '@angular/core';
import {Subscription} from 'rxjs';
import {DataService} from '../../services/data-service';
import {BackgroundComponent} from '../background/background.component';
import {NgClass, NgForOf} from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {ReviewService} from '../../services/review-service';

@Component({
  selector: 'app-param-page',
  imports: [
    BackgroundComponent,
    NgForOf,
    RouterLink,
    NgClass,
    FormsModule
  ],
  templateUrl: './param-page.component.html',
  standalone: true,
  styleUrl: './param-page.component.css'
})
export class ParamPageComponent {
  criteriaList: string[] = [];
  subscription: Subscription | undefined;
  gameName: string = "";
  gameId: number = 0;

  selectedFilter = 'all';
  filters = [
    {value: 'all', display: 'All'},
    {value: 'recent', display: 'Recent'},
    {value: 'updated', display: 'Updated'}
  ];

  selectedLang = "all";
  langs = [
    {value: 'all', display: 'All'},
    {value: 'english', display: 'English'},
  ];

  selectedreviewType = "all";
  reviewTypes = [
    {value: 'all', display: 'All'},
    {value: 'positive', display: 'Positive'},
    {value: 'negative', display: 'Negative'},
  ];
  pages: number = 1;
  minChars: number = 0;
  maxChars: number = 0;

  constructor(private dataService: DataService, private route: ActivatedRoute, private reviewDownloadService: ReviewService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.gameName = params['name'];
      this.gameId = params['id'];
    });
    this.subscription = this.dataService.currentCriteria.subscribe(criteria => this.criteriaList = criteria);
    this.criteriaList.push("Review")
    this.criteriaList.push("Language")
  }

  ngOnDestroy() {
    // @ts-ignore
    this.subscription.unsubscribe();
  }

  downloadFile() {
    if (this.gameName) {
      this.reviewDownloadService.downloadReviews(
        this.gameName,
        this.gameId,
        this.criteriaList,
        this.selectedLang,
        this.selectedFilter,
        this.selectedreviewType,
        this.minChars,
        this.maxChars,
        this.pages
      ).subscribe(
        (data) => {
          this.reviewDownloadService.handleDownload(data, "reviews.xlsx");
        },
        (error) => {
          console.error('Error downloading the file:', error);
          alert('An error occurred while downloading the file. Please try again.');
        }
      );
    } else {
      alert('Please provide a valid Game ID and number of pages.');
    }
  }
}
