import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { MediaComponent } from './components/media/media.component';
import { UploadMediaComponent } from './components/upload-media/upload-media.component';
import { MediaType, MediaStatus } from './models/media';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },

  { path: 'books',  component: MediaComponent, data: { mediaType: MediaType.BOOK } },
  { path: 'movies', component: MediaComponent, data: { mediaType: MediaType.MOVIE } },
  { path: 'tv',     component: MediaComponent, data: { mediaType: MediaType.TV } },
  { path: 'games',  component: MediaComponent, data: { mediaType: MediaType.GAME } },

  { path: 'add-book',  component: UploadMediaComponent, data: { mediaType: MediaType.BOOK } },
  { path: 'add-movie', component: UploadMediaComponent, data: { mediaType: MediaType.MOVIE } },
  { path: 'add-tv',    component: UploadMediaComponent, data: { mediaType: MediaType.TV } },
  { path: 'add-game',  component: UploadMediaComponent, data: { mediaType: MediaType.GAME } },

];
