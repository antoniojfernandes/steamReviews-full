import {Component} from '@angular/core';
import {BackgroundComponent} from "../background/background.component";
import {NgForOf} from "@angular/common";
import {ReviewService} from '../../services/review-service';
import {FormsModule} from '@angular/forms';
import {GameInfo} from '../../domain/GameInfo';
import {ActivatedRoute, RouterLink} from '@angular/router';

@Component({
  selector: 'app-select-page',
  imports: [
    BackgroundComponent,
    NgForOf,
    FormsModule,
    RouterLink
  ],
  templateUrl: './select-page.component.html',
  standalone: true,
  styleUrl: './select-page.component.css'
})
export class SelectPageComponent {
  isFocused = false;
  images: GameInfo[] = [];
  game: string = "";


  constructor(private reviewService: ReviewService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.game = params['name'];
      this.findGamesByName();
    });
  }

  findGamesByName() {
    if (this.game == undefined || this.game === '') {
      this.images = [];
      return;
    }
    this.reviewService.findGamesByName(this.game).subscribe({
      next: (games: GameInfo[]) => {
        // Collect image URLs into the array
        this.images = games;
      },
      error: (error) => {
        console.error('Error fetching games:', error);
      }
    });

  }
}
