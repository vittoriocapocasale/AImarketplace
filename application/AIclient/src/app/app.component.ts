import { Component, OnInit, OnDestroy } from '@angular/core';
import { UrlService } from './url.service';
import { IdentityService } from './identity.service';
import { User } from './user';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent  implements OnInit, OnDestroy{

  role:string;
  userId:string;
  title = 'Data Marketplace';
  open:boolean=false;
  userLogged:boolean=false;
  userAdminLogged:boolean=false;
  private $user:Subscription=null;

  //initializes system
  constructor(private urlService:UrlService, private identityService:IdentityService){
  }
  ngOnInit()
  {
    this.urlService.loadConfiguration().subscribe(resp=>{
      this.urlService.setEnvironment(resp);
      this.urlService.updateUrls();
    });
    this.role=this.identityService.getCurrentUserRole();
    this.userId=this.identityService.getCurrentUsername();
    this.userAdminLogged=this.identityService.isUserLogged();
    this.userLogged=this.identityService.getCurrentUserRole()=="USER";
    this.$user=this.identityService.getUserChanges().subscribe(
      e=>{this.userAdminLogged=this.identityService.isUserLogged(); this.userLogged=this.identityService.getCurrentUserRole()=="USER";this.role=this.identityService.getCurrentUserRole(); this.userId=this.identityService.getCurrentUsername();});

  }
  ngOnDestroy()
  {
    this.$user.unsubscribe();
  }
     showSideNav():void
    {
      this.open=!this.open;
    }

    logout():void
    {
      this.identityService.logout();
      this.urlService.goToHome();
    }
}
