import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient} from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { MediaComponent } from './components/media/media.component';
import { UploadMediaComponent } from './components/upload-media/upload-media.component';
import { EditMediaComponent } from './components/edit-media/edit-media.component';
import { ViewMediaComponent } from './components/view-media/view-media.component';
import { FormatEnumPipe } from "./pipes/format-enum.pipe";

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    MediaComponent,
    UploadMediaComponent,
    EditMediaComponent,
    ViewMediaComponent,
    FormatEnumPipe,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    FormsModule,
  ],
  providers: [
    provideHttpClient()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
