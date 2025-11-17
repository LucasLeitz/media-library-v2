import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { MediaComponent } from './components/media/media.component';
import { EditMediaComponent } from "./components/edit-media/edit-media.component";
import { UploadMediaComponent } from './components/upload-media/upload-media.component';
import {MediaStatus, MediaType} from './models/media';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },

  { path: 'books',              component: MediaComponent, data: { mediaType: MediaType.BOOK,  status: MediaStatus.COMPLETED } },
  { path: 'books/backlog',      component: MediaComponent, data: { mediaType: MediaType.BOOK,  status: MediaStatus.BACKLOG } },
  { path: 'books/in-progress',  component: MediaComponent, data: { mediaType: MediaType.BOOK,  status: MediaStatus.IN_PROGRESS } },

  { path: 'movies',             component: MediaComponent, data: { mediaType: MediaType.MOVIE, status: MediaStatus.COMPLETED } },
  { path: 'movies/backlog',     component: MediaComponent, data: { mediaType: MediaType.MOVIE, status: MediaStatus.BACKLOG } },
  { path: 'movies/in-progress', component: MediaComponent, data: { mediaType: MediaType.MOVIE, status: MediaStatus.IN_PROGRESS } },

  { path: 'tv',                 component: MediaComponent, data: { mediaType: MediaType.TV,    status: MediaStatus.COMPLETED } },
  { path: 'tv/backlog',         component: MediaComponent, data: { mediaType: MediaType.TV,    status: MediaStatus.BACKLOG } },
  { path: 'tv/in-progress',     component: MediaComponent, data: { mediaType: MediaType.TV,    status: MediaStatus.IN_PROGRESS } },

  { path: 'games',              component: MediaComponent, data: { mediaType: MediaType.GAME,  status: MediaStatus.COMPLETED } },
  { path: 'games/backlog',      component: MediaComponent, data: { mediaType: MediaType.GAME,  status: MediaStatus.BACKLOG } },
  { path: 'games/in-progress',  component: MediaComponent, data: { mediaType: MediaType.GAME,  status: MediaStatus.IN_PROGRESS } },

  { path: 'add', component: UploadMediaComponent },
  { path: 'edit-media/:id', component: EditMediaComponent },

];
