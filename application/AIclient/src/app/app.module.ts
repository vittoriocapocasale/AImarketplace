import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpClientModule, HTTP_INTERCEPTORS, HttpClientXsrfModule} from '@angular/common/http'
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MapComponent } from './map/map.component';
import { HomeComponent } from './home/home.component';
import { SearchComponent } from './search/search.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { ProfileComponent } from './profile/profile.component';
import { PurchaseComponent } from './purchase/purchase.component';
import { FormsModule }   from '@angular/forms';
import {LoginGuard} from './login.guard'
import {UserGuard} from './user.guard'
import {IdentityService} from './identity.service'
import {PositionService} from './position.service'
import {TokenInterceptor} from './token.interceptor'
import { ReactiveFormsModule } from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatToolbarModule} from '@angular/material/toolbar'
import { MatDividerModule, MatGridListModule, MatCardModule, MatDialogModule, MatInputModule, MatTableModule, MatMenuModule,MatIconModule,
   MatProgressSpinnerModule,MatFormFieldModule,} from '@angular/material';
import { SearchGuard } from './search.guard';
import { UpformComponent } from './upform/upform.component';
import { AdminComponent } from './admin/admin.component';
import {ScrollingModule} from '@angular/cdk/scrolling';
import { FuploaderComponent } from './fuploader/fuploader.component';
import { SellComponent } from './sell/sell.component';
import { PurchaseGuard } from './purchase.guard';
import {SellGuard} from './sell.guard';
import { ItemComponent } from './item/item.component'
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { ChartComponent } from './chart/chart.component';
import {ChartsModule} from 'ng2-charts';
import { AngularDateTimePickerModule } from 'angular2-datetimepicker';
import {MatBadgeModule} from '@angular/material/badge';
import { UrlService } from './url.service';
@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    HomeComponent,
    SearchComponent,
    LoginComponent,
    RegistrationComponent,
    ProfileComponent,
    PurchaseComponent,
    UpformComponent,
    AdminComponent,
    FuploaderComponent,
    SellComponent,
    ItemComponent,
    ChartComponent
  ],
  imports: [
    LeafletModule.forRoot(),
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    ScrollingModule,
    ChartsModule,
    AngularDateTimePickerModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatSidenavModule,
    MatCheckboxModule,
    MatListModule,
    MatToolbarModule,
    MatCardModule,
    MatDialogModule,
    MatInputModule,
    MatTableModule,
    MatMenuModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatGridListModule,
    MatDividerModule,
    MatBadgeModule
  ],
  providers: [
    UrlService,
    IdentityService,
    PositionService,
    LoginGuard,
    PurchaseGuard,
    SearchGuard,
    SellGuard,
    UserGuard,
    {provide: HTTP_INTERCEPTORS,useClass: TokenInterceptor,multi: true, deps: [IdentityService]}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
