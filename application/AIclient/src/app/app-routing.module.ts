import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UserGuard} from './user.guard'
import {LoginGuard} from './login.guard'
import {PurchaseGuard} from './purchase.guard'
import {HomeComponent} from './home/home.component'
import {RegistrationComponent} from './registration/registration.component'
import {LoginComponent} from './login/login.component'
import {ProfileComponent} from './profile/profile.component'
import {PurchaseComponent} from './purchase/purchase.component'
import {SearchComponent} from './search/search.component'
import {SearchGuard} from './search.guard'
import {AdminComponent} from './admin/admin.component'
import {SellComponent} from './sell/sell.component'
import {SellGuard} from './sell.guard'
//configures routes in the system

const routes: Routes = [
  { path: 'home', component:HomeComponent},
  { path: 'registration', component:RegistrationComponent},
  { path: 'login', component:LoginComponent, canActivate: [LoginGuard]},
  { path: 'login/ADMIN/:id', component: AdminComponent,canActivate: [UserGuard] },
  { path: 'login/USER/:id', component: ProfileComponent,canActivate: [UserGuard] },
  { path: 'login/USER/:id/sell', component: SellComponent,canActivate: [SellGuard] },
  { path: 'login/USER/:id/purchase', component: PurchaseComponent,canActivate: [PurchaseGuard] },
  { path: 'search', component: SearchComponent, canActivate: [SearchGuard] },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'login/USER', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login/ADMIN', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
