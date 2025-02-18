import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {saveAs} from 'file-saver';
import {GameInfo} from '../domain/GameInfo';


@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  constructor(private http: HttpClient) {
  }

  downloadReviews(gameName: string,
                  gameId: number,
                  criteriaList: string[],
                  selectedLang: string,
                  selectedFilter: string,
                  selectedreviewType: string,
                  minChars: number,
                  maxChars: number,
                  pages: number): Observable<Blob> {
    const criteriaListStr = criteriaList.join(',')
    const params = new HttpParams()
      .set('gameName', gameName)
      .set('gameId', gameId)
      .set('criteriaList', criteriaListStr)
      .set('selectedLang', selectedLang)
      .set('selectedFilter', selectedFilter)
      .set('selectedreviewType', selectedreviewType)
      .set('minChars', minChars)
      .set('maxChars', maxChars)
      .set('pages', pages);

    return this.http.get('http://localhost:8080/download-reviews', {
      responseType: 'blob',
      params: params
    });
  }

  findGamesByName(game: string): Observable<GameInfo[]> {
    const params = new HttpParams().set('game', game);

    return this.http.get<GameInfo[]>('http://localhost:8080/find-game-by-name', {params})
      .pipe(
        map(response => {
          return response;
        })
      );
  }

  handleDownload(data: Blob, fileName: string) {
    saveAs(data, fileName);
  }
}
