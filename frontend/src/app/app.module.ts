import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient} from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { MediaComponent } from './components/media/media.component';
import { UploadMediaComponent } from './components/upload-media/upload-media.component';
import { EditMediaComponent } from './components/edit-media/edit-media.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MediaComponent,
    UploadMediaComponent,
    EditMediaComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
  ],
  providers: [
    provideHttpClient()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
