import {Component} from '@angular/core';
import {Subscription} from 'rxjs';
import {DataService} from '../../services/data-service';
import {BackgroundComponent} from '../background/background.component';
import {CommonModule, NgClass, NgForOf} from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {ReviewService} from '../../services/review-service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-param-page',
  imports: [
    BackgroundComponent,
    RouterLink,
    NgClass,
    FormsModule,
    CommonModule,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
  ],
  templateUrl: './param-page.component.html',
  standalone: true,
  styleUrl: './param-page.component.css',
  animations: [
    trigger('transformPanel', [
      state('void', style({
        opacity: 1,
        transform: 'scale(0.95)',
        backgroundColor: 'white', // White background
      })),
      state('*', style({
        opacity: 1,
        transform: 'scale(1)',
        backgroundColor: 'white', // White background
      })),
      transition('void => *', animate('100ms ease-in')),
      transition('* => void', animate('100ms ease-out')),
    ]),
  ],
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

  selectedReviewType = "all";
  reviewTypes = [
    {value: 'all', display: 'All'},
    {value: 'positive', display: 'Positive'},
    {value: 'negative', display: 'Negative'},
  ];
  pages: number = 1;
  minChars: number = 0;
  maxChars: number = -1;
  startDate: Date = new Date();
  endDate: Date = new Date();

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
        this.selectedReviewType,
        this.minChars,
        this.maxChars,
        this.pages,
        this.startDate,
        this.endDate
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
